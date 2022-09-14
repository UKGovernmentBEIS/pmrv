import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { PermitApplicationStore } from '../store/permit-application.store';
import { findReviewGroupsBySection, getAmendTaskStatusKey } from './amend';


@Injectable({ providedIn: 'root' })
export class AmendGuard implements CanActivate {
  constructor(private readonly store: PermitApplicationStore, private readonly router: Router) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    const section = route.paramMap.get('section');
    return this.store.pipe(
      map(
        (storeState) => 
            (Object.keys(storeState.reviewGroupDecisions ?? []).some((reviewGroup) => findReviewGroupsBySection(section)?.includes(reviewGroup)) && 
                !storeState.permitSectionsCompleted?.[getAmendTaskStatusKey(section)]?.[0]) ||
            this.router.parseUrl(`/permit-application/${route.paramMap.get('taskId')}`)  
      ),
    );
  }
}
