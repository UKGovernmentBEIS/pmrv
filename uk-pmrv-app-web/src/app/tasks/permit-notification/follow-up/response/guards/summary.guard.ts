import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { PermitNotificationFollowUpRequestTaskPayload } from 'pmrv-api';

@Injectable({ providedIn: 'root' })
export class SummaryGuard implements CanActivate {
  constructor(private readonly store: CommonTasksStore, private readonly router: Router) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    return this.store.pipe(
      map((state) => {
        const followUpResponse = (
          state.requestTaskItem.requestTask.payload as PermitNotificationFollowUpRequestTaskPayload
        ).followUpResponse;
        return (
          !!followUpResponse ||
          !state.isEditable ||
          this.router.parseUrl(`/tasks/${route.params['taskId']}/permit-notification/follow-up/response`)
        );
      }),
    );
  }
}
