import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { BehaviorSubject, combineLatest, map, tap } from 'rxjs';

import { PendingRequestService } from '../../core/guards/pending-request.service';
import { PendingRequest } from '../../core/interfaces/pending-request.interface';
import { PERMIT_TASK_FORM } from '../shared/permit-task-form.token';
import { PermitApplicationStore } from '../store/permit-application.store';
import { emisionSummariesFormFactory } from './emission-summaries-form.provider';

@Component({
  selector: 'app-emission-summaries',
  templateUrl: './emission-summaries.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [emisionSummariesFormFactory],
  styleUrls: ['./emission-summaries.component.scss'],
})
export class EmissionSummariesComponent implements PendingRequest {
  displayErrorSummary$ = new BehaviorSubject(false);
  isSummaryDisplayed$ = combineLatest([this.displayErrorSummary$, this.store]).pipe(
    tap(() => this.form.updateValueAndValidity()),
    map(
      ([displaySummary, state]) =>
        displaySummary ||
        this.form.errors?.sourceStreamNotExist ||
        this.form.errors?.emissionSourceNotExist ||
        this.form.errors?.emissionPointNotExist ||
        this.form.errors?.regulatedActivityNotExist ||
        (state.permitSectionsCompleted?.emissionSummaries?.[0] &&
          (this.form.errors?.emissionPointsNotUsed ||
            this.form.errors?.emissionSourcesNotUsed ||
            this.form.errors?.sourceStreamsNotUsed ||
            this.form.errors?.regulatedActivitiesNotUsed)),
    ),
  );

  constructor(
    @Inject(PERMIT_TASK_FORM) readonly form: FormGroup,
    readonly pendingRequest: PendingRequestService,
    readonly store: PermitApplicationStore,
    private readonly route: ActivatedRoute,
  ) {}

  onSubmit(): void {
    if (!this.form.valid) {
      this.displayErrorSummary$.next(true);
      return;
    }
    this.store
      .postStatus('emissionSummaries', true)
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => this.store.navigate(this.route, 'summary', 'fuels'));
  }
}
