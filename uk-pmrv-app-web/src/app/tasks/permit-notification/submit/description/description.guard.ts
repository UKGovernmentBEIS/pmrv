import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, UrlTree } from '@angular/router';

import { first, map, Observable } from 'rxjs';

import { PermitNotificationService } from '../../core/permit-notification.service';
import { isWizardComplete } from '../../core/section-status';

@Injectable({
  providedIn: 'root',
})
export class DescriptionGuard implements CanActivate {
  constructor(private readonly router: Router, private readonly permitNotificationService: PermitNotificationService) {}

  canActivate(
    route: ActivatedRouteSnapshot,
  ): boolean | UrlTree | Observable<boolean | UrlTree> | Promise<boolean | UrlTree> {
    return (
      this.router.getCurrentNavigation().extras?.state?.changing ||
      this.permitNotificationService.getPayload().pipe(
        first(),
        map((storeState) => {
          const wizardUrl = `/tasks/${route.paramMap.get('taskId')}/permit-notification/submit`;

          return (
            (storeState?.sectionsCompleted?.DETAILS_CHANGE && this.router.parseUrl(wizardUrl.concat('/summary'))) ||
            (isWizardComplete(storeState) && this.router.parseUrl(wizardUrl.concat('/answers'))) ||
            (!storeState && this.router.parseUrl(wizardUrl.concat('/details-of-change'))) ||
            true
          );
        }),
      )
    );
  }
}
