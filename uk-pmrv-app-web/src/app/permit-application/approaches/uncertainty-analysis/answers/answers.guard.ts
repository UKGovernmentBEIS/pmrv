import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { PermitApplicationStore } from '../../../store/permit-application.store';

@Injectable({
  providedIn: 'root',
})
export class AnswersGuard implements CanActivate {
  constructor(private readonly router: Router, private readonly store: PermitApplicationStore) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): true | Observable<true | UrlTree> {
    return (
      this.router.getCurrentNavigation().extras?.state?.changing ||
      this.store.pipe(
        map((permitState) => {
          const baseUrl = state.url.slice(0, state.url.lastIndexOf(route.url[route.url.length - 1].path) - 1);
          const uncertaintyAnalysis = permitState.permit?.uncertaintyAnalysis;

          return (
            (uncertaintyAnalysis?.exist === true && !!uncertaintyAnalysis?.attachments) ||
            (permitState.permitSectionsCompleted?.uncertaintyAnalysis?.[0] &&
              this.router.parseUrl(baseUrl.concat('/summary'))) ||
            (uncertaintyAnalysis?.exist === undefined && this.router.parseUrl(baseUrl)) ||
            (uncertaintyAnalysis?.exist === true &&
              !uncertaintyAnalysis?.attachments &&
              this.router.parseUrl(baseUrl)) ||
            uncertaintyAnalysis?.exist === false
          );
        }),
      )
    );
  }
}
