import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { Wizard } from '@permit-revocation/factory';
import { PermitRevocationStore } from '@permit-revocation/store/permit-revocation-store';

@Injectable({ providedIn: 'root' })
export class ConfirmSubmitGuard implements CanActivate {
  constructor(private readonly store: PermitRevocationStore, private readonly router: Router) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    return this.store.pipe(
      map((store) => {
        const sectionStatusCompleted = store.sectionsCompleted?.[route.data.statusKey];
        return (
          (sectionStatusCompleted &&
            this.router.parseUrl(`/permit-revocation/${route.params['taskId']}/apply/summary`)) ||
          (!Wizard.completed(store.permitRevocation) &&
            this.router.parseUrl(`/permit-revocation/${route.params['taskId']}`)) ||
          Wizard.completed(store.permitRevocation)
        );
      }),
    );
  }
}
