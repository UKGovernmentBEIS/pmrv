import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { PermitRevocationStore } from '@permit-revocation/store/permit-revocation-store';

@Injectable({ providedIn: 'root' })
export class SummaryGuard implements CanActivate {
  constructor(private readonly store: PermitRevocationStore, private readonly router: Router) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    return this.store.pipe(
      map((store) => {
        const sectionStatusCompleted = store.cessationCompleted;
        return (
          !store.isEditable ||
          sectionStatusCompleted ||
          this.router.parseUrl(`/permit-revocation/${route.params['taskId']}/apply/reason`)
        );
      }),
    );
  }
}
