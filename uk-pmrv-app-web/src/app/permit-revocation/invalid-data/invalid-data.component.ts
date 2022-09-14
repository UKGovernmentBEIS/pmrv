import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';

import { map, tap } from 'rxjs';

import {
  PERMIT_REVOCATION_TASK_FORM,
  permitRevocationFormProvider,
} from '@permit-revocation/factory/permit-revocation-form-provider';
import { PermitRevocationStore } from '@permit-revocation/store/permit-revocation-store';

@Component({
  selector: 'app-invalid-data',
  template: `
    <govuk-error-summary *ngIf="invalidEffectiveAndFeeDates$ | async" [form]="form"></govuk-error-summary>
    <a govukLink routerLink=".."> Return to permit revocation </a>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [permitRevocationFormProvider],
})
export class InvalidDataComponent {
  invalidEffectiveAndFeeDates$ = this.store.pipe(
    tap((values) => {
      this.form.controls.effectiveDate.enable();
      this.form.controls.effectiveDate.updateValueAndValidity();
      if (!values.permitRevocation.feeCharged) {
        delete this.form.controls.feeDate;
      } else {
        this.form.controls.feeDate.enable();
        this.form.controls.feeDate.updateValueAndValidity();
      }
    }),
    map(() =>
      this.form.controls.effectiveDate.errors
        ? this.form.controls.effectiveDate.errors?.invalidEffectiveDate
        : this.form.controls.feeDate.errors?.invalidFeeDate,
    ),
  );

  constructor(@Inject(PERMIT_REVOCATION_TASK_FORM) readonly form: FormGroup, private store: PermitRevocationStore) {}
}
