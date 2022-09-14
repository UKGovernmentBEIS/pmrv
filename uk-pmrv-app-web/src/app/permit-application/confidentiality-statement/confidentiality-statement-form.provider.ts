import { FormBuilder, FormControl, FormGroup } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

import { ConfidentialSection } from 'pmrv-api';

import { PERMIT_TASK_FORM } from '../shared/permit-task-form.token';
import { PermitApplicationStore } from '../store/permit-application.store';

export const confidentialityStatementFormProvider = {
  provide: PERMIT_TASK_FORM,
  deps: [FormBuilder, PermitApplicationStore],
  useFactory: (fb: FormBuilder, store: PermitApplicationStore) => {
    const value = store.permit.confidentialityStatement;

    return fb.group({
      exist: [
        { value: value?.exist ?? null, disabled: !store.getValue().isEditable },
        { validators: GovukValidators.required('Select Yes or No'), updateOn: 'change' },
      ],
      confidentialSections: fb.array(
        value?.confidentialSections?.map(createAnotherSection) ?? [createAnotherSection()],
      ),
    });
  },
};

export function createAnotherSection(value?: ConfidentialSection): FormGroup {
  return new FormGroup({
    section: new FormControl(value?.section ?? null, [
      GovukValidators.required('Enter a section'),
      GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
    ]),
    explanation: new FormControl(value?.explanation ?? null, [
      GovukValidators.required('Enter an explanation'),
      GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
    ]),
  });
}
