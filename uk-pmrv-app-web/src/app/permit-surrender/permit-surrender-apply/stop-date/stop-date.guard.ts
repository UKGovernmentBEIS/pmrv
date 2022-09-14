import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { PermitSurrenderStore } from '../../store/permit-surrender.store';
import { allStepsAreValid } from '../wizard-status';

@Injectable({
  providedIn: 'root',
})
export class StopDateGuard implements CanActivate {
  constructor(private readonly router: Router, private readonly store: PermitSurrenderStore) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): true | Observable<true | UrlTree> {
    return (
      this.router.getCurrentNavigation().extras?.state?.changing ||
      this.store.pipe(
        map((surrenderState) => {
          const permitSurrender = surrenderState.permitSurrender;
          const sectionStatusCompleted = surrenderState.sectionsCompleted?.[route.data.statusKey];

          return (
            (sectionStatusCompleted && this.router.parseUrl(state.url.concat('/summary'))) ||
            (allStepsAreValid(permitSurrender) && this.router.parseUrl(state.url.concat('/answers'))) ||
            true
          );
        }),
      )
    );
  }
}
