import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { AerApplicationSubmitRequestTaskPayload } from 'pmrv-api';

@Injectable({ providedIn: 'root' })
export class SummaryGuard implements CanActivate {
  constructor(private readonly store: CommonTasksStore, private readonly router: Router) {}

  canActivate(_route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean | UrlTree> {
    return this.store.pipe(
      map((storeState) => {
        if (!storeState.isEditable) {
          return true;
        } else {
          const aer = (storeState.requestTaskItem.requestTask.payload as AerApplicationSubmitRequestTaskPayload).aer;
          return (
            aer?.pollutantRegisterActivities?.exist === false ||
            (aer?.pollutantRegisterActivities?.exist && aer?.pollutantRegisterActivities?.activities?.length > 0) ||
            this.router.parseUrl(state.url.slice(0, state.url.lastIndexOf('/summary')))
          );
        }
      }),
    );
  }
}
