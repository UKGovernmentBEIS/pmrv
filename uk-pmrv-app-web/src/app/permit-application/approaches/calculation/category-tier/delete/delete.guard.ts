import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { CalculationSourceStreamCategoryAppliedTier } from 'pmrv-api';

import { PermitApplicationStore } from '../../../../store/permit-application.store';

@Injectable({ providedIn: 'root' })
export class DeleteGuard implements CanActivate {
  constructor(private readonly store: PermitApplicationStore, private readonly router: Router) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    return this.store
      .findTask<CalculationSourceStreamCategoryAppliedTier[]>(
        'monitoringApproaches.CALCULATION.sourceStreamCategoryAppliedTiers',
      )
      .pipe(
        map(
          (tiers) =>
            (tiers && !!tiers[Number(route.paramMap.get('index'))]) ||
            this.router.parseUrl(`/permit-application/${route.paramMap.get('taskId')}/calculation`),
        ),
      );
  }
}
