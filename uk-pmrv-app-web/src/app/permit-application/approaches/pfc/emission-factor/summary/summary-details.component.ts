import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { PFCTier2EmissionFactor } from 'pmrv-api';

import { PermitApplicationStore } from '../../../../store/permit-application.store';

@Component({
  selector: 'app-emission-factor-summary-details',
  templateUrl: './summary-details.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SummaryDetailsComponent {
  @Input() emissionFactor: PFCTier2EmissionFactor;
  @Input() cssClass: string;
  @Input() changePerStage: boolean;
  @Input() hasBottomBorder = true;

  constructor(
    readonly store: PermitApplicationStore,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  changeClick(wizardStep?: string): void {
    this.router.navigate(['../' + wizardStep], { relativeTo: this.route, state: { changing: true } });
  }
}
