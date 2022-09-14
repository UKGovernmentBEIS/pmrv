import { ChangeDetectionStrategy, Component, Inject, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map } from 'rxjs';

import { SharedStore } from '@shared/store/shared.store';

import { PendingRequestService } from '../../../core/guards/pending-request.service';
import { BreadcrumbService } from '../../breadcrumbs/breadcrumb.service';
import { PEER_REVIEW_DECISION_FORM, peerReviewDecisionFormProvider } from './peer-review-decision-form.provider';
import { getPreviousPage, getRequestType } from './peer-review-decision-type-resolver';

@Component({
  selector: 'app-peer-review-decision',
  templateUrl: './peer-review-decision.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [peerReviewDecisionFormProvider, BreadcrumbService],
})
export class PeerReviewDecisionComponent implements OnInit {
  requestType = getRequestType(this.router.url);
  previousPage = getPreviousPage(this.requestType);

  constructor(
    @Inject(PEER_REVIEW_DECISION_FORM) readonly form: FormGroup,
    private readonly router: Router,
    private readonly breadcrumbService: BreadcrumbService,
    private readonly route: ActivatedRoute,
    readonly pendingRequest: PendingRequestService,
    private readonly sharedStore: SharedStore,
  ) {}

  ngOnInit(): void {
    this.sharedStore
      .pipe(
        map((state) => state.peerReviewDecision),
        first(),
      )
      .subscribe((peerReviewDecision) => {
        this.form.controls['type'].setValue(peerReviewDecision?.type);
        this.form.controls['notes'].setValue(peerReviewDecision?.notes);
        this.form.updateValueAndValidity();
      });
  }

  onSubmit(): void {
    const type = this.form.get('type').value;
    const notes = this.form.get('notes').value;
    this.sharedStore.setState({
      ...this.sharedStore.getState(),
      peerReviewDecision: {
        type,
        notes,
      },
    });
    this.router.navigate(['answers'], {
      relativeTo: this.route,
    });
  }
}
