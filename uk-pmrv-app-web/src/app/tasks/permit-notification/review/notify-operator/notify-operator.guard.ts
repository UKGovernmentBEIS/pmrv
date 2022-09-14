import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { PermitNotificationApplicationReviewRequestTaskPayload } from 'pmrv-api';

import { CommonTasksStore } from '../../../store/common-tasks.store';
import { resolveDecisionStatus } from '../../core/section-status';

@Injectable({
  providedIn: 'root',
})
export class NotifyOperatorGuard implements CanActivate {
  constructor(private readonly store: CommonTasksStore, private readonly router: Router) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    return this.store.pipe(
      map((state) => {
        const decisionStatus = resolveDecisionStatus(
          (state.requestTaskItem.requestTask.payload as PermitNotificationApplicationReviewRequestTaskPayload)
            ?.reviewDecision,
        );

        return (
          (state.requestTaskItem.allowedRequestTaskActions.includes(
            'PERMIT_NOTIFICATION_NOTIFY_OPERATOR_FOR_DECISION',
          ) &&
            decisionStatus &&
            decisionStatus !== 'undecided') ||
          this.router.parseUrl(`/tasks/${route.paramMap.get('taskId')}/permit-notification/review`)
        );
      }),
    );
  }
}
