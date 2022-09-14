import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { PermitApplicationStore } from '../store/permit-application.store';

@Injectable({
  providedIn: 'root',
})
export class MonitoringMethodologyPlanGuard implements CanActivate {
  constructor(private readonly router: Router, private readonly store: PermitApplicationStore) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): true | Observable<true | UrlTree> {
    return (
      this.router.getCurrentNavigation().extras?.state?.changing ||
      this.store.pipe(
        map((permitState) => {
          const monitoringMethodologyPlans = permitState.permit?.monitoringMethodologyPlans;

          return (
            (permitState.permitSectionsCompleted?.monitoringMethodologyPlans?.[0] &&
              this.router.parseUrl(state.url.concat('/summary'))) ||
            monitoringMethodologyPlans?.exist === undefined ||
            (monitoringMethodologyPlans?.exist === true && !monitoringMethodologyPlans?.plans) ||
            (monitoringMethodologyPlans?.exist === true &&
              monitoringMethodologyPlans?.plans.length &&
              this.router.parseUrl(state.url.concat('/answers'))) ||
            (monitoringMethodologyPlans?.exist === false && this.router.parseUrl(state.url.concat('/answers')))
          );
        }),
      )
    );
  }
}
