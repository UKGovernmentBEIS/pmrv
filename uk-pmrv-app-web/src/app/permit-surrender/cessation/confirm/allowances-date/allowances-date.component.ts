import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, switchMap } from 'rxjs';

import { PendingRequestService } from '../../../../core/guards/pending-request.service';
import { PendingRequest } from '../../../../core/interfaces/pending-request.interface';
import { PERMIT_SURRENDER_TASK_FORM } from '../../../core/permit-surrender-task-form.token';
import { PermitSurrenderStore } from '../../../store/permit-surrender.store';
import { allowancesDateFormProvider } from './allowances-date-form.provider';

@Component({
  selector: 'app-allowances-date',
  template: `<app-wizard-step
      (formSubmit)="onContinue()"
      [formGroup]="form"
      submitText="Continue"
      [hideSubmit]="(store.isEditable$ | async) === false"
    >
      <span class="govuk-caption-l">Confirm cessation of regulated activities</span>

      <app-page-heading>When were the installation allowances surrendered?</app-page-heading>

      <div class="govuk-hint">The date must not be in the future, for example, 27 3 2007</div>

      <div formControlName="allowancesSurrenderDate" govuk-date-input [max]="today"></div>
    </app-wizard-step>
    <a govukLink routerLink="../..">Return to: Cessation</a> `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [allowancesDateFormProvider],
})
export class AllowancesDateComponent implements PendingRequest {
  today = new Date();

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
        [this.store.getState().allowancesSurrenderRequired ? '../allowances-number' : '../emissions'],
        { relativeTo: this.route },
      );
    } else {
      const allowancesSurrenderDate = this.form.value.allowancesSurrenderDate;

      this.store
        .pipe(
          first(),
          switchMap((state) =>
            this.store.postSaveCessation(
              {
                ...state.cessation,
                allowancesSurrenderDate,
              },
              false,
            ),
          ),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(() =>
          this.router.navigate(
            [this.store.getState().allowancesSurrenderRequired ? '../allowances-number' : '../emissions'],
            { relativeTo: this.route },
          ),
        );
    }
  }
}
