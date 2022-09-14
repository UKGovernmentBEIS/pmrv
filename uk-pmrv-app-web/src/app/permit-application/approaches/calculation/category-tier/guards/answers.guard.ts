import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { PermitApplicationStore } from '../../../../store/permit-application.store';
import { categoryTierSubtaskStatus } from '../../calculation-status';
import { statusKeyTosubtaskUrlParamMapper } from '../category-tier';
import { isWizardComplete } from '../category-tier-wizard';

@Injectable({
  providedIn: 'root',
})
export class AnswersGuard implements CanActivate {
  constructor(private readonly router: Router, private readonly store: PermitApplicationStore) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<true | UrlTree> {
    return this.store.pipe(
      map((state) => {
        const index = Number(route.paramMap.get('index'));
        const taskId = route.paramMap.get('taskId');
        const statusKey = route.data.statusKey;

        const status = categoryTierSubtaskStatus(state, statusKey, Number(index));
        const subtaskUrlParam = statusKeyTosubtaskUrlParamMapper[statusKey];

        const wizardUrl = `/permit-application/${taskId}/calculation/category-tier/${index}/${subtaskUrlParam}`;

        return (
          (status === 'complete' && this.router.parseUrl(wizardUrl.concat('/summary'))) ||
          isWizardComplete(state, index, statusKey) ||
          this.router.parseUrl(wizardUrl)
        );
      }),
    );
  }
}
