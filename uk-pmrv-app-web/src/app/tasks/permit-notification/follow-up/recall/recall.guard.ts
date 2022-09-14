import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { CommonTasksStore } from '../../../store/common-tasks.store';

@Injectable({ providedIn: 'root' })
export class RecallGuard implements CanActivate {
  constructor(private readonly store: CommonTasksStore, private readonly router: Router) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    return this.store.pipe(
      map((state) => {
        return (
          state.requestTaskItem.allowedRequestTaskActions.includes(
            'PERMIT_NOTIFICATION_FOLLOW_UP_RECALL_FROM_AMENDS',
          ) || this.router.parseUrl(`/tasks/${route.paramMap.get('taskId')}/permit-notification/follow-up/wait`)
        );
      }),
    );
  }
}
