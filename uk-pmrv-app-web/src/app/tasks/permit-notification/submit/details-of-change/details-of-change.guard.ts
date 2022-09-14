import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, UrlTree } from '@angular/router';

import { combineLatest, map, Observable } from 'rxjs';

import { PermitNotificationService } from '../../core/permit-notification.service';
import { isWizardComplete } from '../../core/section-status';

@Injectable({
  providedIn: 'root',
})
export class DetailsOfChangeGuard implements CanActivate {
  constructor(private readonly router: Router, private readonly permitNotificationService: PermitNotificationService) {}

  canActivate(
    route: ActivatedRouteSnapshot,
  ): boolean | UrlTree | Observable<boolean | UrlTree> | Promise<boolean | UrlTree> {
    return (
      this.router.getCurrentNavigation().extras?.state?.changing ||
      combineLatest([this.permitNotificationService.getIsEditable(), this.permitNotificationService.getPayload()]).pipe(
        map(([isEditable, payload]) => {
          const wizardUrl = `/tasks/${route.paramMap.get('taskId')}/permit-notification/submit`;

          return (
            ((payload?.sectionsCompleted?.DETAILS_CHANGE || !isEditable) &&
              this.router.parseUrl(wizardUrl.concat('/summary'))) ||
            (isWizardComplete(payload) && this.router.parseUrl(wizardUrl.concat('/answers'))) ||
            true
          );
        }),
      )
    );
  }
}
