import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, switchMap } from 'rxjs';

import { taskNotFoundError } from '@shared/errors/concurrency-error';

import { RequestTaskActionPayload, TasksService } from 'pmrv-api';

import { PendingRequestService } from '../../../core/guards/pending-request.service';
import { PendingRequest } from '../../../core/interfaces/pending-request.interface';
import { ConcurrencyErrorService } from '../../../error/concurrency-error/concurrency-error.service';
import { catchNotFoundRequest, ErrorCode } from '../../../error/not-found-error';

@Component({
  selector: 'app-recall',
  template: `
    <app-page-heading size="xl"> Are you sure you want to recall the permit ? </app-page-heading>
    <div class="govuk-button-group">
      <button appPendingButton (click)="recall()" govukWarnButton>Yes, recall the permit</button>
    </div>
    <a govukLink routerLink="..">Return to: Permit determination</a>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RecallComponent implements PendingRequest {
  private readonly taskId$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('taskId'))));

  constructor(
    readonly pendingRequest: PendingRequestService,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly tasksService: TasksService,
    private readonly concurrencyErrorService: ConcurrencyErrorService,
  ) {}

  recall(): void {
    this.taskId$
      .pipe(
        first(),
        switchMap((taskId) =>
          this.tasksService.processRequestTaskActionUsingPOST({
            requestTaskActionType: 'PERMIT_ISSUANCE_RECALL_FROM_AMENDS',
            requestTaskId: taskId,
            requestTaskActionPayload: {
              payloadType: 'EMPTY_PAYLOAD',
            } as RequestTaskActionPayload,
          }),
        ),
        catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
          this.concurrencyErrorService.showErrorForceNavigation(taskNotFoundError),
        ),
        this.pendingRequest.trackRequest(),
      )
      .subscribe(() => this.router.navigate(['confirmation'], { relativeTo: this.route }));
  }
}
