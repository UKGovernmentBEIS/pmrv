import { FormBuilder } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

import { PERMIT_TASK_FORM } from '../../shared/permit-task-form.token';

export const approachesAddFormProvider = {
  provide: PERMIT_TASK_FORM,
  deps: [FormBuilder],
  useFactory: (fb: FormBuilder) =>
    fb.group({
      monitoringApproaches: [null, GovukValidators.required('Select at least one monitoring approach')],
    }),
};
