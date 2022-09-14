import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Router } from '@angular/router';

import { PermitApplicationStore } from '../../../../store/permit-application.store';

@Component({
  selector: 'app-summary',
  template: `
    <app-permit-task [notification]="notification" [breadcrumb]="[{ text: 'Measurement', link: ['measurement'] }]">
      <app-page-heading caption="Measurement">Approach description</app-page-heading>

      <app-hint></app-hint>

      <h2
        app-summary-header
        [changeRoute]="(store.isEditable$ | async) === true ? '..' : undefined"
        class="govuk-heading-m"
      >
        <span class="govuk-visually-hidden">Approach description</span>
      </h2>

      <app-measurement-approach-description-summary-template></app-measurement-approach-description-summary-template>

      <app-approach-return-link parentTitle="Measurement" reviewGroupUrl="measurement"></app-approach-return-link>
    </app-permit-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SummaryComponent {
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;

  constructor(readonly store: PermitApplicationStore, private readonly router: Router) {}
}
