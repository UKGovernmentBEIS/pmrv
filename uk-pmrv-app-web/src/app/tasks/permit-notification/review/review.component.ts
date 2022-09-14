import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { map, Observable } from 'rxjs';

import { PermitNotificationApplicationReviewRequestTaskPayload } from 'pmrv-api';

import { CommonTasksStore } from '../../store/common-tasks.store';
import { resolveDecisionStatus } from '../core/section-status';

@Component({
  selector: 'app-review',
  templateUrl: './review.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReviewComponent {
  readonly daysRemaining$ = this.store.pipe(map((state) => state.requestTaskItem.requestTask.daysRemaining));

  readonly allowNotifyOperator$: Observable<boolean> = this.store.pipe(
    map((state) => {
      const isDecisionTaken = this.isDecisionTaken(state);
      const isActionAllowed = state.requestTaskItem.allowedRequestTaskActions?.includes(
        'PERMIT_NOTIFICATION_NOTIFY_OPERATOR_FOR_DECISION',
      );
      return isDecisionTaken && isActionAllowed;
    }),
  );

  readonly allowSendPeerReview$: Observable<boolean> = this.store.pipe(
    map((state) => {
      const isDecisionTaken = this.isDecisionTaken(state);
      const isActionAllowed = state.requestTaskItem.allowedRequestTaskActions?.includes(
        'PERMIT_NOTIFICATION_REQUEST_PEER_REVIEW',
      );
      return isDecisionTaken && isActionAllowed;
    }),
  );

  constructor(
    readonly store: CommonTasksStore,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
  ) {}

  notifyOperator(): void {
    this.router.navigate(['notify-operator'], { relativeTo: this.route });
  }

  sendPeerReview(): void {
    this.router.navigate(['peer-review'], { relativeTo: this.route });
  }

  private isDecisionTaken(state): boolean {
    const payload = state.requestTaskItem.requestTask.payload as PermitNotificationApplicationReviewRequestTaskPayload;

    return !(resolveDecisionStatus(payload?.reviewDecision) === 'undecided');
  }
}
