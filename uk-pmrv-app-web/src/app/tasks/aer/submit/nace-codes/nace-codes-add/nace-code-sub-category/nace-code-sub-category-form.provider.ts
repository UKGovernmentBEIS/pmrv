import { FormBuilder } from '@angular/forms';

import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';

import { GovukValidators } from 'govuk-components';

export const naceCodeSubCategoryFormProvider = {
  provide: AER_TASK_FORM,
  deps: [FormBuilder],
  useFactory: (fb: FormBuilder) =>
    fb.group({
      subCategory: [null, GovukValidators.required('Enter the sub-category')],
    }),
};
