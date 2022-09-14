import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, UrlTree } from '@angular/router';

import { first, map, Observable } from 'rxjs';

import { PermitApplicationStore } from '../../store/permit-application.store';

@Injectable({ providedIn: 'root' })
export class SourceStreamDetailsGuard implements CanActivate {
  constructor(private readonly store: PermitApplicationStore, private readonly router: Router) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    return this.store.getTask('sourceStreams').pipe(
      first(),
      map(
        (sourceStreams) =>
          sourceStreams.some((sourceStream) => sourceStream.id === route.paramMap.get('streamId')) ||
          this.router.parseUrl(`/permit-application/${route.paramMap.get('taskId')}/source-streams/summary`),
      ),
    );
  }
}
