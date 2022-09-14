import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Router } from '@angular/router';

import { PermitApplicationStore } from '../../../../store/permit-application.store';

@Component({
  selector: 'app-summary',
  template: `
    <app-permit-task [notification]="notification" [breadcrumb]="[{ text: 'Inherent CO2', link: ['inherent-co2'] }]">
      <app-page-heading caption="Inherent CO2">Approach description</app-page-heading>

      <p class="govuk-body">
        Explain how inherent CO2 will be accounted for through the use of a calculation or measurement method as defined
        in the MRR.
      </p>

      <h2
        app-summary-header
        [changeRoute]="(store.isEditable$ | async) === true ? '..' : undefined"
        class="govuk-heading-m"
      >
        <span class="govuk-visually-hidden">Approach description</span>
      </h2>
      <app-inherent-co2-description-summary-template></app-inherent-co2-description-summary-template>

      <app-approach-return-link parentTitle="Inherent CO2" reviewGroupUrl="inherent-co2"></app-approach-return-link>
    </app-permit-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SummaryComponent {
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;

  constructor(readonly store: PermitApplicationStore, private readonly router: Router) {}
}
