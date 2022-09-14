import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output } from '@angular/core';

import { first, of, switchMap } from 'rxjs';

import { TasksService } from 'pmrv-api';

import { PendingRequestService } from '../../../core/guards/pending-request.service';
import { catchTaskReassignedBadRequest } from '../../../error/business-errors';
import { ConcurrencyErrorService } from '../../../error/concurrency-error/concurrency-error.service';
import { catchNotFoundRequest, ErrorCode } from '../../../error/not-found-error';
import { mapToDismiss } from '../../../installation-account-application/pipes/action-pipes';
import { requestTaskReassignedError, taskNotFoundError } from '../../errors/concurrency-error';

@Component({
  selector: 'app-archive',
  template: `
    <govuk-warning-text>
      No actions are currently required.<br />
      {{ warningText }}
    </govuk-warning-text>
    <button (click)="submit()" appPendingButton govukSecondaryButton type="button">
      {{ buttonText }}
    </button>
    <hr class="govuk-!-margin-bottom-6" />
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ArchiveComponent {
  @Input() type: 'INSTALLATION_ACCOUNT_OPENING_ARCHIVE' | 'SYSTEM_MESSAGE_DISMISS';
  @Input() taskId: number;
  @Input() warningText = 'This task / item can be archived for future reference.';
  @Input() buttonText = 'Archive now and return to dashboard';

  @Output() readonly submitted = new EventEmitter<void>();

  constructor(
    readonly pendingRequestService: PendingRequestService,
    private readonly tasksService: TasksService,
    private readonly concurrencyErrorService: ConcurrencyErrorService,
  ) {}

  submit(): void {
    of(this.taskId)
      .pipe(
        first(),
        mapToDismiss(this.type),
        switchMap((payload) => this.tasksService.processRequestTaskActionUsingPOST(payload)),
        this.pendingRequestService.trackRequest(),
        catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
          this.concurrencyErrorService.showErrorForceNavigation(taskNotFoundError),
        ),
        catchTaskReassignedBadRequest(() =>
          this.concurrencyErrorService.showErrorForceNavigation(requestTaskReassignedError()),
        ),
      )
      .subscribe(() => this.submitted.emit());
  }
}
