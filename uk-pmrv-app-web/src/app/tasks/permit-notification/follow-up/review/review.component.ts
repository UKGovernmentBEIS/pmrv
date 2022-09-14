import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { map, Observable } from 'rxjs';

import { PermitNotificationFollowUpApplicationReviewRequestTaskPayload } from 'pmrv-api';

import { CommonTasksState } from '../../../store/common-tasks.state';
import { CommonTasksStore } from '../../../store/common-tasks.store';
import { resolveFollowUpDecisionStatus } from '../../core/section-status';

@Component({
  selector: 'app-follow-review',
  templateUrl: './review.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FollowUpReviewComponent {
  readonly daysRemaining$ = this.store.pipe(map((state) => state.requestTaskItem.requestTask.daysRemaining));
  readonly allowNotifyOperator$: Observable<boolean> = this.store.pipe(
    map((state) => {
      const isDecisionTaken = this.isDecisionAccepted(state);
      const isActionAllowed = state.requestTaskItem.allowedRequestTaskActions?.includes(
        'PERMIT_NOTIFICATION_FOLLOW_UP_NOTIFY_OPERATOR_FOR_DECISION',
      );
      return isDecisionTaken && isActionAllowed;
    }),
  );

  readonly allowReturnForAmends$: Observable<boolean> = this.store.pipe(
    map((state) => {
      const isDecisionTaken = this.isDecisionReturnForAmends(state);
      const isActionAllowed = state.requestTaskItem.allowedRequestTaskActions?.includes(
        'PERMIT_NOTIFICATION_FOLLOW_UP_RETURN_FOR_AMENDS',
      );
      return isDecisionTaken && isActionAllowed;
    }),
  );

  constructor(readonly store: CommonTasksStore, readonly route: ActivatedRoute, private readonly router: Router) {}

  notifyOperator() {
    this.router.navigate(['notify-operator'], { relativeTo: this.route });
  }

  private isDecisionAccepted(state: CommonTasksState): boolean {
    const payload = state.requestTaskItem.requestTask
      .payload as PermitNotificationFollowUpApplicationReviewRequestTaskPayload;

    return resolveFollowUpDecisionStatus(payload) === 'accepted';
  }

  returnForAmends() {
    this.router.navigate(['return-for-amends'], { relativeTo: this.route });
  }

  private isDecisionReturnForAmends(state: CommonTasksState): boolean {
    const payload = state.requestTaskItem.requestTask
      .payload as PermitNotificationFollowUpApplicationReviewRequestTaskPayload;

    return resolveFollowUpDecisionStatus(payload) === 'operator to amend';
  }
}
