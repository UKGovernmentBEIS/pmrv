import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { N2OMonitoringApproach } from 'pmrv-api';

import { PermitApplicationStore } from '../../../../../store/permit-application.store';
import { N2OCategoryTierSubtaskStatus } from '../../../n2o-status';

@Injectable({ providedIn: 'root' })
export class CategorySummaryGuard implements CanActivate {
  constructor(private readonly store: PermitApplicationStore, private readonly router: Router) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    return this.store.pipe(
      map((state) => {
        const index = Number(route.paramMap.get('index'));
        const tiers = (state.permit.monitoringApproaches.N2O as N2OMonitoringApproach)
          ?.sourceStreamCategoryAppliedTiers;

        return (
          (tiers && !!tiers[index] && N2OCategoryTierSubtaskStatus(state, 'N2O_Category', index) === 'complete') ||
          this.router.parseUrl(
            `/permit-application/${route.paramMap.get('taskId')}/nitrous-oxide/category-tier/${index}/category`,
          )
        );
      }),
    );
  }
}
