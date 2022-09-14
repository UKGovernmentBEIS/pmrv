import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Router } from '@angular/router';

import { PermitApplicationStore } from '../../../../store/permit-application.store';

@Component({
  selector: 'app-summary',
  template: `
    <app-permit-task
      [notification]="notification"
      [breadcrumb]="[{ text: 'Transferred CO2', link: ['transferred-co2'] }]"
    >
      <app-page-heading caption="Transferred CO2">Receiving and transferring installations</app-page-heading>
      <p class="govuk-body">List all the receiving and transferring installations.</p>
      <p class="govuk-body">Get help with receiving and transferring installations (link TBD).</p>
      <h2
        app-summary-header
        [changeRoute]="(store.isEditable$ | async) === true ? '..' : undefined"
        class="govuk-heading-m"
      >
        <span class="govuk-visually-hidden">List all the receiving and transferring installations</span>
      </h2>
      <app-installations-summary-template></app-installations-summary-template>
      <app-approach-return-link
        parentTitle="Transferred CO2"
        reviewGroupUrl="transferred-co2"
      ></app-approach-return-link>
    </app-permit-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SummaryComponent {
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;

  constructor(readonly store: PermitApplicationStore, private readonly router: Router) {}
}
