import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { BehaviorSubject, map, takeUntil } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { BackLinkService } from '@shared/back-link/back-link.service';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { PermitNotificationFollowUpApplicationAmendsSubmitRequestTaskPayload } from 'pmrv-api';

import { PermitNotificationService } from '../../../core/permit-notification.service';

@Component({
  selector: 'app-submit-amends',
  template: `<app-submit
      [allowSubmit]="allowSubmit$ | async"
      [isEditable]="store.isEditable$ | async"
      [isActionSubmitted]="isActionSubmitted$ | async"
      [returnUrlConfig]="{ text: 'follow up response', url: '..' }"
      [competentAuthority]="competentAuthority$ | async"
      [customSubmitContentTemplate]="test"
      (submitClicked)="onSubmit()"
    ></app-submit>
    <ng-template #test>
      <p class="govuk-body"><strong>Send your application</strong></p>

      <p class="govuk-body">
        By submitting this application you are confirming that, to the best of your knowledge, the details you are
        providing are correct.
      </p>
    </ng-template>`,
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [BackLinkService],
})
export class SubmitAmendsContainerComponent implements OnInit {
  allowSubmit$ = this.permitNotificationService.getPayload().pipe(
    map((state: PermitNotificationFollowUpApplicationAmendsSubmitRequestTaskPayload) => {
      return !!state?.followUpResponse && state.followUpSectionsCompleted.AMENDS_NEEDED;
    }),
  );
  isActionSubmitted$ = new BehaviorSubject(false);
  competentAuthority$ = this.store.pipe(map((state) => state.requestTaskItem.requestInfo.competentAuthority));

  constructor(
    readonly store: CommonTasksStore,
    readonly pendingRequest: PendingRequestService,
    private readonly permitNotificationService: PermitNotificationService,
    private readonly backLinkService: BackLinkService,
    readonly route: ActivatedRoute,
    private readonly destroy$: DestroySubject,
  ) {}

  ngOnInit(): void {
    this.backLinkService.show();
  }

  onSubmit(): void {
    this.permitNotificationService
      .postSubmit(
        'PERMIT_NOTIFICATION_FOLLOW_UP_SUBMIT_APPLICATION_AMEND',
        'PERMIT_NOTIFICATION_FOLLOW_UP_SUBMIT_APPLICATION_AMEND_PAYLOAD',
      )
      .pipe(this.pendingRequest.trackRequest(), takeUntil(this.destroy$))
      .subscribe(() => {
        this.isActionSubmitted$.next(true);
        this.backLinkService.hide();
      });
  }
}
