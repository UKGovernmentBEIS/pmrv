import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { PermitSurrenderStore } from '../../store/permit-surrender.store';
import { DeterminationTypeUrlMap } from '../core/review';
import { isWizardComplete } from './determination';

@Injectable({ providedIn: 'root' })
export class DeterminationGuard implements CanActivate {
  constructor(private readonly store: PermitSurrenderStore, private readonly router: Router) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    return (
      this.router.getCurrentNavigation().extras?.state?.changing ||
      this.store.pipe(
        map((storeState) => {
          const taskId = route.paramMap.get('taskId');
          const type = storeState?.reviewDetermination?.type;
          const wizardBaseTypeUrl = `/permit-surrender/${taskId}/review/determination/${DeterminationTypeUrlMap[type]}`;

          return (
            (storeState.reviewDeterminationCompleted && this.router.parseUrl(wizardBaseTypeUrl.concat('/summary'))) ||
            (isWizardComplete(storeState?.reviewDetermination) &&
              this.router.parseUrl(wizardBaseTypeUrl.concat('/answers'))) ||
            true
          );
        }),
      )
    );
  }
}
