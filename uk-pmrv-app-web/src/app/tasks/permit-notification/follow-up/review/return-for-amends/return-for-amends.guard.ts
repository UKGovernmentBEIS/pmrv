import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { PermitNotificationFollowUpApplicationReviewRequestTaskPayload } from 'pmrv-api';

import { CommonTasksStore } from '../../../../store/common-tasks.store';
import { resolveFollowUpDecisionStatus } from '../../../core/section-status';

@Injectable({
  providedIn: 'root',
})
export class FollowUpReturnForAmendsGuard implements CanActivate {
  constructor(private readonly store: CommonTasksStore, private readonly router: Router) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    return this.store.pipe(
      map((state) => {
        const decisionStatus = resolveFollowUpDecisionStatus(
          state.requestTaskItem.requestTask.payload as PermitNotificationFollowUpApplicationReviewRequestTaskPayload,
        );
        return (
          (state.requestTaskItem.allowedRequestTaskActions.includes(
            'PERMIT_NOTIFICATION_FOLLOW_UP_SAVE_REVIEW_DECISION',
          ) &&
            decisionStatus &&
            decisionStatus === 'operator to amend') ||
          this.router.parseUrl(`/tasks/${route.paramMap.get('taskId')}/permit-notification/follow-up/review`)
        );
      }),
    );
  }
}
