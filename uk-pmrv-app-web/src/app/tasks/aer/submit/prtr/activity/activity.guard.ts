import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { AerApplicationSubmitRequestTaskPayload } from 'pmrv-api';

@Injectable({ providedIn: 'root' })
export class ActivityGuard implements CanActivate {
  constructor(private readonly store: CommonTasksStore, private readonly router: Router) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    return this.store.pipe(
      map((storeState) => {
        const wizardUrl = `/tasks/${route.paramMap.get('taskId')}/aer/submit/prtr`;
        const activities = (storeState.requestTaskItem.requestTask.payload as AerApplicationSubmitRequestTaskPayload)
          .aer?.pollutantRegisterActivities;
        const index = Number(route.paramMap.get('index'));

        return (
          (storeState.isEditable &&
            (route.url.find((segment) => segment.path === 'delete')
              ? (activities?.activities?.length ?? 0) > index
              : (activities?.activities?.length ?? 0) >= index)) ||
          this.router.parseUrl(wizardUrl.concat('/summary'))
        );
      }),
    );
  }
}
