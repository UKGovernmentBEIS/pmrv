import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { TransferredCO2MonitoringApproach } from 'pmrv-api';

import { PermitApplicationStore } from '../../../store/permit-application.store';
import { TRANSFERRED_CO2_AccountingStatus } from '../transferred-co2-status';
import { isWizardComplete } from './accounting-wizard';

@Injectable({
  providedIn: 'root',
})
export class AccountingGuard implements CanActivate {
  constructor(private readonly router: Router, private readonly store: PermitApplicationStore) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    return (
      this.router.getCurrentNavigation().extras?.state?.changing ||
      this.store.pipe(
        map((permitState) => {
          const wizardUrl = `/permit-application/${route.paramMap.get('taskId')}/transferred-co2/accounting`;
          const accountingEmissions = (
            permitState.permit.monitoringApproaches.TRANSFERRED_CO2 as TransferredCO2MonitoringApproach
          )?.accountingEmissions;

          return (
            (TRANSFERRED_CO2_AccountingStatus(permitState) === 'complete' &&
              this.router.parseUrl(wizardUrl.concat('/summary'))) ||
            (isWizardComplete(accountingEmissions) && this.router.parseUrl(wizardUrl.concat('/answers'))) ||
            true
          );
        }),
      )
    );
  }
}
