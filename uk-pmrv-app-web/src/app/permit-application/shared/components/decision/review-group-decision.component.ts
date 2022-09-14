import { ChangeDetectionStrategy, Component, EventEmitter, Inject, Input, OnInit, Output } from '@angular/core';
import { FormGroup } from '@angular/forms';

import { BehaviorSubject, map, Observable, startWith, takeUntil } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { DestroySubject } from '@core/services/destroy-subject.service';

import { PermitIssuanceReviewDecision, PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload } from 'pmrv-api';

import { PermitApplicationStore } from '../../../store/permit-application.store';
import { ReviewGroupStatusPipe } from '../../pipes/review-group-status.pipe';
import { REVIEW_GROUP_DECISION_FORM, reviewGroupDecisionFormProvider } from './review-group-decision-form.provider';

@Component({
  selector: 'app-review-group-decision',
  templateUrl: './review-group-decision.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject, reviewGroupDecisionFormProvider],
})
export class ReviewGroupDecisionComponent implements OnInit, PendingRequest {
  @Input() groupKey: PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload['group'];
  @Input() canEdit = true;
  @Output() readonly notification = new EventEmitter<boolean>();

  files$ = new BehaviorSubject<{ downloadUrl: string; fileName: string }[]>([]);
  isSummaryDisplayed$ = new BehaviorSubject<boolean>(false);
  isFileUploaded$ = this.form.get('files').valueChanges.pipe(
    startWith(this.form.get('files').value),
    map((value) => value?.length > 0),
  );

  isEditable$ = this.store.pipe(map((state) => state.isEditable));
  isOnEditState = false;
  isInEditByDefaultStatus$: Observable<boolean>;

  constructor(
    @Inject(REVIEW_GROUP_DECISION_FORM) readonly form: FormGroup,
    readonly pendingRequest: PendingRequestService,
    readonly store: PermitApplicationStore,
    private readonly destroy$: DestroySubject,
  ) {}

  ngOnInit(): void {
    this.isInEditByDefaultStatus$ = new ReviewGroupStatusPipe(this.store).transform(this.groupKey).pipe(
      map((status) => status === 'undecided' || status === 'needs review'),
      takeUntil(this.destroy$),
    );

    this.store.pipe(takeUntil(this.destroy$)).subscribe((state) =>
      this.files$.next(
        this.form.get('files').value?.map((file) => ({
          downloadUrl: `/permit-application/${state.requestTaskId}/file-download/${file.uuid}`,
          fileName: file.file.name,
        })),
      ),
    );
  }

  onSubmit(): void {
    if (this.form.valid) {
      this.store
        .postReview(this.getReviewDecision(), this.groupKey, this.form.controls.files.value)
        .pipe(this.pendingRequest.trackRequest())
        .subscribe(() => {
          this.isOnEditState = false;
          this.notification.emit(true);
        });
    } else {
      this.isSummaryDisplayed$.next(true);
    }
  }

  getReviewDecision(): PermitIssuanceReviewDecision {
    return {
      type: this.form.controls.decision.value,
      notes: this.form.controls.notes.value,
      ...(this.form.controls.decision.value === 'OPERATOR_AMENDS_NEEDED'
        ? {
            changesRequired: this.form.controls.changesRequired.value,
            files: this.form.controls.files.value?.map((file) => file.uuid),
          }
        : {}),
    };
  }

  getDownloadUrl() {
    return this.store.createBaseFileDownloadUrl();
  }
}
