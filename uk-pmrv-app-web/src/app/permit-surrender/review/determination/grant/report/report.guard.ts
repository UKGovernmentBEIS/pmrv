import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { PermitSurrenderReviewDeterminationGrant } from 'pmrv-api';

import { PermitSurrenderStore } from '../../../../store/permit-surrender.store';
import { isWizardComplete } from '../wizard';

@Injectable({
  providedIn: 'root',
})
export class ReportGuard implements CanActivate {
  constructor(private readonly store: PermitSurrenderStore, private readonly router: Router) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    return (
      this.router.getCurrentNavigation().extras?.state?.changing ||
      this.store.pipe(
        map((storeState) => {
          const taskId = route.paramMap.get('taskId');
          const wizardBaseUrl = `/permit-surrender/${taskId}/review/determination`;
          const wizardBaseGrantUrl = `${wizardBaseUrl}/grant`;

          const determination = storeState.reviewDetermination as PermitSurrenderReviewDeterminationGrant;

          return (
            (storeState.reviewDeterminationCompleted && this.router.parseUrl(wizardBaseGrantUrl.concat('/summary'))) ||
            (isWizardComplete(determination) && this.router.parseUrl(`${wizardBaseGrantUrl}/answers`)) ||
            ((determination?.type !== 'GRANTED' || !determination?.stopDate || !determination?.noticeDate) &&
              this.router.parseUrl(wizardBaseUrl)) ||
            true
          );
        }),
      )
    );
  }
}
