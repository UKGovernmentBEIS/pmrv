import { Injectable } from '@angular/core';

import { filter, first, map, Observable, switchMap, tap } from 'rxjs';

import { requestTaskReassignedError, taskNotFoundError } from '@shared/errors/concurrency-error';

import {
  PermitNotificationApplicationReviewRequestTaskPayload,
  PermitNotificationApplicationSubmitRequestTaskPayload,
  PermitNotificationFollowUpApplicationReviewRequestTaskPayload,
  PermitNotificationFollowUpReviewDecision,
  RequestTaskActionPayload,
  RequestTaskActionProcessDTO,
  TasksService,
} from 'pmrv-api';

import { catchTaskReassignedBadRequest } from '../../../error/business-errors';
import { ConcurrencyErrorService } from '../../../error/concurrency-error/concurrency-error.service';
import { catchNotFoundRequest, ErrorCode } from '../../../error/not-found-error';
import { CommonTasksState } from '../../store/common-tasks.state';
import { CommonTasksStore } from '../../store/common-tasks.store';
import { StatusKey } from './section-status';

@Injectable({ providedIn: 'root' })
export class PermitNotificationService {
  constructor(
    private readonly store: CommonTasksStore,
    private readonly tasksService: TasksService,
    private readonly concurrencyErrorService: ConcurrencyErrorService,
  ) {}

  getPayload(): Observable<any> {
    return this.store.payload$.pipe(map((payload) => payload));
  }

  get permitNotification$() {
    return this.getPayload().pipe(map((p) => p?.permitNotification));
  }

  get downloadUrlFiles$(): Observable<{ downloadUrl: string; fileName: string }[]> {
    return this.documents$.pipe(map((documents) => this.getDownloadUrlFiles(documents || [])));
  }

  get reviewDecision$() {
    return this.getPayload().pipe(
      map((payload: PermitNotificationApplicationReviewRequestTaskPayload) => payload.reviewDecision),
    );
  }

  get followUpData$() {
    return this.getPayload().pipe(
      map(({ followUpRequest, followUpResponseExpirationDate }) => ({
        followUpRequest,
        followUpResponseExpirationDate,
      })),
    );
  }

  get amendsData$() {
    return this.getPayload().pipe(
      map(({ followUpRequest, followUpResponse, followUpFiles }) => ({
        followUpRequest,
        followUpResponse,
        followUpFiles,
      })),
    );
  }

  get amendsReviewDecisionData$() {
    return (this.reviewDecision$ as Observable<PermitNotificationFollowUpReviewDecision>).pipe(
      map(({ changesRequired, dueDate, files }) => ({
        changesRequired,
        dueDate,
        files,
      })),
    );
  }

  getIsEditable(): Observable<boolean> {
    return this.store.isEditable$;
  }

  getDownloadUrlFiles(files: string[]): { downloadUrl: string; fileName: string }[] {
    const attachments: { [key: string]: string } = this.attachments;
    const url = this.createBaseFileDownloadUrl();
    return (
      files?.map((id) => ({
        downloadUrl: url + `${id}`,
        fileName: attachments[id],
      })) ?? []
    );
  }

  createBaseFileDownloadUrl() {
    const requestTaskId = this.store.requestTaskId;
    return `/tasks/${requestTaskId}/file-download/`;
  }

  getFollowUpReviewDownloadUrlFiles(files: string[]): { downloadUrl: string; fileName: string }[] {
    const { followUpAttachments } = this.store.getValue().requestTaskItem.requestTask
      .payload as PermitNotificationFollowUpApplicationReviewRequestTaskPayload;
    const url = this.createBaseFileDownloadUrl();

    return (
      files?.map((id) => ({
        downloadUrl: url + `${id}`,
        fileName: followUpAttachments[id],
      })) ?? []
    );
  }

  postTaskSave(value: any, attachments?: { [key: string]: string }, statusValue?: boolean, statusKey?: StatusKey) {
    return this.store.pipe(
      first(),
      switchMap((state) =>
        this.postPermitNotification(
          {
            ...state,
            requestTaskItem: {
              ...state.requestTaskItem,
              requestTask: {
                ...state.requestTaskItem.requestTask,
                payload: {
                  ...state.requestTaskItem.requestTask.payload,
                  ...value,
                  payloadType:
                    state.requestTaskItem.requestTask.payload?.payloadType ??
                    'PERMIT_NOTIFICATION_APPLICATION_SUBMIT_PAYLOAD',
                  permitNotificationAttachments: {
                    ...(
                      state.requestTaskItem.requestTask.payload as PermitNotificationApplicationSubmitRequestTaskPayload
                    )?.permitNotificationAttachments,
                    ...attachments,
                  },
                  sectionsCompleted: {
                    ...(
                      state.requestTaskItem.requestTask.payload as PermitNotificationApplicationSubmitRequestTaskPayload
                    )?.sectionsCompleted,
                    ...(statusKey ? { [statusKey]: statusValue } : undefined),
                  },
                } as PermitNotificationApplicationSubmitRequestTaskPayload,
              },
            },
          },
          'PERMIT_NOTIFICATION_SAVE_APPLICATION',
        ),
      ),
    );
  }

  postTaskSubmit() {
    return this.store.pipe(
      first(),
      switchMap((state) => this.postPermitNotification(state, 'PERMIT_NOTIFICATION_SUBMIT_APPLICATION')),
    );
  }

  private postPermitNotification(
    state: CommonTasksState,
    actionType: RequestTaskActionProcessDTO['requestTaskActionType'],
  ) {
    const payload = state.requestTaskItem.requestTask.payload as PermitNotificationApplicationSubmitRequestTaskPayload;
    return this.tasksService
      .processRequestTaskActionUsingPOST({
        requestTaskActionType: actionType,
        requestTaskId: state.requestTaskItem.requestTask.id,
        requestTaskActionPayload: this.createRequestTaskActionPayload(actionType, payload),
      })
      .pipe(
        catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
          this.concurrencyErrorService.showErrorForceNavigation(taskNotFoundError),
        ),
        catchTaskReassignedBadRequest(() =>
          this.concurrencyErrorService.showErrorForceNavigation(requestTaskReassignedError()),
        ),
        tap(() => this.store.setState(state)),
      );
  }

  postPermitNotificationDecision(reviewDecision) {
    return this.store.pipe(
      first(),
      switchMap((state) =>
        this.tasksService.processRequestTaskActionUsingPOST({
          requestTaskActionType: 'PERMIT_NOTIFICATION_SAVE_REVIEW_GROUP_DECISION',
          requestTaskId: state.requestTaskItem.requestTask.id,
          requestTaskActionPayload: {
            payloadType: 'PERMIT_NOTIFICATION_SAVE_REVIEW_GROUP_DECISION_PAYLOAD',
            reviewDecision,
            reviewDeterminationCompleted: true,
          } as RequestTaskActionPayload,
        }),
      ),
      catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
        this.concurrencyErrorService.showErrorForceNavigation(taskNotFoundError),
      ),
      catchTaskReassignedBadRequest(() =>
        this.concurrencyErrorService.showErrorForceNavigation(requestTaskReassignedError()),
      ),
      tap(() => {
        const state = this.store.getState();
        this.store.setState({
          ...state,
          requestTaskItem: {
            ...state.requestTaskItem,
            requestTask: {
              ...state.requestTaskItem.requestTask,
              payload: {
                ...state.requestTaskItem.requestTask.payload,
                reviewDecision: reviewDecision,
              } as PermitNotificationApplicationReviewRequestTaskPayload,
            },
          },
        });
      }),
    );
  }

  postSubmit<T>(
    requestTaskActionType: RequestTaskActionProcessDTO['requestTaskActionType'],
    payloadType: RequestTaskActionPayload['payloadType'],
    payload?: T,
    attachments?: { [key: string]: string },
  ) {
    return this.store.pipe(
      first(),
      switchMap((state) =>
        this.tasksService.processRequestTaskActionUsingPOST({
          requestTaskActionType: requestTaskActionType,
          requestTaskId: state.requestTaskItem.requestTask.id,
          requestTaskActionPayload: {
            payloadType: payloadType,
            ...payload,
          } as RequestTaskActionPayload,
        }),
      ),
      catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
        this.concurrencyErrorService.showErrorForceNavigation(taskNotFoundError),
      ),
      catchTaskReassignedBadRequest(() =>
        this.concurrencyErrorService.showErrorForceNavigation(requestTaskReassignedError()),
      ),
      tap(() => {
        const state = this.store.getState();
        this.store.setState({
          ...state,
          requestTaskItem: {
            ...state.requestTaskItem,
            requestTask: {
              ...state.requestTaskItem.requestTask,
              payload: {
                ...state.requestTaskItem.requestTask.payload,
                ...this.customPayloadPerType(payloadType, payload, attachments),
              },
            },
          },
        });
      }),
    );
  }

  customPayloadPerType<T>(
    payloadType: RequestTaskActionPayload['payloadType'],
    payload: T,
    attachments?: { [key: string]: string },
  ) {
    switch (payloadType) {
      case 'PERMIT_NOTIFICATION_FOLLOW_UP_EXTEND_DATE_PAYLOAD':
        return { followUpResponseExpirationDate: payload['dueDate'] };
      case 'PERMIT_NOTIFICATION_FOLLOW_UP_SAVE_APPLICATION_AMEND_PAYLOAD':
      case 'PERMIT_NOTIFICATION_FOLLOW_UP_SAVE_RESPONSE_PAYLOAD':
        return {
          followUpResponse: payload['response'],
          followUpFiles: payload['files'],
          followUpAttachments: attachments,
          ...(payload['followUpSectionsCompleted'] && {
            followUpSectionsCompleted: payload['followUpSectionsCompleted'],
          }),
        };
      default:
        return payload;
    }
  }

  postFollowUpDecision(reviewDecision: PermitNotificationFollowUpReviewDecision) {
    return this.store.pipe(
      first(),
      switchMap((state) =>
        this.tasksService.processRequestTaskActionUsingPOST({
          requestTaskActionType: 'PERMIT_NOTIFICATION_FOLLOW_UP_SAVE_REVIEW_DECISION',
          requestTaskId: state.requestTaskItem.requestTask.id,
          requestTaskActionPayload: {
            payloadType: 'PERMIT_NOTIFICATION_FOLLOW_UP_SAVE_REVIEW_DECISION_PAYLOAD',
            reviewDecision,
          } as RequestTaskActionPayload,
        }),
      ),
      catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
        this.concurrencyErrorService.showErrorForceNavigation(taskNotFoundError),
      ),
      catchTaskReassignedBadRequest(() =>
        this.concurrencyErrorService.showErrorForceNavigation(requestTaskReassignedError()),
      ),
      tap(() => {
        const state = this.store.getState();
        this.store.setState({
          ...state,
          requestTaskItem: {
            ...state.requestTaskItem,
            requestTask: {
              ...state.requestTaskItem.requestTask,
              payload: {
                ...state.requestTaskItem.requestTask.payload,
                reviewDecision: reviewDecision,
              } as PermitNotificationFollowUpApplicationReviewRequestTaskPayload,
            },
          },
        });
      }),
    );
  }

  private createRequestTaskActionPayload(
    actionType: RequestTaskActionProcessDTO['requestTaskActionType'],
    payload: PermitNotificationApplicationSubmitRequestTaskPayload,
  ) {
    return {
      ...(actionType === 'PERMIT_NOTIFICATION_SAVE_APPLICATION'
        ? {
            payloadType: 'PERMIT_NOTIFICATION_SAVE_APPLICATION_PAYLOAD',
            permitNotification: payload.permitNotification,
            sectionsCompleted: payload.sectionsCompleted,
          }
        : {
            payloadType: 'EMPTY_PAYLOAD',
          }),
    } as RequestTaskActionPayload;
  }

  private get documents$(): Observable<string[]> {
    return this.store.payload$.pipe(
      filter((payload) => !!payload?.payloadType),
      map((payload) => {
        switch (payload.payloadType) {
          case 'PERMIT_NOTIFICATION_APPLICATION_PEER_REVIEW_PAYLOAD':
          case 'PERMIT_NOTIFICATION_WAIT_FOR_PEER_REVIEW_PAYLOAD':
          case 'PERMIT_NOTIFICATION_APPLICATION_REVIEW_PAYLOAD':
          case 'PERMIT_NOTIFICATION_APPLICATION_SUBMIT_PAYLOAD':
            return (<PermitNotificationApplicationSubmitRequestTaskPayload>payload).permitNotification.documents;
          case 'PERMIT_NOTIFICATION_FOLLOW_UP_APPLICATION_AMENDS_SUBMIT_PAYLOAD':
          case 'PERMIT_NOTIFICATION_FOLLOW_UP_APPLICATION_REVIEW_PAYLOAD':
          case 'PERMIT_NOTIFICATION_FOLLOW_UP_PAYLOAD':
          case 'PERMIT_NOTIFICATION_FOLLOW_UP_WAIT_FOR_AMENDS_PAYLOAD':
            return (<PermitNotificationFollowUpApplicationReviewRequestTaskPayload>payload).followUpFiles;
          default:
            throw Error('Unhandled task payload type: ' + payload.payloadType);
        }
      }),
    );
  }

  private get attachments() {
    let attachments: { [key: string]: string };
    const requestTaskType = this.store.requestTaskType;
    switch (requestTaskType) {
      case 'PERMIT_NOTIFICATION_APPLICATION_REVIEW':
      case 'PERMIT_NOTIFICATION_APPLICATION_SUBMIT':
      case 'PERMIT_NOTIFICATION_APPLICATION_PEER_REVIEW':
      case 'PERMIT_NOTIFICATION_WAIT_FOR_PEER_REVIEW':
        attachments = this.store.permitNotificationAttachments;
        break;
      case 'PERMIT_NOTIFICATION_FOLLOW_UP_APPLICATION_AMENDS_SUBMIT':
      case 'PERMIT_NOTIFICATION_FOLLOW_UP':
      case 'PERMIT_NOTIFICATION_FOLLOW_UP_APPLICATION_REVIEW':
        attachments = this.store.followUpAttachments;
        break;
      case 'PERMIT_NOTIFICATION_FOLLOW_UP_WAIT_FOR_AMENDS':
        attachments = this.store.followUpResponseAttachments;
        break;
      default:
        throw Error('Unhandled task payload type: ' + requestTaskType);
    }
    return attachments;
  }
}
