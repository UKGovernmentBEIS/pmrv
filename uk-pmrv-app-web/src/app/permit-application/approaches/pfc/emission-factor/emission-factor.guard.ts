import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { PFCMonitoringApproach } from 'pmrv-api';

import { PermitApplicationStore } from '../../../store/permit-application.store';
import { isWizardComplete } from './emission-factor-wizard';

@Injectable({
  providedIn: 'root',
})
export class EmissionFactorGuard implements CanActivate {
  constructor(private readonly router: Router, private readonly store: PermitApplicationStore) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    return (
      this.router.getCurrentNavigation().extras?.state?.changing ||
      this.store.pipe(
        map((permitState) => {
          const wizardUrl = `/permit-application/${route.paramMap.get('taskId')}/pfc/emission-factor`;
          const emissionFactor = (permitState.permit.monitoringApproaches.PFC as PFCMonitoringApproach)
            ?.tier2EmissionFactor;

          return (
            (permitState.permitSectionsCompleted?.PFC_Tier2EmissionFactor?.[0] &&
              this.router.parseUrl(wizardUrl.concat('/summary'))) ||
            (isWizardComplete(emissionFactor) && this.router.parseUrl(wizardUrl.concat('/answers'))) ||
            true
          );
        }),
      )
    );
  }
}
