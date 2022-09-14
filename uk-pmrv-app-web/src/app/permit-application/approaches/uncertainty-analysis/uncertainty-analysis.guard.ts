import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { PermitApplicationStore } from '../../store/permit-application.store';

@Injectable({
  providedIn: 'root',
})
export class UncertaintyAnalysisGuard implements CanActivate {
  constructor(private readonly router: Router, private readonly store: PermitApplicationStore) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): true | Observable<true | UrlTree> {
    return (
      this.router.getCurrentNavigation().extras?.state?.changing ||
      this.store.pipe(
        map((permitState) => {
          const uncertaintyAnalysis = permitState.permit?.uncertaintyAnalysis;

          return (
            (permitState.permitSectionsCompleted?.uncertaintyAnalysis?.[0] &&
              this.router.parseUrl(state.url.concat('/summary'))) ||
            uncertaintyAnalysis?.exist === undefined ||
            (uncertaintyAnalysis?.exist === true && !uncertaintyAnalysis?.attachments) ||
            (uncertaintyAnalysis?.exist === true &&
              uncertaintyAnalysis?.attachments.length &&
              this.router.parseUrl(state.url.concat('/answers'))) ||
            (uncertaintyAnalysis?.exist === false && this.router.parseUrl(state.url.concat('/answers')))
          );
        }),
      )
    );
  }
}
