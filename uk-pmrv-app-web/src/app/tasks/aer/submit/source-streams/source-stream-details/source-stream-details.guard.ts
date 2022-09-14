import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, UrlTree } from '@angular/router';

import { first, map, Observable } from 'rxjs';

import { AerService } from '@tasks/aer/core/aer.service';

@Injectable({ providedIn: 'root' })
export class SourceStreamDetailsGuard implements CanActivate {
  constructor(private readonly aerService: AerService, private readonly router: Router) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    return this.aerService.getTask('sourceStreams').pipe(
      first(),
      map(
        (sourceStreams) =>
          sourceStreams.some((sourceStream) => sourceStream.id === route.paramMap.get('streamId')) ||
          this.router.parseUrl(`tasks/${route.paramMap.get('taskId')}/aer/submit/source-streams`),
      ),
    );
  }
}
