import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Router } from '@angular/router';

import { PermitApplicationStore } from '../../../../store/permit-application.store';

@Component({
  selector: 'app-summary',
  template: `
    <app-permit-task [notification]="notification" [breadcrumb]="[{ text: 'Perfluorocarbons', link: ['pfc'] }]">
      <app-page-heading caption="Perfluorocarbons">Tier 2 - Emission factor</app-page-heading>
      <app-emission-factor-summary-details
        [emissionFactor]="('PFC' | monitoringApproachTask | async).tier2EmissionFactor"
        [changePerStage]="true"
        cssClass="summary-list--edge-border"
      ></app-emission-factor-summary-details>
      <app-approach-return-link parentTitle="Perfluorocarbons" reviewGroupUrl="pfc"></app-approach-return-link>
    </app-permit-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SummaryComponent {
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;

  constructor(readonly store: PermitApplicationStore, private readonly router: Router) {}
}
