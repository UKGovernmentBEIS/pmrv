import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Router } from '@angular/router';

import { PermitApplicationStore } from '../../store/permit-application.store';

@Component({
  selector: 'app-regulated-activities-summary',
  template: `
    <app-permit-task
      [notification]="notification"
      [breadcrumb]="true"
      reviewGroupTitle="Installation details"
      reviewGroupUrl="details"
    >
      <app-page-heading caption="Installation details">
        Regulated activities carried out at the installation
      </app-page-heading>
      <h2
        [changeRoute]="(store.isEditable$ | async) === true ? '..' : undefined"
        app-summary-header
        class="govuk-heading-m"
      >
        <span class="govuk-visually-hidden">List of installation categories</span>
      </h2>
      <app-regulated-activities-summary-template></app-regulated-activities-summary-template>
      <app-list-return-link reviewGroupTitle="Installation details" reviewGroupUrl="details"></app-list-return-link>
    </app-permit-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RegulatedActivitiesSummaryComponent {
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;

  constructor(readonly store: PermitApplicationStore, private readonly router: Router) {}
}
