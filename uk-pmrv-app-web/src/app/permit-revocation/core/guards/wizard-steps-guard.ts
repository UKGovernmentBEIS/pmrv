import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { permitRevocationMapper } from '@permit-revocation/constants/permit-revocation-consts';
import { PermitRevocationStore } from '@permit-revocation/store/permit-revocation-store';

import { Wizard } from '../../factory';

@Injectable({ providedIn: 'root' })
export class WizardStepsGuard implements CanActivate {
  constructor(private readonly store: PermitRevocationStore, private readonly router: Router) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    const key = route.data?.keys[0];
    const step = permitRevocationMapper[key]?.step;
    const previousStep = step - 1;

    const isChangePermitted = this.router.getCurrentNavigation().extras?.state?.changing;
    return (
      isChangePermitted ||
      this.store.pipe(
        map((store) => {
          const sectionStatusCompleted = store.sectionsCompleted?.[route.data.statusKey];

          return (
            (sectionStatusCompleted &&
              this.router.parseUrl(`/permit-revocation/${route.params['taskId']}/apply/summary`)) ||
            (Wizard.completed(store.permitRevocation) &&
              this.router.parseUrl(`/permit-revocation/${route.params['taskId']}/apply/answers`)) ||
            (!Wizard.stepStatus(previousStep, store.permitRevocation) &&
              this.router.parseUrl(`/permit-revocation/${route.params['taskId']}/apply/reason`)) ||
            true
          );
        }),
      )
    );
  }
}
