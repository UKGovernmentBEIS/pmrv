import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { map, Observable } from 'rxjs';

import { PermitNotificationFollowUpReviewDecision } from 'pmrv-api';

import { SummaryList } from '../../../tasks/permit-notification/follow-up/model/model';
import { PermitNotificationService } from '../core/permit-notification.service';

@Component({
  selector: 'app-follow-up-return-for-amends',
  template: `
    <app-base-action-container-component
      [header]="(route.data | async)?.pageTitle"
      [customContentTemplate]="customContentTemplate"
      [expectedActionType]="['PERMIT_NOTIFICATION_FOLLOW_UP_RETURNED_FOR_AMENDS']"
    >
    </app-base-action-container-component>
    <ng-template #customContentTemplate>
      <h2 class="govuk-heading-m">Details of the amends needed</h2>
      <p class="govuk-body">The following notes are from the regulator explaining the things you need to change.</p>
      <app-follow-up-summary
        class="govuk-!-display-block govuk-!-margin-bottom-8"
        [data]="data$ | async"
        [summaryListMapper]="summaryListMapper"
        [allowChange]="false"
      ></app-follow-up-summary>
    </ng-template>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FollowUpReturnForAmendsComponent {
  constructor(private permitNotificationService: PermitNotificationService, public route: ActivatedRoute) {}

  data$: Observable<Pick<PermitNotificationFollowUpReviewDecision, 'changesRequired' | 'dueDate' | 'files'>> =
    this.permitNotificationService.getPayload().pipe(
      map(({ changesRequired, dueDate }) => ({
        changesRequired,
        dueDate,
      })),
    );

  summaryListMapper: Record<keyof { changesRequired: string; dueDate: string }, SummaryList> = {
    changesRequired: { label: 'Regulators Comments', order: 1, type: 'string' },
    dueDate: { label: 'Changes due by', order: 1, type: 'date' },
  };
}
