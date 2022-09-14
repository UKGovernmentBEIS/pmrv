import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { RfiStore } from '../../store/rfi.store';

@Injectable({
  providedIn: 'root',
})
export class AnswersGuard implements CanActivate {
  constructor(private readonly router: Router, private readonly store: RfiStore) {}

  canActivate(route: ActivatedRouteSnapshot): boolean | UrlTree | Observable<boolean | UrlTree> {
    return this.store.pipe(
      map((rfiState) => {
        const allStepsExist =
          rfiState.rfiSubmitPayload?.questions.length > 0 &&
          !!rfiState.rfiSubmitPayload?.deadline &&
          !!rfiState.rfiSubmitPayload?.signatory;
        return allStepsExist || this.router.parseUrl(`/rfi/${route.paramMap.get('taskId')}/questions`);
      }),
    );
  }
}
