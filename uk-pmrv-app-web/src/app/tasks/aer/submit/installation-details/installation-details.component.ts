import { ChangeDetectionStrategy, Component } from '@angular/core';

import { map } from 'rxjs';

import { AerService } from '@tasks/aer/core/aer.service';

import { AerApplicationSubmitRequestTaskPayload } from 'pmrv-api';

@Component({
  selector: 'app-installation-details',
  template: `
    <app-aer-task [breadcrumb]="true">
      <app-page-heading caption="Installation details">Installation and operator details</app-page-heading>
      <app-installation-details-summary
        cssClass="summary-list--edge-border"
        [installationOperatorDetails]="installationOperatorDetails$ | async"
      ></app-installation-details-summary>
      <app-return-link></app-return-link>
    </app-aer-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class InstallationDetailsComponent {
  installationOperatorDetails$ = this.aerService
    .getPayload()
    .pipe(map((payload: AerApplicationSubmitRequestTaskPayload) => payload.installationOperatorDetails));

  constructor(private readonly aerService: AerService) {}
}
