import { Inject, Injectable } from '@angular/core';

import {
  distinctUntilChanged,
  filter,
  interval,
  map,
  Observable,
  pluck,
  skipWhile,
  Subscription,
  switchMapTo,
  tap,
} from 'rxjs';

import {
  PermitSaveCessationRequestTaskActionPayload,
  PermitSurrender,
  PermitSurrenderApplicationSubmitRequestTaskPayload,
  PermitSurrenderReviewDecision,
  PermitSurrenderSaveApplicationRequestTaskActionPayload,
  PermitSurrenderSaveReviewDeterminationRequestTaskActionPayload,
  PermitSurrenderSaveReviewGroupDecisionRequestTaskActionPayload,
  RequestTaskActionEmptyPayload,
  TasksService,
} from 'pmrv-api';

import { LOCAL_STORAGE } from '../../core/services/local-storage';
import { Store } from '../../core/store';
import { catchTaskReassignedBadRequest } from '../../error/business-errors';
import { ConcurrencyErrorService } from '../../error/concurrency-error/concurrency-error.service';
import { catchNotFoundRequest, ErrorCode } from '../../error/not-found-error';
import { requestTaskReassignedError, taskNotFoundError } from '../../shared/errors/concurrency-error';
import { initialState, PermitSurrenderState } from './permit-surrender.state';

const localStorageItemCode = 'permit-surrender';

@Injectable({ providedIn: 'root' })
export class PermitSurrenderStore extends Store<PermitSurrenderState> {
  private localStorageSubscription: Subscription;

  private storage$ = interval(2000).pipe(
    switchMapTo(this),
    pluck('requestTaskId'),
    skipWhile((requestTaskId) => !requestTaskId),
    map((requestTaskId) => this.localStorage.getItem(`${localStorageItemCode}/${requestTaskId}`)),
    distinctUntilChanged(),
    filter((state) => state && state !== JSON.stringify(this.getState())),
    map((state) => JSON.parse(state)),
  );

  constructor(
    private readonly tasksService: TasksService,
    private readonly concurrencyErrorService: ConcurrencyErrorService,
    @Inject(LOCAL_STORAGE) private readonly localStorage: Storage,
  ) {
    super(initialState);
  }

  listenToStorage(): void {
    this.localStorageSubscription = this.storage$.subscribe((state) => this.setState(state));
  }

  stopListeningToStorage(): void {
    this.localStorageSubscription.unsubscribe();
  }

  setState(state: PermitSurrenderState): void {
    if (state.requestTaskId) {
      this.localStorage.setItem(`${localStorageItemCode}/${state.requestTaskId}`, JSON.stringify(state));
    }
    super.setState(state);
  }

  get payload(): PermitSurrenderApplicationSubmitRequestTaskPayload {
    return this.getValue() as PermitSurrenderApplicationSubmitRequestTaskPayload;
  }

  get permitSurrender(): PermitSurrender {
    return this.payload.permitSurrender;
  }

  get isEditable$(): Observable<boolean> {
    return this.pipe(pluck('isEditable'));
  }

  getDownloadUrlFiles(files: string[]): { downloadUrl: string; fileName: string }[] {
    const url = this.createBaseFileDownloadUrl();

    return (
      files?.map((id) => ({
        downloadUrl: url + `${id}`,
        fileName: this.getState().permitSurrenderAttachments[id],
      })) ?? []
    );
  }

  createBaseFileDownloadUrl() {
    return this.getState().requestActionId
      ? `/permit-surrender/action/${this.getState().requestActionId}/file-download/attachment/`
      : `/permit-surrender/${this.getState().requestTaskId}/file-download/`;
  }

  getPermitSurrender(): Observable<PermitSurrender> {
    return this.pipe(pluck('permitSurrender'), distinctUntilChanged());
  }

  postApplyPermitSurrender(state: PermitSurrenderState) {
    return this.tasksService
      .processRequestTaskActionUsingPOST({
        requestTaskActionType: 'PERMIT_SURRENDER_SAVE_APPLICATION',
        requestTaskId: state.requestTaskId,
        requestTaskActionPayload: {
          payloadType: 'PERMIT_SURRENDER_SAVE_APPLICATION_PAYLOAD',
          permitSurrender: state.permitSurrender,
          sectionsCompleted: state.sectionsCompleted,
        } as PermitSurrenderSaveApplicationRequestTaskActionPayload,
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

  postSubmitPermitSurrender() {
    return this.tasksService
      .processRequestTaskActionUsingPOST({
        requestTaskActionType: 'PERMIT_SURRENDER_SUBMIT_APPLICATION',
        requestTaskId: this.getState().requestTaskId,
        requestTaskActionPayload: {
          payloadType: 'EMPTY_PAYLOAD',
        } as RequestTaskActionEmptyPayload,
      })
      .pipe(
        catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
          this.concurrencyErrorService.showErrorForceNavigation(taskNotFoundError),
        ),
        catchTaskReassignedBadRequest(() =>
          this.concurrencyErrorService.showErrorForceNavigation(requestTaskReassignedError()),
        ),
      );
  }

  postReviewDecision(reviewDecision: PermitSurrenderReviewDecision, reviewDeterminationCompleted: boolean) {
    return this.tasksService
      .processRequestTaskActionUsingPOST({
        requestTaskActionType: 'PERMIT_SURRENDER_SAVE_REVIEW_GROUP_DECISION',
        requestTaskId: this.getState().requestTaskId,
        requestTaskActionPayload: {
          payloadType: 'PERMIT_SURRENDER_SAVE_REVIEW_GROUP_DECISION_PAYLOAD',
          reviewDecision: reviewDecision,
          reviewDeterminationCompleted: reviewDeterminationCompleted,
        } as PermitSurrenderSaveReviewGroupDecisionRequestTaskActionPayload,
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
            reviewDecision,
            reviewDeterminationCompleted,
            ...(!reviewDeterminationCompleted ? { reviewDetermination: null } : null),
          }),
        ),
      );
  }

  postReviewDetermination(value: any, status: boolean) {
    return this.tasksService
      .processRequestTaskActionUsingPOST({
        requestTaskActionType: 'PERMIT_SURRENDER_SAVE_REVIEW_DETERMINATION',
        requestTaskId: this.getState().requestTaskId,
        requestTaskActionPayload: {
          payloadType: 'PERMIT_SURRENDER_SAVE_REVIEW_DETERMINATION_PAYLOAD',
          reviewDetermination: value,
          reviewDeterminationCompleted: status,
        } as PermitSurrenderSaveReviewDeterminationRequestTaskActionPayload,
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
            reviewDetermination: value,
            reviewDeterminationCompleted: status,
          }),
        ),
      );
  }

  postSaveCessation(value: any, status: boolean) {
    return this.tasksService
      .processRequestTaskActionUsingPOST({
        requestTaskActionType: 'PERMIT_SURRENDER_SAVE_CESSATION',
        requestTaskId: this.getState().requestTaskId,
        requestTaskActionPayload: {
          payloadType: 'PERMIT_SURRENDER_SAVE_CESSATION_PAYLOAD',
          cessation: value,
          cessationCompleted: status,
        } as PermitSaveCessationRequestTaskActionPayload,
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
            cessation: value,
            cessationCompleted: status,
          }),
        ),
      );
  }
}
