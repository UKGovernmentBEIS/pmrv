import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { TransferredCO2MonitoringApproach } from 'pmrv-api';

import { PermitApplicationStore } from '../../../../store/permit-application.store';
import { TRANSFERRED_CO2_AccountingStatus } from '../../transferred-co2-status';
import { isWizardComplete } from '../accounting-wizard';

@Injectable({ providedIn: 'root' })
export class AccountingAnswersGuard implements CanActivate {
  constructor(private readonly store: PermitApplicationStore, private readonly router: Router) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    return this.store.pipe(
      map((state) => {
        const wizardUrl = `/permit-application/${route.paramMap.get('taskId')}/transferred-co2/accounting`;
        const accountingEmissions = (
          state.permit.monitoringApproaches.TRANSFERRED_CO2 as TransferredCO2MonitoringApproach
        )?.accountingEmissions;

        return (
          (TRANSFERRED_CO2_AccountingStatus(state) === 'complete' &&
            this.router.parseUrl(wizardUrl.concat('/summary'))) ||
          isWizardComplete(accountingEmissions) ||
          this.router.parseUrl(wizardUrl)
        );
      }),
    );
  }
}
