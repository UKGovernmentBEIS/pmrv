import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { BehaviorSubject, map, takeUntil } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { BackLinkService } from '@shared/back-link/back-link.service';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { PermitNotificationService } from '../../../core/permit-notification.service';

@Component({
  selector: 'app-submit-container',
  template: `<app-submit
    [allowSubmit]="allowSubmit$ | async"
    [isEditable]="store.isEditable$ | async"
    [isActionSubmitted]="isActionSubmitted$ | async"
    [returnUrlConfig]="{ text: 'follow up response', url: '..' }"
    [competentAuthority]="competentAuthority$ | async"
    (submitClicked)="onSubmit()"
  ></app-submit>`,
  providers: [BackLinkService, DestroySubject],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SubmitContainerComponent implements OnInit {
  allowSubmit$ = this.permitNotificationService.getPayload().pipe(
    map((state) => {
      return state?.followUpResponse;
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
      .postSubmit('PERMIT_NOTIFICATION_FOLLOW_UP_SUBMIT_RESPONSE', 'EMPTY_PAYLOAD')
      .pipe(this.pendingRequest.trackRequest(), takeUntil(this.destroy$))
      .subscribe(() => {
        this.isActionSubmitted$.next(true);
        this.backLinkService.hide();
      });
  }
}
