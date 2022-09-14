import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { PermitSurrenderStore } from '../../../../store/permit-surrender.store';
import { DeterminationTypeUrlMap } from '../../../core/review';
import { isWizardComplete } from '../../grant/wizard';

@Injectable({ providedIn: 'root' })
export class ReasonGuard implements CanActivate {
  constructor(private readonly store: PermitSurrenderStore, private readonly router: Router) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    return (
      this.router.getCurrentNavigation().extras?.state?.changing ||
      this.store.pipe(
        map((storeState) => {
          const taskId = route.paramMap.get('taskId');
          const type = storeState?.reviewDetermination?.type;
          const wizardBaseUrl = `/permit-surrender/${taskId}/review/determination`;
          const wizardBaseTypeUrl = `${wizardBaseUrl}/${DeterminationTypeUrlMap[type]}`;
          return (
            (storeState.reviewDeterminationCompleted && this.router.parseUrl(`${wizardBaseTypeUrl}/summary`)) ||
            (!type && this.router.parseUrl(wizardBaseUrl)) ||
            (isWizardComplete(storeState.reviewDetermination as any) &&
              this.router.parseUrl(`${wizardBaseTypeUrl}/answers`)) ||
            true
          );
        }),
      )
    );
  }
}
