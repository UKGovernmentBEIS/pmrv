import { FormBuilder } from '@angular/forms';

import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';

import { GovukValidators } from 'govuk-components';

export const approachesAddFormProvider = {
  provide: AER_TASK_FORM,
  deps: [FormBuilder],
  useFactory: (fb: FormBuilder) =>
    fb.group({
      monitoringApproaches: [null, GovukValidators.required('Select at least one monitoring approach')],
    }),
};
