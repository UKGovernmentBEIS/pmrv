import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, UrlTree } from '@angular/router';

import { combineLatest, map, Observable } from 'rxjs';

import { PermitNotificationService } from '../../core/permit-notification.service';

@Injectable({
  providedIn: 'root',
})
export class SummaryGuard implements CanActivate {
  constructor(private readonly router: Router, private readonly permitNotificationService: PermitNotificationService) {}

  canActivate(
    route: ActivatedRouteSnapshot,
  ): boolean | UrlTree | Observable<boolean | UrlTree> | Promise<boolean | UrlTree> {
    return (
      this.router.getCurrentNavigation().extras?.state?.changing ||
      combineLatest([this.permitNotificationService.getIsEditable(), this.permitNotificationService.getPayload()]).pipe(
        map(([isEditable, storeState]) => {
          const wizardUrl = `/tasks/${route.paramMap.get('taskId')}/permit-notification/submit`;

          return (
            !isEditable ||
            (!storeState?.sectionsCompleted?.DETAILS_CHANGE &&
              this.router.parseUrl(wizardUrl.concat('/details-of-change'))) ||
            true
          );
        }),
      )
    );
  }
}
