import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, switchMap } from 'rxjs';

import { PendingRequestService } from '../../../../core/guards/pending-request.service';
import { PendingRequest } from '../../../../core/interfaces/pending-request.interface';
import { PERMIT_SURRENDER_TASK_FORM } from '../../../core/permit-surrender-task-form.token';
import { PermitSurrenderStore } from '../../../store/permit-surrender.store';
import { allowancesNumberFormProvider } from './allowances-number-form.provider';

@Component({
  selector: 'app-allowances-number',
  template: `<app-wizard-step
      (formSubmit)="onContinue()"
      [formGroup]="form"
      submitText="Continue"
      [hideSubmit]="(store.isEditable$ | async) === false"
    >
      <span class="govuk-caption-l">Confirm cessation of regulated activities</span>

      <app-page-heading>How many allowances were surrendered by the installation?</app-page-heading>

      <div
        formControlName="numberOfSurrenderAllowances"
        inputType="number"
        govuk-text-input
        hint="For example 1000"
        widthClass="govuk-input--width-10"
      ></div>
    </app-wizard-step>
    <a govukLink routerLink="../..">Return to: Cessation</a> `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [allowancesNumberFormProvider],
})
export class AllowancesNumberComponent implements PendingRequest {
  constructor(
    @Inject(PERMIT_SURRENDER_TASK_FORM) readonly form: FormGroup,
    readonly store: PermitSurrenderStore,
    readonly pendingRequest: PendingRequestService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onContinue(): void {
    if (!this.form.dirty) {
      this.router.navigate(['../emissions'], { relativeTo: this.route });
    } else {
      const numberOfSurrenderAllowances = this.form.value.numberOfSurrenderAllowances;

      this.store
        .pipe(
          first(),
          switchMap((state) =>
            this.store.postSaveCessation(
              {
                ...state.cessation,
                numberOfSurrenderAllowances,
              },
              false,
            ),
          ),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(() => this.router.navigate(['../emissions'], { relativeTo: this.route }));
    }
  }
}
