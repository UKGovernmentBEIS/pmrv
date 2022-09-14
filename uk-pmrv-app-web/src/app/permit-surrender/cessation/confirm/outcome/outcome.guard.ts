import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { PermitSurrenderStore } from '../../../store/permit-surrender.store';
import { isWizardComplete } from '../wizard';

@Injectable({
  providedIn: 'root',
})
export class OutcomeGuard implements CanActivate {
  constructor(private readonly store: PermitSurrenderStore, private readonly router: Router) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    return (
      this.router.getCurrentNavigation().extras?.state?.changing ||
      this.store.pipe(
        map((storeState) => {
          const taskId = route.paramMap.get('taskId');
          const wizardBaseUrl = `/permit-surrender/${taskId}/cessation/confirm`;
          const cessation = storeState.cessation;
          const allowancesSurrenderRequired = storeState.allowancesSurrenderRequired;

          return (
            (storeState.cessationCompleted && this.router.parseUrl(wizardBaseUrl.concat('/summary'))) ||
            (isWizardComplete(cessation, allowancesSurrenderRequired) &&
              this.router.parseUrl(wizardBaseUrl.concat('/answers'))) ||
            true
          );
        }),
      )
    );
  }
}
