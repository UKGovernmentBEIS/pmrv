import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { PFCMonitoringApproach } from 'pmrv-api';

import { PermitApplicationStore } from '../../../../store/permit-application.store';
import { isWizardComplete } from '../emission-factor-wizard';

@Injectable({ providedIn: 'root' })
export class AnswersGuard implements CanActivate {
  constructor(private readonly store: PermitApplicationStore, private readonly router: Router) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    return this.store.pipe(
      map((state) => {
        const wizardUrl = `/permit-application/${route.paramMap.get('taskId')}/pfc/emission-factor`;
        const emissionFactor = (state.permit.monitoringApproaches.PFC as PFCMonitoringApproach)?.tier2EmissionFactor;

        return (
          (state.permitSectionsCompleted?.PFC_Tier2EmissionFactor?.[0] &&
            this.router.parseUrl(wizardUrl.concat('/summary'))) ||
          isWizardComplete(emissionFactor) ||
          this.router.parseUrl(wizardUrl)
        );
      }),
    );
  }
}
