import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { PermitApplicationStore } from '../../../store/permit-application.store';
import { isWizardComplete } from '../determination-wizard';

@Injectable({ providedIn: 'root' })
export class EmissionsGuard implements CanActivate {
  constructor(private readonly store: PermitApplicationStore, private readonly router: Router) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    return (
      this.router.getCurrentNavigation().extras?.state?.changing ||
      this.store.pipe(
        map((storeState) => {
          const wizardUrl = `/permit-application/${route.paramMap.get('taskId')}/review/determination`;

          return (
            (storeState.reviewSectionsCompleted?.[route.data.statusKey] &&
              this.router.parseUrl(wizardUrl.concat('/summary'))) ||
            (isWizardComplete(storeState.determination, storeState.permitType) &&
              this.router.parseUrl(wizardUrl.concat('/answers'))) ||
            ((storeState.permitType !== 'HSE' ||
              storeState.determination?.type !== 'GRANTED' ||
              !storeState.determination?.reason ||
              !storeState.determination?.activationDate) &&
              this.router.parseUrl(wizardUrl)) ||
            true
          );
        }),
      )
    );
  }
}
