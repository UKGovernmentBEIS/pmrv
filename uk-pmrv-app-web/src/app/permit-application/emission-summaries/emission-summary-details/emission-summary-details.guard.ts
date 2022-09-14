import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, UrlTree } from '@angular/router';

import { first, map, Observable } from 'rxjs';

import { PermitApplicationStore } from '../../store/permit-application.store';

@Injectable({ providedIn: 'root' })
export class EmissionSummaryDetailsGuard implements CanActivate {
  constructor(private readonly store: PermitApplicationStore, private readonly router: Router) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    return this.store.getTask('emissionSummaries').pipe(
      first(),
      map(
        (emissionSummaries) =>
          !!emissionSummaries[Number(route.paramMap.get('emissionSummaryIndex') ?? -1)] ||
          this.router.parseUrl(`/permit-application/${route.paramMap.get('taskId')}/emission-summaries/summary`),
      ),
    );
  }
}
