import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { PermitSurrenderReviewDeterminationReject } from 'pmrv-api';

import { PermitSurrenderStore } from '../../../../store/permit-surrender.store';
import { isWizardComplete } from '../wizard';

@Injectable({
  providedIn: 'root',
})
export class RefundGuard implements CanActivate {
  constructor(private readonly store: PermitSurrenderStore, private readonly router: Router) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    return (
      this.router.getCurrentNavigation().extras?.state?.changing ||
      this.store.pipe(
        map((storeState) => {
          const taskId = route.paramMap.get('taskId');
          const wizardBaseUrl = `/permit-surrender/${taskId}/review/determination`;
          const wizardBaseRejectUrl = `${wizardBaseUrl}/reject`;

          const determination = storeState.reviewDetermination as PermitSurrenderReviewDeterminationReject;

          return (
            (storeState.reviewDeterminationCompleted && this.router.parseUrl(wizardBaseRejectUrl.concat('/summary'))) ||
            (isWizardComplete(determination) && this.router.parseUrl(`${wizardBaseRejectUrl}/answers`)) ||
            ((determination?.type !== 'REJECTED' || !determination?.reason || !determination?.officialRefusalLetter) &&
              this.router.parseUrl(wizardBaseUrl)) ||
            true
          );
        }),
      )
    );
  }
}
