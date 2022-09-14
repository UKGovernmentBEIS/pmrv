import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { PermitApplicationStore } from '../../store/permit-application.store';

@Injectable({
  providedIn: 'root',
})
export class NotifyOperatorGuard implements CanActivate {
  constructor(private readonly store: PermitApplicationStore, private readonly router: Router) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    return this.store.pipe(
      map((state) => {
        return (
          (state.allowedRequestTaskActions.includes('PERMIT_ISSUANCE_NOTIFY_OPERATOR_FOR_DECISION') &&
            state?.reviewSectionsCompleted?.determination === true) ||
          this.router.parseUrl(`/permit-application/${route.paramMap.get('taskId')}/review`)
        );
      }),
    );
  }
}
