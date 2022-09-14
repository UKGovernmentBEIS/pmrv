import { FormBuilder } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

import { PERMIT_TASK_FORM } from '../shared/permit-task-form.token';
import { PermitApplicationStore } from '../store/permit-application.store';

export const permitTypeFormProvider = {
  provide: PERMIT_TASK_FORM,
  deps: [FormBuilder, PermitApplicationStore],
  useFactory: (fb: FormBuilder, store: PermitApplicationStore) => {
    const value = store.permitType;

    return fb.group({
      type: [
        { value: value ?? null, disabled: !store.getValue().isEditable },
        { validators: GovukValidators.required('Select the type of permit you are applying for'), updateOn: 'change' },
      ],
    });
  },
};
