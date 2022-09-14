import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { MeasMonitoringApproach, N2OMonitoringApproach } from 'pmrv-api';

import { MEASUREMENTCategoryTierSubtaskStatus } from '../../approaches/measurement/measurement-status';
import { N2OCategoryTierSubtaskStatus } from '../../approaches/n2o/n2o-status';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { isWizardComplete } from './emissions-wizard';

@Injectable({
  providedIn: 'root',
})
export class EmissionsGuard implements CanActivate {
  constructor(private readonly router: Router, private readonly store: PermitApplicationStore) {}

  canActivate(
    route: ActivatedRouteSnapshot,
  ): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
    return (
      this.router.getCurrentNavigation().extras?.state?.changing ||
      this.store.pipe(
        map((permitState) => {
          const { taskKey } = route.data;
          const index = route.paramMap.get('index');

          const wizardUrl = `/permit-application/${route.paramMap.get('taskId')}/${
            taskKey === 'MEASUREMENT' ? 'measurement' : 'nitrous-oxide'
          }/category-tier/${index}/emissions`;

          const measuredEmissions = (
            permitState.permit.monitoringApproaches[taskKey] as N2OMonitoringApproach | MeasMonitoringApproach
          )?.sourceStreamCategoryAppliedTiers?.[index]?.measuredEmissions;

          const taskStatus =
            taskKey === 'MEASUREMENT'
              ? MEASUREMENTCategoryTierSubtaskStatus(permitState, 'MEASUREMENT_Measured_Emissions', Number(index))
              : N2OCategoryTierSubtaskStatus(permitState, 'N2O_Measured_Emissions', Number(index));

          return (
            (taskStatus === 'complete' && this.router.parseUrl(wizardUrl.concat('/summary'))) ||
            (isWizardComplete(taskKey, measuredEmissions) && this.router.parseUrl(wizardUrl.concat('/answers'))) ||
            true
          );
        }),
      )
    );
  }
}
