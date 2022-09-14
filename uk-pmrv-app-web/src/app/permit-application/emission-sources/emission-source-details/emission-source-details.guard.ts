import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, UrlTree } from '@angular/router';

import { first, map, Observable } from 'rxjs';

import { PermitApplicationStore } from '../../store/permit-application.store';

@Injectable({
  providedIn: 'root',
})
export class EmissionSourceDetailsGuard implements CanActivate {
  constructor(private readonly store: PermitApplicationStore, private readonly router: Router) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    return this.store.getTask('emissionSources').pipe(
      first(),
      map(
        (emissionSources) =>
          emissionSources.some((emissionSource) => emissionSource.id === route.paramMap.get('sourceId')) ||
          this.router.parseUrl(`/permit-application/${route.paramMap.get('taskId')}/emission-sources/summary`),
      ),
    );
  }
}
