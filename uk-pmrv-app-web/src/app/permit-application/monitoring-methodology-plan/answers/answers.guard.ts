import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { PermitApplicationStore } from '../../store/permit-application.store';

@Injectable({
  providedIn: 'root',
})
export class AnswersGuard implements CanActivate {
  constructor(private readonly router: Router, private readonly store: PermitApplicationStore) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): true | Observable<true | UrlTree> {
    return (
      this.router.getCurrentNavigation().extras?.state?.changing ||
      this.store.pipe(
        map((permitState) => {
          const baseUrl = state.url.slice(0, state.url.lastIndexOf(route.url[route.url.length - 1].path) - 1);
          const monitoringMethodologyPlans = permitState.permit?.monitoringMethodologyPlans;

          return (
            (monitoringMethodologyPlans?.exist === true && !!monitoringMethodologyPlans?.plans) ||
            (permitState.permitSectionsCompleted?.monitoringMethodologyPlans?.[0] &&
              this.router.parseUrl(baseUrl.concat('/summary'))) ||
            (monitoringMethodologyPlans?.exist === undefined && this.router.parseUrl(baseUrl)) ||
            (monitoringMethodologyPlans?.exist === true &&
              !monitoringMethodologyPlans?.plans &&
              this.router.parseUrl(baseUrl)) ||
            monitoringMethodologyPlans?.exist === false
          );
        }),
      )
    );
  }
}
