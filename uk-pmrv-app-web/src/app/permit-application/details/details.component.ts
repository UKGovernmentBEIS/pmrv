import { ChangeDetectionStrategy, Component } from '@angular/core';

import { PermitApplicationStore } from '../store/permit-application.store';

@Component({
  selector: 'app-permit-details',
  template: `
    <app-permit-task [breadcrumb]="true">
      <app-page-heading caption="Installation details">Installation and operator details</app-page-heading>
      <app-installation-details-summary
        cssClass="summary-list--edge-border"
        [installationOperatorDetails]="installationOperatorDetails$ | async"
      ></app-installation-details-summary>
      <app-list-return-link></app-list-return-link>
    </app-permit-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DetailsComponent {
  constructor(private readonly store: PermitApplicationStore) {}
  installationOperatorDetails$ = this.store.select('installationOperatorDetails');
}
