import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, UrlTree } from '@angular/router';

import { first, map, Observable } from 'rxjs';

import { TransferredCO2MonitoringApproach } from 'pmrv-api';

import { PermitApplicationStore } from '../../../../store/permit-application.store';

@Injectable({ providedIn: 'root' })
export class DetailsGuard implements CanActivate {
  constructor(private readonly store: PermitApplicationStore, private readonly router: Router) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    return this.store.pipe(
      first(),
      map((state) => {
        const transferredCO2Approach = state.permit.monitoringApproaches
          ?.TRANSFERRED_CO2 as TransferredCO2MonitoringApproach;
        return (
          !!transferredCO2Approach?.receivingTransferringInstallations[route.paramMap.get('index')] ||
          this.router.parseUrl(
            `/permit-application/${route.paramMap.get('taskId')}/transferred-co2/installations/summary`,
          )
        );
      }),
    );
  }
}
