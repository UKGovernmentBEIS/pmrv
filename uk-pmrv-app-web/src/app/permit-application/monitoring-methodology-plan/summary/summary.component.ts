import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Router } from '@angular/router';

import { PermitApplicationStore } from '../../store/permit-application.store';

@Component({
  selector: 'app-monitoring-methodology-plan-summary',
  template: `
    <app-permit-task
      [notification]="notification"
      [breadcrumb]="true"
      reviewGroupTitle="Monitoring methodology"
      reviewGroupUrl="monitoring-methodology-plan"
    >
      <app-page-heading caption="Monitoring methodology plan">Monitoring methodology plan</app-page-heading>
      <app-monitoring-methodology-summary-details></app-monitoring-methodology-summary-details>
      <app-list-return-link
        reviewGroupTitle="Monitoring methodology"
        reviewGroupUrl="monitoring-methodology-plan"
      ></app-list-return-link>
    </app-permit-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MonitoringMethodologyPlanSummaryComponent {
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;

  constructor(readonly store: PermitApplicationStore, private readonly router: Router) {}
}
