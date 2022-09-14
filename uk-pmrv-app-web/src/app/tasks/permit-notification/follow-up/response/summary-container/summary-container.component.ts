import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, map, Observable } from 'rxjs';

import { BackLinkService } from '@shared/back-link/back-link.service';

import { PermitNotificationFollowUpRequestTaskPayload } from 'pmrv-api';

import { PermitNotificationService } from '../../../core/permit-notification.service';
import { Files, SummaryList } from '../../model/model';

interface TaskPayload extends PermitNotificationFollowUpRequestTaskPayload {
  files?: Files[];
}

@Component({
  selector: 'app-summary-container',
  template: `
    <govuk-notification-banner *ngIf="notification" type="success">
      <h1 class="govuk-notification-banner__heading">Details updated</h1>
    </govuk-notification-banner>
    <app-page-heading size="l">{{ (route.data | async)?.pageTitle }}</app-page-heading>
    <app-follow-up-summary
      class="govuk-!-display-block govuk-!-margin-bottom-8"
      [data]="responseData$ | async"
      [summaryListMapper]="summaryListResponseMapper"
      sectionHeading="Response details"
    ></app-follow-up-summary>
    <app-follow-up-summary
      class="govuk-!-display-block govuk-!-margin-bottom-8"
      [data]="data$ | async"
      [summaryListMapper]="summaryListMapper"
      [changeLink]="(isEditable$ | async) ? 'response' : undefined"
      sectionHeading="Decision details"
    ></app-follow-up-summary>
    <a govukLink routerLink="../">Return to: Follow up response to a notification</a>
  `,
  providers: [BackLinkService],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SummaryContainerComponent implements OnInit {
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;
  constructor(
    private readonly permitNotificationService: PermitNotificationService,
    readonly route: ActivatedRoute,
    readonly router: Router,
    private readonly backLinkService: BackLinkService,
  ) {}

  isEditable$ = this.permitNotificationService.getIsEditable();

  responseData$ = this.permitNotificationService.getPayload().pipe(
    map(({ followUpRequest }) => ({
      followUpRequest,
    })),
  );

  summaryListMapper: Record<keyof { followUpResponse: string; files: string }, SummaryList> = {
    followUpResponse: { label: 'Provide a response', order: 1, type: 'string' },
    files: { label: 'Upload any supporting documents (optional)', order: 2, type: 'files', isArray: true },
  };

  data$: Observable<Pick<TaskPayload, 'followUpResponse' | 'files'>> = combineLatest([
    this.permitNotificationService.getPayload(),
    this.permitNotificationService.downloadUrlFiles$,
  ]).pipe(
    map(([payload, files]) => {
      return {
        followUpResponse: (payload as PermitNotificationFollowUpRequestTaskPayload)?.followUpResponse,
        files,
      };
    }),
  );

  summaryListResponseMapper: Record<keyof { followUpRequest: string }, SummaryList> = {
    followUpRequest: { label: 'Request from the regulator', order: 1, type: 'string' },
  };

  ngOnInit(): void {
    this.backLinkService.show();
  }
}
