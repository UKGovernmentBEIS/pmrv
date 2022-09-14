import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { map } from 'rxjs';

import { PeerReviewDecisionSubmittedRequestActionPayload } from 'pmrv-api';

import { CommonActionsStore } from '../../store/common-actions.store';

@Component({
  selector: 'app-peer-review-decision',
  templateUrl: './peer-review-decision.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PeerReviewDecisionComponent {
  constructor(readonly route: ActivatedRoute, private readonly commonActionStore: CommonActionsStore) {}

  submitter$ = this.commonActionStore.requestAction$.pipe(map((a) => a.submitter));

  payload$ = this.commonActionStore.payload$.pipe(map((p) => p as PeerReviewDecisionSubmittedRequestActionPayload));
}
