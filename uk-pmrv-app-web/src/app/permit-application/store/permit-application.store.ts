import { Inject, Injectable } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import {
  distinctUntilChanged,
  filter,
  first,
  interval,
  map,
  Observable,
  pluck,
  skipWhile,
  Subscription,
  switchMap,
  switchMapTo,
  tap,
} from 'rxjs';

import { LOCAL_STORAGE } from '@core/services/local-storage';
import { Store } from '@core/store';
import { requestTaskReassignedError, taskNotFoundError } from '@shared/errors/concurrency-error';
import { FileUpload } from '@shared/file-input/file-upload-event';

import {
  Permit,
  PermitContainer,
  PermitIssuanceReviewDecision,
  PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload,
  PermitVariationDetails,
  PermitVariationReviewDecision,
  RequestTaskActionPayload,
  RequestTaskActionProcessDTO,
  TasksService,
} from 'pmrv-api';

import { catchTaskReassignedBadRequest } from '../../error/business-errors';
import { ConcurrencyErrorService } from '../../error/concurrency-error/concurrency-error.service';
import { catchNotFoundRequest, ErrorCode } from '../../error/not-found-error';
import { findReviewGroupByTaskKey, mandatoryGroups } from '../review/review';
import { MainTaskKey, Path, StatusKey, TaskKey } from '../shared/types/permit-task.type';
import { initialState, PermitApplicationState } from './permit-application.state';

type MasterTaskKey = 'monitoringApproaches';
export type SubtaskKey = Permit[MasterTaskKey][string]['type'];
export type PermitTaskSave<K extends MainTaskKey> = {
  key: K;
  value: K extends keyof Permit ? Permit[K] : undefined;
  attachments?: FileUpload[];
  isCompleted?: boolean[];
  subtaskKey?: SubtaskKey;
};

@Injectable({ providedIn: 'root' })
export class PermitApplicationStore extends Store<PermitApplicationState> {
  private storage$ = interval(2000).pipe(
    switchMapTo(this),
    pluck('requestTaskId'),
    skipWhile((requestTaskId) => !requestTaskId),
    map((requestTaskId) => this.localStorage.getItem(`permit/${requestTaskId}`)),
    distinctUntilChanged(),
    filter((state) => state && state !== JSON.stringify(this.getState())),
    map((state) => JSON.parse(state)),
  );
  private localStorageSubscription: Subscription;

  constructor(
    private readonly tasksService: TasksService,
    private readonly router: Router,
    private readonly concurrencyErrorService: ConcurrencyErrorService,
    @Inject(LOCAL_STORAGE) private readonly localStorage: Storage,
  ) {
    super(initialState);
  }

  get payload() {
    return this.getValue();
  }

  get permit(): Permit {
    return this.payload.permit;
  }

  get permitType(): PermitContainer['permitType'] {
    return this.payload.permitType;
  }

  get reviewGroupDecisions(): { [key: string]: PermitIssuanceReviewDecision } {
    return this.getValue().reviewGroupDecisions;
  }

  listenToStorage(): void {
    this.localStorageSubscription = this.storage$.subscribe((state) => this.setState(state));
  }

  stopListeningToStorage(): void {
    this.localStorageSubscription.unsubscribe();
  }

  setState(state: PermitApplicationState): void {
    if (state.requestTaskId) {
      this.localStorage.setItem(`permit/${state.requestTaskId}`, JSON.stringify(state));
    }
    super.setState(state);
  }

  get isEditable$(): Observable<boolean> {
    return this.pipe(pluck('isEditable'));
  }

  getFileUploadSectionAttachmentActionContext(): RequestTaskActionProcessDTO['requestTaskActionType'] {
    return this.getValue().isVariation
      ? 'PERMIT_VARIATION_UPLOAD_SECTION_ATTACHMENT'
      : 'PERMIT_ISSUANCE_UPLOAD_SECTION_ATTACHMENT';
  }

  getFileUploadReviewGroupDecisionAttachmentActionContext(): RequestTaskActionProcessDTO['requestTaskActionType'] {
    return this.getValue().isVariation
      ? 'PERMIT_VARIATION_UPLOAD_REVIEW_GROUP_DECISION_ATTACHMENT'
      : 'PERMIT_ISSUANCE_UPLOAD_REVIEW_GROUP_DECISION_ATTACHMENT';
  }

  getDownloadUrlFiles(files: string[]): { downloadUrl: string; fileName: string }[] {
    const url = this.createBaseFileDownloadUrl();
    return (
      files?.map((id) => ({
        downloadUrl: url + `${id}`,
        fileName: this.getState().permitAttachments[id],
      })) ?? []
    );
  }

  createBaseFileDownloadUrl() {
    return this.getState().actionId
      ? `/permit-application/action/${this.getState().actionId}/file-download/`
      : `/permit-application/${this.getState().requestTaskId}/file-download/`;
  }

  navigate(route: ActivatedRoute, target: string, reviewGroupUrl: string): void {
    let url = target;

    if (this.getValue().requestTaskType === 'PERMIT_ISSUANCE_APPLICATION_REVIEW') {
      url = `../review/${reviewGroupUrl}`;
    }

    this.router.navigate([url], { relativeTo: route, state: { notification: true } });
  }

  getTask<K extends keyof Permit>(permitTask: K): Observable<Permit[K]> {
    return this.pipe(pluck('permit', permitTask), distinctUntilChanged());
  }

  findTask<T = any, P extends Path<Permit> = any>(path: P): Observable<T> {
    return this.pipe(pluck('permit', ...path.split('.'))) as Observable<T>;
  }

  patchTask(taskKey: TaskKey, value: any, statusValue?: boolean | boolean[], statusKey?: StatusKey) {
    return this.updateTask(taskKey, value, 'PATCH', statusValue, statusKey);
  }

  postTask(taskKey: TaskKey, value: any, statusValue?: boolean | boolean[], statusKey?: StatusKey) {
    return this.updateTask(taskKey, value, 'POST', statusValue, statusKey);
  }

  postReview(
    value: PermitIssuanceReviewDecision | PermitVariationReviewDecision,
    groupKey: PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload['group'],
    files?: { uuid: string; file: File }[],
  ) {
    return this.pipe(
      first(),
      switchMap((state) => {
        const reviewGroups = mandatoryGroups.concat(
          Object.keys(state.permit.monitoringApproaches) as Array<
            PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload['group']
          >,
        );

        const areAllOtherReviewGroupsAccepted =
          reviewGroups
            .filter((mg) => mg !== groupKey)
            .every((mg) => state.reviewGroupDecisions[mg]?.type === 'ACCEPTED') &&
          (state.isVariation ? state.permitVariationDetailsReviewDecision?.type === 'ACCEPTED' : true);

        const shouldResetDetermination = (state) =>
          (state.determination?.type === 'GRANTED' && value.type !== 'ACCEPTED') ||
          (state.determination?.type === 'REJECTED' && value.type === 'ACCEPTED' && areAllOtherReviewGroupsAccepted);

        return this.postReviewGroupDecision(
          {
            ...state,
            reviewGroupDecisions: {
              ...state.reviewGroupDecisions,
              [groupKey]: value,
            },
            reviewSectionsCompleted: {
              ...state.reviewSectionsCompleted,
              [groupKey]: true,
              ...(shouldResetDetermination(state) ? { determination: false } : null),
            },
            reviewAttachments: {
              ...state.reviewAttachments,
              ...files?.reduce((result, item) => ({ ...result, [item.uuid]: item.file.name }), {}),
            },
            determination: shouldResetDetermination(state) ? {} : state.determination,
          },
          groupKey,
        );
      }),
    );
  }

  postStatus(statusKey: StatusKey, statusValue: boolean | boolean[]) {
    return this.pipe(
      first(),
      switchMap((state) =>
        this.postState({
          ...state,
          permitSectionsCompleted: {
            ...state.permitSectionsCompleted,
            [statusKey]: Array.isArray(statusValue) ? statusValue : [statusValue],
          },
        }),
      ),
    );
  }

  postDetermination(value: any, status: boolean): Observable<any> {
    return this.tasksService
      .processRequestTaskActionUsingPOST({
        requestTaskActionType: 'PERMIT_ISSUANCE_SAVE_REVIEW_DETERMINATION',
        requestTaskId: this.getState().requestTaskId,
        requestTaskActionPayload: {
          payloadType: 'PERMIT_ISSUANCE_SAVE_REVIEW_DETERMINATION_PAYLOAD',
          determination: value,
          reviewSectionsCompleted: {
            ...this.getState().reviewSectionsCompleted,
            determination: status,
          },
        } as RequestTaskActionPayload,
      })
      .pipe(
        catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
          this.concurrencyErrorService.showErrorForceNavigation(taskNotFoundError),
        ),
        catchTaskReassignedBadRequest(() =>
          this.concurrencyErrorService.showErrorForceNavigation(requestTaskReassignedError()),
        ),
        tap(() =>
          this.setState({
            ...this.getState(),
            determination: value,
            reviewSectionsCompleted: {
              ...this.getState().reviewSectionsCompleted,
              determination: status,
            },
          }),
        ),
      );
  }

  postCategoryTask(
    taskKey: TaskKey,
    state: PermitApplicationState,
    reviewSectionsCompleted?: { [key: string]: boolean },
  ) {
    return this.postState({
      ...state,
      ...(reviewSectionsCompleted
        ? { reviewSectionsCompleted: reviewSectionsCompleted }
        : {
            reviewSectionsCompleted: {
              ...state.reviewSectionsCompleted,
              ...(findReviewGroupByTaskKey(taskKey) ? { [findReviewGroupByTaskKey(taskKey)]: false } : {}),
              ...(state?.determination?.type !== 'DEEMED_WITHDRAWN' ? { determination: false } : null),
            },
          }),
    });
  }

  postVariationDetails(permitVariationDetails: PermitVariationDetails, permitVariationDetailsCompleted = false) {
    return this.pipe(
      first(),
      switchMap((state) =>
        this.postState({
          ...state,
          permitVariationDetails,
          permitVariationDetailsCompleted,
          permitVariationDetailsReviewCompleted: false,
          reviewSectionsCompleted: {
            ...state.reviewSectionsCompleted,
            ...(state?.determination?.type !== 'DEEMED_WITHDRAWN' ? { determination: false } : null),
          },
        }),
      ),
    );
  }

  private postReviewGroupDecision(
    state: PermitApplicationState,
    groupKey: PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload['group'],
  ) {
    return this.tasksService
      .processRequestTaskActionUsingPOST({
        requestTaskActionType: state.isVariation
          ? 'PERMIT_VARIATION_SAVE_REVIEW_GROUP_DECISION'
          : 'PERMIT_ISSUANCE_SAVE_REVIEW_GROUP_DECISION',
        requestTaskId: state.requestTaskId,
        requestTaskActionPayload: {
          payloadType: state.isVariation
            ? 'PERMIT_VARIATION_SAVE_REVIEW_GROUP_DECISION_PAYLOAD'
            : 'PERMIT_ISSUANCE_SAVE_REVIEW_GROUP_DECISION_PAYLOAD',
          group: groupKey,
          decision: state.reviewGroupDecisions[groupKey],
          reviewSectionsCompleted: state.reviewSectionsCompleted,
        } as RequestTaskActionPayload,
      })
      .pipe(
        catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
          this.concurrencyErrorService.showErrorForceNavigation(taskNotFoundError),
        ),
        catchTaskReassignedBadRequest(() =>
          this.concurrencyErrorService.showErrorForceNavigation(requestTaskReassignedError()),
        ),
        tap(() => this.setState(state)),
      );
  }

  private updateTask(
    taskKey: TaskKey,
    value: any,
    updateType: 'PATCH' | 'POST',
    statusValue?: boolean | boolean[],
    statusKey?: StatusKey,
  ) {
    return this.pipe(
      first(),
      switchMap((state) =>
        this.postState({
          ...state,
          permit: {
            ...state.permit,
            ...taskKey
              .split('.')
              .reverse()
              .reduce(
                (taskTree, taskTreeKey, index, array) => ({
                  [taskTreeKey]:
                    updateType === 'POST' && index === 0
                      ? taskTree
                      : {
                          ...taskKey
                            .split('.')
                            .slice(0, array.length - index)
                            .reduce((target, targetKey) => (target = target[targetKey]), state.permit),
                          ...taskTree,
                        },
                }),
                value,
              ),
          },
          permitSectionsCompleted: {
            ...state.permitSectionsCompleted,
            ...(statusKey
              ? { [statusKey]: Array.isArray(statusValue) ? statusValue : [statusValue] }
              : statusValue !== undefined
              ? { [taskKey]: Array.isArray(statusValue) ? statusValue : [statusValue] }
              : null),
          },
          reviewSectionsCompleted: {
            ...state.reviewSectionsCompleted,
            ...(findReviewGroupByTaskKey(taskKey) ? { [findReviewGroupByTaskKey(taskKey)]: false } : {}),
            ...(state?.determination?.type !== 'DEEMED_WITHDRAWN' ? { determination: false } : null),
          },
        }),
      ),
    );
  }

  private postState(state: PermitApplicationState) {
    return this.tasksService
      .processRequestTaskActionUsingPOST({
        requestTaskActionType:
          state.requestTaskType === 'PERMIT_ISSUANCE_APPLICATION_REVIEW'
            ? 'PERMIT_ISSUANCE_SAVE_APPLICATION_REVIEW'
            : state.requestTaskType === 'PERMIT_ISSUANCE_APPLICATION_AMENDS_SUBMIT'
            ? 'PERMIT_ISSUANCE_SAVE_APPLICATION_AMEND'
            : state.requestTaskType === 'PERMIT_VARIATION_OPERATOR_APPLICATION_SUBMIT'
            ? 'PERMIT_VARIATION_OPERATOR_SAVE_APPLICATION'
            : state.requestTaskType === 'PERMIT_VARIATION_APPLICATION_REVIEW'
            ? 'PERMIT_VARIATION_SAVE_APPLICATION_REVIEW'
            : 'PERMIT_ISSUANCE_SAVE_APPLICATION',
        requestTaskId: state.requestTaskId,
        requestTaskActionPayload: {
          payloadType:
            state.requestTaskType === 'PERMIT_ISSUANCE_APPLICATION_REVIEW'
              ? 'PERMIT_ISSUANCE_SAVE_APPLICATION_REVIEW_PAYLOAD'
              : state.requestTaskType === 'PERMIT_ISSUANCE_APPLICATION_AMENDS_SUBMIT'
              ? 'PERMIT_ISSUANCE_SAVE_APPLICATION_AMEND_PAYLOAD'
              : state.requestTaskType === 'PERMIT_VARIATION_OPERATOR_APPLICATION_SUBMIT'
              ? 'PERMIT_VARIATION_OPERATOR_SAVE_APPLICATION_PAYLOAD'
              : state.requestTaskType === 'PERMIT_VARIATION_APPLICATION_REVIEW'
              ? 'PERMIT_VARIATION_SAVE_APPLICATION_REVIEW_PAYLOAD'
              : 'PERMIT_ISSUANCE_SAVE_APPLICATION_PAYLOAD',
          permit: state.permit,
          permitSectionsCompleted: state.permitSectionsCompleted,
          ...([
            'PERMIT_ISSUANCE_APPLICATION_REVIEW',
            'PERMIT_ISSUANCE_APPLICATION_AMENDS_SUBMIT',
            'PERMIT_VARIATION_OPERATOR_APPLICATION_SUBMIT',
            'PERMIT_VARIATION_APPLICATION_REVIEW',
          ].includes(state.requestTaskType)
            ? { reviewSectionsCompleted: state.reviewSectionsCompleted }
            : null),
          ...(!['PERMIT_VARIATION_OPERATOR_APPLICATION_SUBMIT', 'PERMIT_VARIATION_APPLICATION_REVIEW'].includes(
            state.requestTaskType,
          ) && {
            permitType: state.permitType,
          }),
          ...(state.isVariation && {
            permitVariationDetails: state.permitVariationDetails ?? {},
            permitVariationDetailsCompleted: state.permitVariationDetailsCompleted ?? false,
          }),
          ...(state.isVariation &&
            state.requestTaskType === 'PERMIT_VARIATION_APPLICATION_REVIEW' && {
              permitVariationDetailsReviewCompleted: state.permitVariationDetailsReviewCompleted,
            }),
        } as RequestTaskActionPayload,
      })
      .pipe(
        catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
          this.concurrencyErrorService.showErrorForceNavigation(taskNotFoundError),
        ),
        catchTaskReassignedBadRequest(() =>
          this.concurrencyErrorService.showErrorForceNavigation(requestTaskReassignedError()),
        ),
        tap(() =>
          this.setState({
            ...state,
            ...(state.requestTaskType === 'PERMIT_ISSUANCE_APPLICATION_REVIEW' &&
            state?.reviewSectionsCompleted?.determination &&
            state?.determination?.type !== 'DEEMED_WITHDRAWN'
              ? { determination: {} }
              : null),
          }),
        ),
      );
  }
}
