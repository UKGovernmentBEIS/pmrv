import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { cessationMapper } from '@permit-revocation/cessation/confirm/constants/cessation-consts';
import { PermitRevocationStore } from '@permit-revocation/store/permit-revocation-store';

import { Wizard } from '../factory';

@Injectable({ providedIn: 'root' })
export class WizardStepsGuard implements CanActivate {
  constructor(private readonly store: PermitRevocationStore, private readonly router: Router) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    const key = route.data?.keys[0];
    const step = cessationMapper[key]?.step;
    const previousStep = step - 1;

    const isChangePermitted = this.router.getCurrentNavigation().extras?.state?.changing;
    return (
      isChangePermitted ||
      this.store.pipe(
        map((store) => {
          const sectionStatusCompleted = store.cessationCompleted;

          if (!store.allowancesSurrenderRequired && [2, 3].includes(step)) {
            return this.router.parseUrl(`/permit-revocation/${route.params['taskId']}/cessation/confirm/emissions`);
          }

          return (
            (sectionStatusCompleted &&
              this.router.parseUrl(`/permit-revocation/${route.params['taskId']}/cessation/confirm/summary`)) ||
            (Wizard.completed(store.cessation) &&
              this.router.parseUrl(`/permit-revocation/${route.params['taskId']}/cessation/confirm/answers`)) ||
            (!Wizard.stepStatus(previousStep, store.cessation, store.allowancesSurrenderRequired) &&
              this.router.parseUrl(`/permit-revocation/${route.params['taskId']}/cessation/confirm/outcome`)) ||
            true
          );
        }),
      )
    );
  }
}
