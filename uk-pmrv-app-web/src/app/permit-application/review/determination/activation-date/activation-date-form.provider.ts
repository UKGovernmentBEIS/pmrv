import { AbstractControl, FormBuilder, ValidatorFn } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

import { PermitIssuanceGrantDetermination } from 'pmrv-api';

import { PERMIT_TASK_FORM } from '../../../shared/permit-task-form.token';
import { PermitApplicationStore } from '../../../store/permit-application.store';

export const activationDateFormProvider = {
  provide: PERMIT_TASK_FORM,
  deps: [FormBuilder, PermitApplicationStore],
  useFactory: (fb: FormBuilder, store: PermitApplicationStore) => {
    const state = store.getValue();
    const value = state.determination as PermitIssuanceGrantDetermination;

    return fb.group({
      activationDate: [
        {
          value: value?.activationDate ? new Date(value.activationDate) : null,
          disabled: !state.isEditable,
        },
        {
          validators: [GovukValidators.required('Enter a date'), futureDateValidator()],
        },
      ],
    });
  },
};

export function futureDateValidator(): ValidatorFn {
  return (control: AbstractControl): { [key: string]: string } | null => {
    const yesterday = new Date(new Date().setDate(new Date().getDate() - 1));
    return control.value && control.value < yesterday
      ? { invalidDate: `The date must be today or in the future` }
      : null;
  };
}
