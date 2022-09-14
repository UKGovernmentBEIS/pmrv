import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { PermitSurrenderStore } from '../../store/permit-surrender.store';

@Injectable({
  providedIn: 'root',
})
export class NotifyOperatorGuard implements CanActivate {
  constructor(private readonly store: PermitSurrenderStore, private readonly router: Router) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    return this.store.pipe(
      map((state) => {
        return (
          (state.allowedRequestTaskActions.includes('PERMIT_SURRENDER_CESSATION_NOTIFY_OPERATOR_FOR_DECISION') &&
            state?.cessationCompleted) ||
          this.router.parseUrl(`/permit-surrender/${route.paramMap.get('taskId')}/cessation`)
        );
      }),
    );
  }
}
