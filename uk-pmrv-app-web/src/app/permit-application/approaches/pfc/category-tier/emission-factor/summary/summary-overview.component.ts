import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, map } from 'rxjs';

import { PFCSourceStreamCategoryAppliedTier } from 'pmrv-api';

import { PermitApplicationStore } from '../../../../../store/permit-application.store';

@Component({
  selector: 'app-category-tier-emission-factor-summary-overview',
  templateUrl: './summary-overview.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SummaryOverviewComponent {
  @Input() preview = false;

  index$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('index'))));
  task$ = this.store.findTask<PFCSourceStreamCategoryAppliedTier[]>(
    'monitoringApproaches.PFC.sourceStreamCategoryAppliedTiers',
  );

  files$ = combineLatest([this.index$, this.task$]).pipe(
    map(([index, tiers]) => [...(tiers?.[index].emissionFactor?.noHighestRequiredTierJustification?.files ?? [])]),
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
