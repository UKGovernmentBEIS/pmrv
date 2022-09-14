import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { RfiStore } from '../../store/rfi.store';

@Injectable({
  providedIn: 'root',
})
export class NotifyGuard implements CanActivate {
  constructor(private readonly router: Router, private readonly store: RfiStore) {}

  canActivate(route: ActivatedRouteSnapshot): boolean | UrlTree | Observable<boolean | UrlTree> {
    return (
      this.router.getCurrentNavigation().extras?.state?.changing ||
      this.store.pipe(
        map((rfiState) => {
          const firstStepExists =
            rfiState.rfiSubmitPayload?.questions?.length > 0 && !!rfiState.rfiSubmitPayload?.deadline;
          const allStepsExist =
            firstStepExists &&
            rfiState.rfiSubmitPayload?.operators?.length > 0 &&
            !!rfiState.rfiSubmitPayload?.signatory;
          return (
            (allStepsExist && this.router.parseUrl(`/rfi/${route.paramMap.get('taskId')}/answers`)) ||
            (!firstStepExists && this.router.parseUrl(`/rfi/${route.paramMap.get('taskId')}/questions`)) ||
            firstStepExists
          );
        }),
      )
    );
  }
}
