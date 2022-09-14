import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { CalculationMonitoringApproach } from 'pmrv-api';

import { PermitApplicationStore } from '../../../../../store/permit-application.store';
import { categoryTierSubtaskStatus } from '../../../calculation-status';

@Injectable({ providedIn: 'root' })
export class CategorySummaryGuard implements CanActivate {
  constructor(private readonly store: PermitApplicationStore, private readonly router: Router) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    return this.store.pipe(
      map((state) => {
        const index = Number(route.paramMap.get('index'));
        const tiers = (state.permit.monitoringApproaches.CALCULATION as CalculationMonitoringApproach)
          ?.sourceStreamCategoryAppliedTiers;

        return (
          (tiers && !!tiers[index] && categoryTierSubtaskStatus(state, 'CALCULATION_Category', index) === 'complete') ||
          this.router.parseUrl(
            `/permit-application/${route.paramMap.get('taskId')}/calculation/category-tier/${index}/category`,
          )
        );
      }),
    );
  }
}
