import { FormBuilder } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

import { PERMIT_TASK_FORM } from '../shared/permit-task-form.token';
import { PermitApplicationStore } from '../store/permit-application.store';

export const environmentalSystemFormProvider = {
  provide: PERMIT_TASK_FORM,
  deps: [FormBuilder, PermitApplicationStore],
  useFactory: (fb: FormBuilder, store: PermitApplicationStore) => {
    const value = store.permit.environmentalManagementSystem;

    return fb.group({
      exist: [
        { value: value?.exist ?? null, disabled: !store.getValue().isEditable },
        { validators: GovukValidators.required('Select Yes or No'), updateOn: 'change' },
      ],
      certified: [
        { value: value?.certified ?? null, disabled: !value?.exist },
        GovukValidators.required('Select Yes or No'),
      ],
      certificationStandard: [
        { value: value?.certificationStandard ?? null, disabled: !value?.certified },
        [
          GovukValidators.required('Enter a certification standard'),
          GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
        ],
      ],
    });
  },
};
