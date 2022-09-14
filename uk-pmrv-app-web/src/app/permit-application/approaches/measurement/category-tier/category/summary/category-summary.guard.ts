import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { MeasMonitoringApproach } from 'pmrv-api';

import { PermitApplicationStore } from '../../../../../store/permit-application.store';
import { MEASUREMENTCategoryTierSubtaskStatus } from '../../../measurement-status';

@Injectable({ providedIn: 'root' })
export class CategorySummaryGuard implements CanActivate {
  constructor(private readonly store: PermitApplicationStore, private readonly router: Router) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    return this.store.pipe(
      map((state) => {
        const index = Number(route.paramMap.get('index'));
        const tiers = (state.permit.monitoringApproaches.MEASUREMENT as MeasMonitoringApproach)
          ?.sourceStreamCategoryAppliedTiers;

        return (
          !state.isRequestTask ||
          (tiers &&
            !!tiers[index] &&
            MEASUREMENTCategoryTierSubtaskStatus(state, 'MEASUREMENT_Category', index) === 'complete') ||
          this.router.parseUrl(
            `/permit-application/${route.paramMap.get('taskId')}/measurement/category-tier/${index}/category`,
          )
        );
      }),
    );
  }
}
