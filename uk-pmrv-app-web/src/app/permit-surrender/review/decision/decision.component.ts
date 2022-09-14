import { ChangeDetectionStrategy, Component, Inject, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';

import { BehaviorSubject } from 'rxjs';

import { PendingRequestService } from '../../../core/guards/pending-request.service';
import { BackLinkService } from '../../../shared/back-link/back-link.service';
import { PermitSurrenderStore } from '../../store/permit-surrender.store';
import { resolveDeterminationCompletedUponDecision } from '../core/review-status';
import { REVIEW_DECISION_FORM, reviewDecisionFormProvider } from './decision-form.provider';

@Component({
  selector: 'app-decision',
  templateUrl: './decision.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [reviewDecisionFormProvider, BackLinkService],
})
export class DecisionComponent implements OnInit {
  isEditMode$ = new BehaviorSubject(false);
  isErrorSummaryDisplayed$ = new BehaviorSubject<boolean>(false);

  reviewDecision$ = this.store.select('reviewDecision');
  isEditable$ = this.store.select('isEditable');

  constructor(
    @Inject(REVIEW_DECISION_FORM) readonly form: FormGroup,
    readonly pendingRequest: PendingRequestService,
    readonly store: PermitSurrenderStore,
    private readonly backLinkService: BackLinkService,
  ) {}

  ngOnInit(): void {
    this.backLinkService.show();
  }

  onSubmit(): void {
    if (this.form.valid) {
      this.store
        .postReviewDecision(
          {
            type: this.form.controls.decision.value,
            notes: this.form.controls.notes.value,
          },
          resolveDeterminationCompletedUponDecision(this.form.controls.decision.value, this.store.getState()),
        )
        .pipe(this.pendingRequest.trackRequest())
        .subscribe(() => {
          this.isEditMode$.next(false);
        });
    } else {
      this.isErrorSummaryDisplayed$.next(true);
    }
  }
}
