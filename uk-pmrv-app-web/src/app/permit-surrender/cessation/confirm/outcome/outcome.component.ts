import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, switchMap } from 'rxjs';

import { PendingRequestService } from '../../../../core/guards/pending-request.service';
import { PendingRequest } from '../../../../core/interfaces/pending-request.interface';
import { PERMIT_SURRENDER_TASK_FORM } from '../../../core/permit-surrender-task-form.token';
import { PermitSurrenderStore } from '../../../store/permit-surrender.store';
import { outcomeFormProvider } from './outcome-form.provider';

@Component({
  selector: 'app-outcome',
  template: `<app-wizard-step
      (formSubmit)="onContinue()"
      [formGroup]="form"
      submitText="Continue"
      [hideSubmit]="(store.isEditable$ | async) === false"
    >
      <span class="govuk-caption-l">Confirm cessation of regulated activities</span>

      <app-page-heading>What was the outcome of the emission report determination?</app-page-heading>

      <div govuk-radio formControlName="determinationOutcome">
        <govuk-radio-option label="Approved" value="APPROVED"></govuk-radio-option>
        <govuk-radio-option label="Rejected" value="REJECTED"></govuk-radio-option>
      </div>
    </app-wizard-step>
    <a govukLink routerLink="../..">Return to: Cessation</a> `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [outcomeFormProvider],
})
export class OutcomeComponent implements PendingRequest {
  constructor(
    @Inject(PERMIT_SURRENDER_TASK_FORM) readonly form: FormGroup,
    readonly store: PermitSurrenderStore,
    readonly pendingRequest: PendingRequestService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onContinue(): void {
    if (!this.form.dirty) {
      this.router.navigate(
        [this.store.getState().allowancesSurrenderRequired ? '../allowances-date' : '../emissions'],
        { relativeTo: this.route },
      );
    } else {
      const determinationOutcome = this.form.value.determinationOutcome;

      this.store
        .pipe(
          first(),
          switchMap((state) =>
            this.store.postSaveCessation(
              {
                ...state.cessation,
                determinationOutcome,
              },
              false,
            ),
          ),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(() =>
          this.router.navigate(
            [this.store.getState().allowancesSurrenderRequired ? '../allowances-date' : '../emissions'],
            { relativeTo: this.route },
          ),
        );
    }
  }
}
