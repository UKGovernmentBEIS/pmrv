import { Location } from '@angular/common';
import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { combineLatest, first, map, Observable, Subject, switchMap, withLatestFrom } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { requestTaskReassignedError, taskNotFoundError } from '@shared/errors/concurrency-error';

import { TasksService } from 'pmrv-api';

import { catchTaskReassignedBadRequest } from '../../../error/business-errors';
import { ConcurrencyErrorService } from '../../../error/concurrency-error/concurrency-error.service';
import { catchNotFoundRequest, ErrorCode } from '../../../error/not-found-error';
import { TaskItemStatus } from '../../../shared/task-list/task-list.interface';
import { getAvailableSections } from '../../shared/permit-sections/available-sections';
import { TaskStatusPipe } from '../../shared/pipes/task-status.pipe';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { variationDetailsStatus } from '../variation-status';

@Component({
  selector: 'app-summary',
  templateUrl: './summary.component.html',
  providers: [TaskStatusPipe],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SummaryComponent implements PendingRequest {
  private readonly taskId$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('taskId'))));

  variationDetailsStatus$: Observable<TaskItemStatus> = this.store.pipe(map((state) => variationDetailsStatus(state)));

  allowSubmission$ = this.store.pipe(
    switchMap((state) =>
      combineLatest(getAvailableSections(state).map((section: string) => this.taskStatus.transform(section))),
    ),
    withLatestFrom(this.variationDetailsStatus$),
    map(
      ([permitStatuses, variationDetailsStatus]) =>
        permitStatuses.every((status) => status === 'complete') && variationDetailsStatus === 'complete',
    ),
  );
  competentAuthority$ = this.store.select('competentAuthority');

  variationSubmitted$ = new Subject<boolean>();

  constructor(
    readonly pendingRequest: PendingRequestService,
    private readonly store: PermitApplicationStore,
    private readonly tasksService: TasksService,
    private readonly route: ActivatedRoute,
    private readonly taskStatus: TaskStatusPipe,
    private readonly concurrencyErrorService: ConcurrencyErrorService,
    readonly location: Location,
  ) {}

  onSubmit(): void {
    this.taskId$
      .pipe(
        first(),
        switchMap((taskId) =>
          this.tasksService.processRequestTaskActionUsingPOST({
            requestTaskActionType: 'PERMIT_VARIATION_OPERATOR_SUBMIT_APPLICATION',
            requestTaskId: taskId,
            requestTaskActionPayload: {
              payloadType: 'EMPTY_PAYLOAD',
            },
          }),
        ),
        this.pendingRequest.trackRequest(),
        catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
          this.concurrencyErrorService.showErrorForceNavigation(taskNotFoundError),
        ),
        catchTaskReassignedBadRequest(() =>
          this.concurrencyErrorService.showErrorForceNavigation(requestTaskReassignedError()),
        ),
      )
      .subscribe(() => this.variationSubmitted$.next(true));
  }
}
