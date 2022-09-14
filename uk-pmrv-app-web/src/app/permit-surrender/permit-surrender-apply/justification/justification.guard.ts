import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { PermitSurrenderStore } from '../../store/permit-surrender.store';
import { allStepsAreValid, stopDateStepIsValid } from '../wizard-status';

@Injectable({
  providedIn: 'root',
})
export class JustificationGuard implements CanActivate {
  constructor(private readonly router: Router, private readonly store: PermitSurrenderStore) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): true | Observable<true | UrlTree> {
    return (
      this.router.getCurrentNavigation().extras?.state?.changing ||
      this.store.pipe(
        map((surrenderState) => {
          const baseUrl = state.url.slice(0, state.url.lastIndexOf(route.url[route.url.length - 1].path) - 1);
          const permitSurrender = surrenderState.permitSurrender;
          const sectionStatusCompleted = surrenderState.sectionsCompleted?.[route.data.statusKey];
          return (
            (sectionStatusCompleted && this.router.parseUrl(baseUrl.concat('/summary'))) ||
            (allStepsAreValid(permitSurrender) && this.router.parseUrl(baseUrl.concat('/answers'))) ||
            (!stopDateStepIsValid(permitSurrender) && this.router.parseUrl(baseUrl)) ||
            true
          );
        }),
      )
    );
  }
}
