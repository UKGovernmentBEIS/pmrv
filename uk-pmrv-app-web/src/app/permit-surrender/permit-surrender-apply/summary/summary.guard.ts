import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { PermitSurrenderStore } from '../../store/permit-surrender.store';

@Injectable({ providedIn: 'root' })
export class SummaryGuard implements CanActivate {
  constructor(private readonly store: PermitSurrenderStore, private readonly router: Router) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean | UrlTree> {
    return this.store.pipe(
      map((storeState) => {
        const sectionStatusCompleted = storeState.sectionsCompleted?.[route.data.statusKey];
        return (
          !storeState.isEditable ||
          sectionStatusCompleted ||
          this.router.parseUrl(state.url.slice(0, state.url.lastIndexOf('/summary')))
        );
      }),
    );
  }
}
