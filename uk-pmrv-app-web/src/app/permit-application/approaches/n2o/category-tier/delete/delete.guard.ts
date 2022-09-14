import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { N2OSourceStreamCategoryAppliedTier } from 'pmrv-api';

import { PermitApplicationStore } from '../../../../store/permit-application.store';

@Injectable({ providedIn: 'root' })
export class DeleteGuard implements CanActivate {
  constructor(private readonly store: PermitApplicationStore, private readonly router: Router) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    return this.store
      .findTask<N2OSourceStreamCategoryAppliedTier[]>('monitoringApproaches.N2O.sourceStreamCategoryAppliedTiers')
      .pipe(
        map(
          (tiers) =>
            (tiers && !!tiers[Number(route.paramMap.get('index'))]) ||
            this.router.parseUrl(`/permit-application/${route.paramMap.get('taskId')}/nitrous-oxide`),
        ),
      );
  }
}
