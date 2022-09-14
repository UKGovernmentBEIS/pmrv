import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { CalculationMonitoringApproach } from 'pmrv-api';

import { PermitApplicationStore } from '../../../store/permit-application.store';

@Injectable({ providedIn: 'root' })
export class SamplingPlanGuard implements CanActivate {
  constructor(private readonly router: Router, private readonly store: PermitApplicationStore) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): true | Observable<true | UrlTree> {
    return (
      this.router.getCurrentNavigation().extras?.state?.changing ||
      this.store.pipe(
        map((permitState) => {
          const plan = (permitState.permit.monitoringApproaches.CALCULATION as CalculationMonitoringApproach)
            ?.samplingPlan;
          return (
            (permitState.permitSectionsCompleted?.CALCULATION_Plan?.[0] &&
              this.router.parseUrl(state.url.concat('/summary'))) ||
            plan?.exist === undefined ||
            (plan?.exist === true &&
              (plan?.details?.procedurePlan === undefined ||
                plan?.details?.appropriateness === undefined ||
                plan?.details?.yearEndReconciliation === undefined)) ||
            this.router.parseUrl(state.url.concat('/answers'))
          );
        }),
      )
    );
  }
}
