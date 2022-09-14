import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';

import { BehaviorSubject, map, takeUntil } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { BackLinkService } from '@shared/back-link/back-link.service';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { PermitNotificationService } from '../../core/permit-notification.service';

@Component({
  selector: 'app-submit-container',
  templateUrl: './submit-container.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject, BackLinkService],
})
export class SubmitContainerComponent implements OnInit {
  allowSubmit$ = this.permitNotificationService
    .getPayload()
    .pipe(map((state) => state?.sectionsCompleted['DETAILS_CHANGE']));
  isPermitNotificationSubmitted$ = new BehaviorSubject(false);
  competentAuthority$ = this.store.pipe(map((state) => state.requestTaskItem.requestInfo.competentAuthority));

  constructor(
    readonly store: CommonTasksStore,
    readonly pendingRequest: PendingRequestService,
    private readonly permitNotificationService: PermitNotificationService,
    private readonly backLinkService: BackLinkService,
    private readonly destroy$: DestroySubject,
  ) {}

  ngOnInit(): void {
    this.backLinkService.show();
  }

  onSubmit(): void {
    this.permitNotificationService
      .postSubmit('PERMIT_NOTIFICATION_SUBMIT_APPLICATION', 'EMPTY_PAYLOAD')
      .pipe(this.pendingRequest.trackRequest(), takeUntil(this.destroy$))
      .subscribe(() => {
        this.isPermitNotificationSubmitted$.next(true);
        this.backLinkService.hide();
      });
  }
}
