import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { MeasSourceStreamCategoryAppliedTier } from 'pmrv-api';

import { PermitApplicationStore } from '../../../store/permit-application.store';

@Injectable({ providedIn: 'root' })
export class CategoryTierGuard implements CanActivate {
  constructor(private readonly store: PermitApplicationStore, private readonly router: Router) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    const index = Number(route.paramMap.get('index'));
    return this.store
      .findTask<MeasSourceStreamCategoryAppliedTier[]>(
        'monitoringApproaches.MEASUREMENT.sourceStreamCategoryAppliedTiers',
      )
      .pipe(
        map(
          (tiers) =>
            (tiers && (!!tiers[index] || tiers.length === index)) ||
            (!tiers && index === 0) ||
            this.router.parseUrl(`/permit-application/${route.paramMap.get('taskId')}/measurement`),
        ),
      );
  }
}
