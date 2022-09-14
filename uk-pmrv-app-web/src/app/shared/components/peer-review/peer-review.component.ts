import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { BehaviorSubject, filter, first, map, Observable, shareReplay, switchMap, switchMapTo, take } from 'rxjs';

import { GovukSelectOption } from 'govuk-components';

import {
  ItemDTO,
  ItemDTOResponse,
  RequestItemsService,
  RequestTaskActionPayload,
  RequestTaskActionProcessDTO,
  RequestTaskDTO,
  TasksAssignmentService,
  TasksService,
} from 'pmrv-api';

import { PendingRequestService } from '../../../core/guards/pending-request.service';
import { catchTaskReassignedBadRequest } from '../../../error/business-errors';
import { ConcurrencyErrorService } from '../../../error/concurrency-error/concurrency-error.service';
import { catchNotFoundRequest, ErrorCode } from '../../../error/not-found-error';
import { requestTaskReassignedError, taskNotFoundError } from '../../errors/concurrency-error';
import { UserFullNamePipe } from '../../pipes/user-full-name.pipe';
import { StoreResolver } from '../../store-resolver/store-resolver';
import { PEER_REVIEW_FORM, peerReviewFormFactory } from './peer-review-form.provider';
import {
  getPreviousPage,
  getRequestTaskActionPayloadType,
  getRequestTaskActionType,
  getRequestType,
  getTaskType,
  getWaitActionTypes,
} from './peer-review-type-resolver';

@Component({
  selector: 'app-peer-review',
  templateUrl: './peer-review.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [peerReviewFormFactory, UserFullNamePipe],
})
export class PeerReviewComponent {
  private readonly taskId$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('taskId'))));
  private readonly requestType = getRequestType(this.router.url);

  taskType: RequestTaskDTO['type'] = getTaskType(this.requestType);
  requestTaskActionType: RequestTaskActionProcessDTO['requestTaskActionType'] = getRequestTaskActionType(
    this.requestType,
  );
  payloadType: RequestTaskActionPayload['payloadType'] = getRequestTaskActionPayloadType(this.requestType);
  waitActions: ItemDTO['taskType'][] = getWaitActionTypes(this.requestType);

  pendingRfi$: Observable<boolean> = this.storeResolver.getStore(this.requestType).pipe(
    filter(() => this.waitActions.length > 0),
    first(),
    map((state) =>
      ['permit-application', 'permit-surrender'].includes(this.requestType)
        ? state.requestId
        : state.requestTaskItem.requestInfo.id,
    ),
    switchMap((requestTaskId) => this.requestItemsService.getItemsByRequestUsingGET(requestTaskId)),
    first(),
    map((res: ItemDTOResponse) => res.items.some((i) => this.waitActions.includes(i.taskType))),
  );

  assignees$: Observable<GovukSelectOption<string>[]> = this.taskId$.pipe(
    switchMap((id: number) => this.tasksAssignmentService.getCandidateAssigneesByTaskTypeUsingGET(id, this.taskType)),
    map((assignees) =>
      assignees.map((assignee) => ({ text: this.fullNamePipe.transform(assignee), value: assignee.id })),
    ),
    shareReplay({ bufferSize: 1, refCount: true }),
  );

  isFormSubmitted$ = new BehaviorSubject(false);
  assignee$: BehaviorSubject<string> = new BehaviorSubject(null);

  previousPage = getPreviousPage(this.requestType);

  constructor(
    @Inject(PEER_REVIEW_FORM) readonly form: FormGroup,
    readonly pendingRequest: PendingRequestService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly tasksAssignmentService: TasksAssignmentService,
    private readonly fullNamePipe: UserFullNamePipe,
    private readonly tasksService: TasksService,
    private readonly concurrencyErrorService: ConcurrencyErrorService,
    private readonly storeResolver: StoreResolver,
    private readonly requestItemsService: RequestItemsService,
  ) {}

  onSubmit(): void {
    if (this.form.valid) {
      this.taskId$
        .pipe(
          first(),
          switchMap((taskId) =>
            this.tasksService.processRequestTaskActionUsingPOST({
              requestTaskActionType: this.requestTaskActionType,
              requestTaskId: taskId,
              requestTaskActionPayload: {
                payloadType: this.payloadType,
                peerReviewer: this.form.get('assignees').value,
              } as RequestTaskActionPayload,
            }),
          ),
          this.pendingRequest.trackRequest(),
          catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
            this.concurrencyErrorService.showErrorForceNavigation(taskNotFoundError),
          ),
          catchTaskReassignedBadRequest(() =>
            this.concurrencyErrorService.showErrorForceNavigation(requestTaskReassignedError()),
          ),
          switchMapTo(this.assignees$),
          take(1),
          map((assignees) => assignees.find((assignee) => assignee.value === this.form.get('assignees').value)),
        )
        .subscribe((assignee) => {
          this.assignee$.next(assignee.text);
          this.isFormSubmitted$.next(true);
        });
    }
  }
}
