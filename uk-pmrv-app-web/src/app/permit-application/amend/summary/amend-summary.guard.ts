import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { PermitApplicationStore } from '../../store/permit-application.store';
import { getAmendTaskStatusKey } from '../amend';

@Injectable({ providedIn: 'root' })
export class AmendSummaryGuard implements CanActivate {
  constructor(private readonly store: PermitApplicationStore, private readonly router: Router) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    const section = route.paramMap.get('section');
    return this.store.pipe(
      map(
        (storeState) =>
          storeState.permitSectionsCompleted?.[getAmendTaskStatusKey(section)]?.[0] ||
          this.router.parseUrl(`/permit-application/${route.paramMap.get('taskId')}/amend/${section}`),
      ),
    );
  }
}
