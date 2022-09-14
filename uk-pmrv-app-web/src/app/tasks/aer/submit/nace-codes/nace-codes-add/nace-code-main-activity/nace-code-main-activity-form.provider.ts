import { FormBuilder } from '@angular/forms';

import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';

import { GovukValidators } from 'govuk-components';

export const naceMainActivityFormProvider = {
  provide: AER_TASK_FORM,
  deps: [FormBuilder],
  useFactory: (fb: FormBuilder) =>
    fb.group({
      mainActivity: [null, GovukValidators.required('Enter the relevant activity for the main activity')],
    }),
};
