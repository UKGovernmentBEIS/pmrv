import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, UrlTree } from '@angular/router';

import { first, map, Observable } from 'rxjs';

import { AerService } from '@tasks/aer/core/aer.service';

@Injectable({ providedIn: 'root' })
export class RegulatedActivityGuard implements CanActivate {
  constructor(private readonly aerService: AerService, private readonly router: Router) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    return this.aerService.getTask('regulatedActivities').pipe(
      first(),
      map((regulatedActivities) => {
        const activityId = route.paramMap.get('activityId');
        const activity = regulatedActivities?.find((activity) => activity.id === activityId);
        return (
          !!activity || this.router.parseUrl(`tasks/${route.paramMap.get('taskId')}/aer/submit/regulated-activities`)
        );
      }),
    );
  }
}
