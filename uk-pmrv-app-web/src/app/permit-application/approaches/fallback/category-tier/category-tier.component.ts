import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { combineLatest, map } from 'rxjs';

import { FallbackMonitoringApproach } from 'pmrv-api';

import { PermitApplicationStore } from '../../../store/permit-application.store';
import { areCategoryTierPrerequisitesMet } from '../fallback-status';

@Component({
  selector: 'app-category-tier',
  templateUrl: './category-tier.component.html',
  styles: [
    `
      app-page-heading button {
        float: right;
      }
    `,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CategoryTierComponent {
  index$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('index'))));

  isEditable$ = combineLatest([this.index$, this.store]).pipe(
    map(
      ([index, state]) =>
        areCategoryTierPrerequisitesMet(state) ||
        !!(state.permit.monitoringApproaches.FALLBACK as FallbackMonitoringApproach)
          ?.sourceStreamCategoryAppliedTiers?.[index],
    ),
  );

  constructor(readonly store: PermitApplicationStore, private readonly route: ActivatedRoute) {}
}
