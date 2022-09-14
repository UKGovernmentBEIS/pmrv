import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { AerApplicationSubmitRequestTaskPayload } from 'pmrv-api';

@Injectable({ providedIn: 'root' })
export class SummaryGuard implements CanActivate {
  constructor(private readonly store: CommonTasksStore, private readonly router: Router) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean | UrlTree> {
    return this.store.pipe(
      map((storeState) => {
        if (!storeState.isEditable) {
          return true;
        } else {
          return (
            (storeState.requestTaskItem.requestTask.payload as AerApplicationSubmitRequestTaskPayload)
              .aerSectionsCompleted?.[route.data.aerTask]?.[route.paramMap.get('index') ?? 0] ||
            this.router.parseUrl(state.url.slice(0, state.url.lastIndexOf('/summary')))
          );
        }
      }),
    );
  }
}
