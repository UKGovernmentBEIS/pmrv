import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { PermitSurrenderStore } from '../../store/permit-surrender.store';

@Injectable({ providedIn: 'root' })
export class SummaryGuard implements CanActivate {
  constructor(private readonly store: PermitSurrenderStore, private readonly router: Router) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    return this.store.pipe(
      map((storeState) => {
        const taskId = route.paramMap.get('taskId');
        const wizardBaseUrl = `/permit-surrender/${taskId}/review/determination`;

        return storeState.reviewDeterminationCompleted || this.router.parseUrl(wizardBaseUrl);
      }),
    );
  }
}
