import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { PFCMonitoringApproach } from 'pmrv-api';

import { PermitApplicationStore } from '../../../../../store/permit-application.store';
import { categoryTierSubtaskStatus } from '../../../pfc-status';

@Injectable({ providedIn: 'root' })
export class CategorySummaryGuard implements CanActivate {
  constructor(private readonly store: PermitApplicationStore, private readonly router: Router) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    return this.store.pipe(
      map((state) => {
        const index = Number(route.paramMap.get('index'));
        const tiers = (state.permit.monitoringApproaches.PFC as PFCMonitoringApproach)
          ?.sourceStreamCategoryAppliedTiers;

        return (
          (tiers && !!tiers[index] && categoryTierSubtaskStatus(state, 'PFC_Category', index) === 'complete') ||
          this.router.parseUrl(
            `/permit-application/${route.paramMap.get('taskId')}/pfc/category-tier/${index}/category`,
          )
        );
      }),
    );
  }
}
