import { FormBuilder } from '@angular/forms';

import { createAbbreviationDefinition } from '@shared/components/abbreviations/abbreviation-definition-form';

import { GovukValidators } from 'govuk-components';

import { PERMIT_TASK_FORM } from '../shared/permit-task-form.token';
import { PermitApplicationStore } from '../store/permit-application.store';

export const abbreviationsFormProvider = {
  provide: PERMIT_TASK_FORM,
  deps: [FormBuilder, PermitApplicationStore],
  useFactory: (fb: FormBuilder, store: PermitApplicationStore) => {
    const value = store.permit.abbreviations;

    return fb.group({
      exist: [
        { value: value?.exist ?? null, disabled: !store.getValue().isEditable },
        { validators: GovukValidators.required('Select Yes or No'), updateOn: 'change' },
      ],
      abbreviationDefinitions: fb.array(value?.abbreviationDefinitions?.map(createAbbreviationDefinition) ?? []),
    });
  },
};
