import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, UrlTree } from '@angular/router';

import { first, map, Observable } from 'rxjs';

import { AerService } from '@tasks/aer/core/aer.service';

@Injectable({
  providedIn: 'root',
})
export class EmissionSourceDetailsGuard implements CanActivate {
  constructor(private readonly aerService: AerService, private readonly router: Router) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    return this.aerService.getTask('emissionSources').pipe(
      first(),
      map(
        (emissionSources) =>
          emissionSources.some((emissionSource) => emissionSource.id === route.paramMap.get('sourceId')) ||
          this.router.parseUrl(`/tasks/${route.paramMap.get('taskId')}/aer/emission-sources`),
      ),
    );
  }
}
