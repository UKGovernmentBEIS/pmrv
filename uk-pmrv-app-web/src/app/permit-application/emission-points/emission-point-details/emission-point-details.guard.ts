import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, UrlTree } from '@angular/router';

import { first, map, Observable } from 'rxjs';

import { PermitApplicationStore } from '../../store/permit-application.store';

@Injectable({ providedIn: 'root' })
export class EmissionPointDetailsGuard implements CanActivate {
  constructor(private readonly store: PermitApplicationStore, private readonly router: Router) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    return this.store.getTask('emissionPoints').pipe(
      first(),
      map(
        (emissionPoints) =>
          emissionPoints.some((emissionPoint) => emissionPoint.id === route.paramMap.get('emissionPointId')) ||
          this.router.parseUrl(`/permit-application/${route.paramMap.get('taskId')}/emission-points/summary`),
      ),
    );
  }
}
