import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Router } from '@angular/router';

import { PermitApplicationStore } from '../../store/permit-application.store';

@Component({
  selector: 'app-installation-description-summary',
  template: `
    <app-permit-task
      [notification]="notification"
      [breadcrumb]="true"
      reviewGroupTitle="Installation details"
      reviewGroupUrl="details"
    >
      <app-page-heading caption="Installation details">Description of the installation</app-page-heading>
      <h2
        [changeRoute]="(store.isEditable$ | async) === true ? '..' : undefined"
        app-summary-header
        class="govuk-heading-m"
      >
        <span class="govuk-visually-hidden">List of installation categories</span>
      </h2>
      <app-description-summary-template></app-description-summary-template>
      <app-list-return-link reviewGroupTitle="Installation details" reviewGroupUrl="details"></app-list-return-link>
    </app-permit-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class InstallationDescriptionSummaryComponent {
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;

  constructor(readonly store: PermitApplicationStore, private readonly router: Router) {}
}
