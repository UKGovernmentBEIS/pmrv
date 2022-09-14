import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, UrlTree } from '@angular/router';

import { first, map, Observable } from 'rxjs';

import { AerService } from '@tasks/aer/core/aer.service';

@Injectable({ providedIn: 'root' })
export class EmissionPointDetailsGuard implements CanActivate {
  constructor(private readonly aerService: AerService, private readonly router: Router) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    return this.aerService.getTask('emissionPoints').pipe(
      first(),
      map(
        (emissionPoints) =>
          emissionPoints.some((emissionPoint) => emissionPoint.id === route.paramMap.get('emissionPointId')) ||
          this.router.parseUrl(`tasks/${route.paramMap.get('taskId')}/aer/submit/emission-points`),
      ),
    );
  }
}
