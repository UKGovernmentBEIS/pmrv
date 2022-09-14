import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { SummaryList } from '../../../tasks/permit-notification/follow-up/model/model';
import { PermitNotificationService } from '../core/permit-notification.service';

@Component({
  selector: 'app-follow-up',
  template: `
    <app-base-action-container-component
      [header]="(route.data | async)?.pageTitle"
      [customContentTemplate]="customContentTemplate"
      [expectedActionType]="['PERMIT_NOTIFICATION_FOLLOW_UP_RESPONSE_SUBMITTED']"
    >
    </app-base-action-container-component>
    <ng-template #customContentTemplate>
      <h2 class="govuk-heading-m">Decision details</h2>
      <app-follow-up-summary
        [allowChange]="false"
        [sectionHeading]="(route.data | async)?.caption"
        [summaryListMapper]="followUpSummaryListMapper"
        [data]="data$ | async"
        [files]="files$ | async"
      ></app-follow-up-summary>
    </ng-template>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FollowUpResponseComponent {
  followUpSummaryListMapper: Record<string, SummaryList> = {
    request: { label: 'Request from the regulator', order: 1, type: 'string' },
    response: { label: 'Response from the operator', order: 2, type: 'string' },
  };
  data$ = this.permitNotificationService.followUpData$;
  files$ = this.permitNotificationService.downloadUrlFiles$;

  constructor(readonly route: ActivatedRoute, private readonly permitNotificationService: PermitNotificationService) {}
}
