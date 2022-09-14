import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, map } from 'rxjs';

import { CalculationSourceStreamCategoryAppliedTier } from 'pmrv-api';

import { PermitApplicationStore } from '../../../../../store/permit-application.store';

@Component({
  selector: 'app-activity-data-summary-overview',
  templateUrl: './summary-overview.component.html',
  styleUrls: ['./summary-overview.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SummaryOverviewComponent {
  @Input() isChangeLinkAvailable: boolean;
  @Input() activityDataSectionHasBottomBorder: boolean;
  @Input() justificationSectionHasBottomBorder: boolean;
  @Input() validMeasurementDevicesOrMethodsError: string;

  index$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('index'))));
  task$ = this.store.findTask<CalculationSourceStreamCategoryAppliedTier[]>(
    'monitoringApproaches.CALCULATION.sourceStreamCategoryAppliedTiers',
  );
  files$ = combineLatest([this.index$, this.task$]).pipe(
    map(([index, tiers]) => [...(tiers?.[index].activityData?.noHighestRequiredTierJustification?.files ?? [])]),
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
