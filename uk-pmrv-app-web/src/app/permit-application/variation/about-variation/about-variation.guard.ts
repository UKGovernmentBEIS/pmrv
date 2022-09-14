import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { PermitApplicationStore } from '../../store/permit-application.store';

@Injectable({
  providedIn: 'root',
})
export class AboutVariationGuard implements CanActivate {
  constructor(private readonly router: Router, private readonly store: PermitApplicationStore) {}

  canActivate(route: ActivatedRouteSnapshot): true | Observable<true | UrlTree> {
    return (
      this.router.getCurrentNavigation().extras?.state?.changing ||
      this.store.pipe(
        map((permitState) => {
          const taskId = route.paramMap.get('taskId');
          const wizardBaseUrl = `/permit-application/${taskId}/about`;

          const permitVariationDetails = permitState?.permitVariationDetails;

          return (
            (permitState.permitVariationDetailsCompleted && this.router.parseUrl(wizardBaseUrl.concat('/summary'))) ||
            (permitVariationDetails?.reason &&
              permitVariationDetails?.modifications &&
              this.router.parseUrl(wizardBaseUrl.concat('/answers'))) ||
            true
          );
        }),
      )
    );
  }
}
