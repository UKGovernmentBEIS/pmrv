import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, Observable } from 'rxjs';

import { CommonTasksStore } from '../../store/common-tasks.store';

@Component({
  selector: 'app-peer-review',
  templateUrl: './peer-review.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PeerReviewComponent {
  readonly daysRemaining$ = this.store.pipe(
    first(),
    map((state) => state.requestTaskItem.requestTask.daysRemaining),
  );

  readonly allowPeerReviewDecision$: Observable<boolean> = this.store.pipe(
    first(),
    map((state) => {
      return state.requestTaskItem.allowedRequestTaskActions?.includes(
        'PERMIT_NOTIFICATION_REVIEW_SUBMIT_PEER_REVIEW_DECISION',
      );
    }),
  );

  peerReviewDecision(): void {
    this.router.navigate(['./decision'], { relativeTo: this.route });
  }

  constructor(
    readonly store: CommonTasksStore,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}
}
