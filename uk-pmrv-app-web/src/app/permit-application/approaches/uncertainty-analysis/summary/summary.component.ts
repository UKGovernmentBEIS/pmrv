import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Router } from '@angular/router';

import { PermitApplicationStore } from '../../../store/permit-application.store';

@Component({
  selector: 'app-summary',
  template: `
    <app-permit-task
      [notification]="notification"
      [breadcrumb]="true"
      reviewGroupTitle="Uncertainty analysis"
      reviewGroupUrl="uncertainty-analysis"
    >
      <app-page-heading caption="Monitoring approaches">Uncertainty analysis</app-page-heading>
      <app-uncertainty-analysis-summary-details></app-uncertainty-analysis-summary-details>
      <app-list-return-link
        reviewGroupTitle="Uncertainty analysis"
        reviewGroupUrl="uncertainty-analysis"
      ></app-list-return-link>
    </app-permit-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SummaryComponent {
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;

  constructor(private readonly router: Router, readonly store: PermitApplicationStore) {}
}
