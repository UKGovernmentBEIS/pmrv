import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Router } from '@angular/router';

import { PermitApplicationStore } from '../../../../store/permit-application.store';

@Component({
  selector: 'app-summary',
  template: `
    <app-permit-task [notification]="notification" [breadcrumb]="[{ text: 'Fall-back approach', link: ['fall-back'] }]">
      <app-page-heading caption="Fall-back">Approach description and justification</app-page-heading>

      <h2
        app-summary-header
        [changeRoute]="(store.isEditable$ | async) === true ? '..' : undefined"
        class="govuk-heading-m"
      >
        <span class="govuk-visually-hidden">Approach description and justification</span>
      </h2>

      <app-fallback-description-summary-template></app-fallback-description-summary-template>
      <app-approach-return-link parentTitle="Fall-back" reviewGroupUrl="fall-back"></app-approach-return-link>
    </app-permit-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SummaryComponent {
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;

  constructor(readonly store: PermitApplicationStore, private readonly router: Router) {}
}
