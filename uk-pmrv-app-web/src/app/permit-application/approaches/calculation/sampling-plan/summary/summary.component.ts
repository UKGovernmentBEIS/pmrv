import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Router } from '@angular/router';

import { PermitApplicationStore } from '../../../../store/permit-application.store';

@Component({
  selector: 'app-summary',
  template: `
    <app-permit-task
      [notification]="notification"
      [breadcrumb]="[{ text: 'Calculation approach', link: ['calculation'] }]"
    >
      <app-page-heading caption="Calculation">Sampling plan</app-page-heading>
      <app-calculation-plan-summary-details [changePerStage]="true"></app-calculation-plan-summary-details>
      <app-approach-return-link
        parentTitle="Calculation approach"
        reviewGroupUrl="calculation"
      ></app-approach-return-link>
    </app-permit-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SummaryComponent {
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;

  constructor(private readonly router: Router, readonly store: PermitApplicationStore) {}
}
