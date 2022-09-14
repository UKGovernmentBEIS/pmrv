import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { PermitRevocationStore } from '@permit-revocation/store/permit-revocation-store';


@Injectable({
  providedIn: 'root',
})
export class WithdrawReasonGuard implements CanActivate {
  constructor(private readonly router: Router, private readonly store: PermitRevocationStore) {}

  canActivate(route: ActivatedRouteSnapshot): true | Observable<true | UrlTree> {
    return (
      this.router.getCurrentNavigation().extras?.state?.changing ||
      this.store.pipe(
        map((store) => {
          const reason = store.reason;
          return (
            (reason && this.router.parseUrl(`/permit-revocation/${route.params['taskId']}/wait-for-appeal/summary`)) ||
            true
          );
        }),
      )
    );
  }
}
