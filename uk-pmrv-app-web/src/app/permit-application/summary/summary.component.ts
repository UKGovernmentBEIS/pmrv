import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, pluck, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { requestTaskReassignedError, taskNotFoundError } from '@shared/errors/concurrency-error';

import { PermitIssuanceSubmitApplicationAmendRequestTaskActionPayload, TasksService } from 'pmrv-api';

import { catchTaskReassignedBadRequest } from '../../error/business-errors';
import { ConcurrencyErrorService } from '../../error/concurrency-error/concurrency-error.service';
import { catchNotFoundRequest, ErrorCode } from '../../error/not-found-error';
import { amendTaskStatusKeys } from '../amend/amend';
import { getAvailableSections } from '../shared/permit-sections/available-sections';
import { TaskStatusPipe } from '../shared/pipes/task-status.pipe';
import { PermitApplicationStore } from '../store/permit-application.store';
@Component({
  selector: 'app-summary',
  templateUrl: './summary.component.html',
  providers: [TaskStatusPipe],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SummaryComponent implements PendingRequest {
  private readonly taskId$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('taskId'))));
  allowSubmission$ = this.store.pipe(
    switchMap((state) =>
      combineLatest(getAvailableSections(state).map((section: string) => this.taskStatus.transform(section))),
    ),
    map((statuses) => statuses.every((status) => status === 'complete')),
  );

  permitType$ = this.store.pipe(pluck('permitType'));

  constructor(
    readonly pendingRequest: PendingRequestService,
    private readonly store: PermitApplicationStore,
    private readonly router: Router,
    private readonly tasksService: TasksService,
    private readonly route: ActivatedRoute,
    private readonly taskStatus: TaskStatusPipe,
    private readonly concurrencyErrorService: ConcurrencyErrorService,
  ) {}

  onSubmit(): void {
    combineLatest([this.store, this.taskId$])
      .pipe(
        first(),
        switchMap(([state, taskId]) =>
          this.tasksService.processRequestTaskActionUsingPOST({
            requestTaskActionType:
              state.requestTaskType === 'PERMIT_ISSUANCE_APPLICATION_AMENDS_SUBMIT'
                ? 'PERMIT_ISSUANCE_SUBMIT_APPLICATION_AMEND'
                : 'PERMIT_ISSUANCE_SUBMIT_APPLICATION',
            requestTaskId: taskId,
            requestTaskActionPayload:
              state.requestTaskType === 'PERMIT_ISSUANCE_APPLICATION_AMENDS_SUBMIT'
                ? ({
                    payloadType: 'PERMIT_ISSUANCE_SUBMIT_APPLICATION_AMEND_PAYLOAD',
                    permitSectionsCompleted: {
                      ...Object.keys(state.permitSectionsCompleted ?? [])
                        .filter((statusKey) => !amendTaskStatusKeys.includes(statusKey))
                        .reduce((res, key) => ((res[key] = state.permitSectionsCompleted[key]), res), {}),
                    },
                  } as PermitIssuanceSubmitApplicationAmendRequestTaskActionPayload)
                : {
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
      .subscribe(() => this.router.navigate(['../application-submitted'], { relativeTo: this.route }));
  }
}
