import { Injectable } from '@angular/core';
import { Router } from '@angular/router';

import { first, forkJoin, map, Observable, of, switchMap, tap } from 'rxjs';

import { AuthService } from '@core/services/auth.service';
import { Store } from '@core/store';
import { SharedStore } from '@shared/store/shared.store';

import {
  AerApplicationSubmitRequestTaskPayload,
  ItemDTO,
  PermitNotificationApplicationSubmitRequestTaskPayload,
  PermitNotificationFollowUpRequestTaskPayload,
  PermitNotificationFollowUpWaitForAmendsRequestTaskPayload,
  RequestActionInfoDTO,
  RequestActionsService,
  RequestItemsService,
  RequestMetadata,
  RequestTaskActionPayload,
  RequestTaskActionProcessDTO,
  RequestTaskDTO,
  RequestTaskItemDTO,
  RequestTaskPayload,
  TasksService,
} from 'pmrv-api';

import { catchNotFoundRequest, ErrorCode } from '../../error/not-found-error';
import { cancelActionMap } from '../cancel/cancel-action.map';
import { CommonTasksState, initialState } from './common-tasks.state';

const requestTaskAllowedActionsMap: Partial<Record<RequestTaskDTO['type'], any[]>> = {
  PERMIT_NOTIFICATION_APPLICATION_REVIEW: ['PERMIT_NOTIFICATION_SAVE_REVIEW_GROUP_DECISION'],
  PERMIT_NOTIFICATION_APPLICATION_SUBMIT: ['PERMIT_NOTIFICATION_SAVE_APPLICATION'],
  PERMIT_NOTIFICATION_FOLLOW_UP: [
    'PERMIT_NOTIFICATION_FOLLOW_UP_UPLOAD_ATTACHMENT',
    'PERMIT_NOTIFICATION_FOLLOW_UP_SAVE_RESPONSE',
    'PERMIT_NOTIFICATION_FOLLOW_UP_SUBMIT_RESPONSE',
  ],
  PERMIT_NOTIFICATION_WAIT_FOR_PEER_REVIEW: [],
  PERMIT_NOTIFICATION_WAIT_FOR_FOLLOW_UP: ['PERMIT_NOTIFICATION_FOLLOW_UP_EXTEND_DATE'],
  PERMIT_NOTIFICATION_FOLLOW_UP_APPLICATION_REVIEW: ['PERMIT_NOTIFICATION_FOLLOW_UP_SAVE_REVIEW_DECISION'],
  PERMIT_NOTIFICATION_FOLLOW_UP_APPLICATION_AMENDS_SUBMIT: ['PERMIT_NOTIFICATION_FOLLOW_UP_SAVE_APPLICATION_AMEND'],
  PERMIT_NOTIFICATION_FOLLOW_UP_WAIT_FOR_AMENDS: ['PERMIT_NOTIFICATION_FOLLOW_UP_EXTEND_DATE'],

  PERMIT_VARIATION_OPERATOR_APPLICATION_SUBMIT: ['PERMIT_VARIATION_OPERATOR_SAVE_APPLICATION'],

  AER_APPLICATION_SUBMIT: ['AER_SAVE_APPLICATION', 'AER_UPLOAD_SECTION_ATTACHMENT'],
};

@Injectable({ providedIn: 'root' })
export class CommonTasksStore extends Store<CommonTasksState> {
  private readonly defaultTaskActionPayload = {
    payloadType: 'EMPTY_PAYLOAD',
  } as RequestTaskActionPayload;

  constructor(
    private readonly tasksService: TasksService,
    private readonly sharedStore: SharedStore,
    private readonly router: Router,
    private readonly requestItemsService: RequestItemsService,
    private readonly requestActionsService: RequestActionsService,
    // authService will be used when Permit Application will be migrated
    private readonly authService: AuthService,
  ) {
    super(initialState);
  }

  setState(state: CommonTasksState): void {
    super.setState(state);
  }

  get state$() {
    return this.asObservable();
  }

  get requestTaskItem$(): Observable<RequestTaskItemDTO> {
    return this.state$.pipe(map((state) => state.requestTaskItem));
  }

  get requestMetadata$(): Observable<RequestMetadata> {
    return this.state$.pipe(map((state) => state.requestTaskItem.requestInfo?.requestMetadata));
  }

  get requestTaskType$(): Observable<RequestTaskDTO['type']> {
    return this.requestTaskItem$.pipe(map((requestTaskItem) => requestTaskItem?.requestTask?.type));
  }

  get relatedTasksItems$(): Observable<ItemDTO[]> {
    return this.state$.pipe(map((state) => state.relatedTasks));
  }

  get timeLineActions$(): Observable<RequestActionInfoDTO[]> {
    return this.state$.pipe(map((state) => state.timeLineActions));
  }

  get storeInitialized$(): Observable<boolean> {
    return this.state$.pipe(map((state) => !!state.storeInitialized));
  }

  get payload$(): Observable<RequestTaskPayload> {
    return this.requestTaskItem$.pipe(map((item) => item?.requestTask.payload));
  }

  get requestTaskId() {
    return this.getValue().requestTaskItem.requestTask.id;
  }

  get requestTaskType() {
    return this.getValue().requestTaskItem?.requestTask?.type;
  }

  get isEditable$() {
    return this.state$.pipe(map((s) => s.isEditable));
  }

  get permitNotificationAttachments() {
    return (
      this.getValue().requestTaskItem.requestTask.payload as PermitNotificationApplicationSubmitRequestTaskPayload
    ).permitNotificationAttachments;
  }

  get followUpAttachments() {
    return (this.getValue().requestTaskItem.requestTask.payload as PermitNotificationFollowUpRequestTaskPayload)
      .followUpAttachments;
  }

  get requestId() {
    return this.getValue().requestTaskItem.requestInfo.id;
  }

  get followUpResponseAttachments() {
    return (
      this.getValue().requestTaskItem.requestTask.payload as PermitNotificationFollowUpWaitForAmendsRequestTaskPayload
    ).followUpResponseAttachments;
  }

  get aerAttachments() {
    return (this.getValue().requestTaskItem.requestTask.payload as AerApplicationSubmitRequestTaskPayload)
      .aerAttachments;
  }

  requestedTask(taskId: number) {
    this.reset();
    this.tasksService
      .getTaskItemInfoByIdUsingGET(taskId)
      .pipe(
        catchNotFoundRequest(ErrorCode.NOTFOUND1001, () => {
          this.router.navigate(['error', '404']);
          return of(null);
        }),
        tap((requestTask) => {
          this.patchState({ requestTaskItem: requestTask, isEditable: this.isTaskEditable(requestTask) });

          this.sharedStore.reset();
          this.sharedStore.setState({ ...this.sharedStore.getState(), accountId: requestTask.requestInfo.accountId });
        }),
        switchMap((requestTask) => this.requestRelatedItemsAndActions$(requestTask)),
        tap(() => this.patchState({ storeInitialized: true })),
        first(),
      )
      .subscribe();
  }

  private requestRelatedItemsAndActions$(requestTaskItem: RequestTaskItemDTO) {
    if (!requestTaskItem) {
      throw Error('No request task item found in Store');
    }
    const { requestInfo, requestTask } = requestTaskItem;
    return forkJoin([
      this.requestItemsService
        .getItemsByRequestUsingGET(requestInfo.id)
        .pipe(map((response) => response.items.filter((item) => item.taskId !== requestTask.id))),
      this.requestActionsService
        .getRequestActionsByRequestIdUsingGET(requestInfo.id)
        .pipe(map((actions) => this.orderTimelineActions(actions))),
    ]).pipe(tap(([relatedTasks, timeLineActions]) => this.patchState({ relatedTasks, timeLineActions })));
  }

  updateTimelineActions(requestId: string): Observable<Array<RequestActionInfoDTO>> {
    return this.requestActionsService.getRequestActionsByRequestIdUsingGET(requestId).pipe(
      map((actions) => this.orderTimelineActions(actions)),
      tap((timeLineActions) =>
        this.setState({
          ...this.getState(),
          ...{ timeLineActions },
        }),
      ),
    );
  }

  private orderTimelineActions(timelineActions: RequestActionInfoDTO[]): RequestActionInfoDTO[] {
    return timelineActions
      .slice()
      .sort((a, b) => new Date(b.creationDate).getTime() - new Date(a.creationDate).getTime());
  }

  cancelCurrentTask() {
    const taskType = this.getState().requestTaskItem.requestTask.type;
    return this.performActionForCurrentTask(cancelActionMap[taskType]);
  }

  private performActionForCurrentTask(taskType: RequestTaskActionProcessDTO['requestTaskActionType']) {
    const task = this.getState()?.requestTaskItem?.requestTask;
    if (!task) {
      throw new Error('No task is currently selected');
    }
    return this.processRequestTaskAction(taskType, task.id);
  }

  private patchState(state: Partial<CommonTasksState>) {
    this.setState({ ...this.getState(), ...state });
  }

  private processRequestTaskAction(
    taskType: RequestTaskActionProcessDTO['requestTaskActionType'],
    taskId: number,
    requestTaskActionPayload: RequestTaskActionPayload = this.defaultTaskActionPayload,
  ) {
    return this.tasksService.processRequestTaskActionUsingPOST({
      requestTaskActionType: taskType,
      requestTaskId: taskId,
      requestTaskActionPayload: requestTaskActionPayload,
    });
  }

  private isTaskEditable(requestTask: RequestTaskItemDTO): boolean {
    return requestTaskAllowedActionsMap[requestTask.requestTask?.type]?.some((taskAction) =>
      requestTask.allowedRequestTaskActions.includes(taskAction),
    );
  }
}
