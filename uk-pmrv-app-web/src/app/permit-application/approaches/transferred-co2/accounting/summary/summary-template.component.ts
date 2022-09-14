import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { map } from 'rxjs';

import { AccountingEmissions, TransferredCO2MonitoringApproach } from 'pmrv-api';

import { PermitApplicationStore } from '../../../../store/permit-application.store';

@Component({
  selector: 'app-accounting-summary-template',
  templateUrl: './summary-template.component.html',
  styleUrls: ['./summary-template.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SummaryTemplateComponent {
  @Input() accounting: AccountingEmissions;
  @Input() cssClass: string;
  @Input() changePerStage: boolean;
  @Input() missingMeasurementDevice: string;

  files$ = this.store.pipe(
    map(
      (state) =>
        (state.permit.monitoringApproaches.TRANSFERRED_CO2 as TransferredCO2MonitoringApproach).accountingEmissions
          .accountingEmissionsDetails?.noHighestRequiredTierJustification?.files ?? [],
    ),
  );

  constructor(
    readonly store: PermitApplicationStore,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  changeClick(wizardStep?: string): void {
    this.router.navigate(['../' + wizardStep], { relativeTo: this.route, state: { changing: true } });
  }
}
