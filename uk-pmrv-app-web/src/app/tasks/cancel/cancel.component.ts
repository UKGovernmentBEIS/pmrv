import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { filter, first, map, switchMapTo } from 'rxjs';

import { PendingRequestService } from '../../core/guards/pending-request.service';
import { catchTaskReassignedBadRequest } from '../../error/business-errors';
import { ConcurrencyErrorService } from '../../error/concurrency-error/concurrency-error.service';
import { catchNotFoundRequest, ErrorCode } from '../../error/not-found-error';
import { BackLinkService } from '../../shared/back-link/back-link.service';
import { requestTaskReassignedError, taskNotFoundError } from '../../shared/errors/concurrency-error';
import { CommonTasksStore } from '../store/common-tasks.store';
import { cancelActionMap } from './cancel-action.map';

@Component({
  selector: 'app-cancel-surrender',
  template: `
    <app-page-heading size="xl"> Are you sure you want to cancel this task? </app-page-heading>
    <p class="govuk-body">This task and its data will be deleted.</p>
    <div class="govuk-button-group">
      <button appPendingButton (click)="cancel()" govukWarnButton>Yes, cancel this task</button>
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [BackLinkService],
})
export class CancelComponent implements OnInit {
  taskId$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('taskId'))));

  constructor(
    private readonly pendingRequest: PendingRequestService,
    private readonly concurrencyErrorService: ConcurrencyErrorService,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly backLinkService: BackLinkService,
    private readonly commonTasksStore: CommonTasksStore,
  ) {}

  ngOnInit() {
    //check if cancel action is available for given request type
    this.commonTasksStore.requestTaskItem$
      .pipe(
        filter((item) => !!item),
        switchMapTo(this.commonTasksStore.requestTaskType$),
        first(),
      )
      .subscribe((type) => {
        if (cancelActionMap[type] == null) {
          this.router.navigate(['error', '404']);
        } else {
          this.backLinkService.show();
        }
      });
  }

  cancel(): void {
    this.commonTasksStore
      .cancelCurrentTask()
      .pipe(
        this.pendingRequest.trackRequest(),
        catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
          this.concurrencyErrorService.showErrorForceNavigation(taskNotFoundError),
        ),
        catchTaskReassignedBadRequest(() =>
          this.concurrencyErrorService.showErrorForceNavigation(requestTaskReassignedError()),
        ),
      )
      .subscribe(() => this.router.navigate(['confirmation'], { relativeTo: this.route }));
  }
}
