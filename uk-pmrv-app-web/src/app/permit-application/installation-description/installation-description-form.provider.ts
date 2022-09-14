import { FormBuilder } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

import { PERMIT_TASK_FORM } from '../shared/permit-task-form.token';
import { PermitApplicationStore } from '../store/permit-application.store';

export const installationDescriptionFormProvider = {
  provide: PERMIT_TASK_FORM,
  deps: [FormBuilder, PermitApplicationStore],
  useFactory: (fb: FormBuilder, store: PermitApplicationStore) => {
    const value = store.permit.installationDescription;

    return fb.group({
      mainActivitiesDesc: [
        { value: value?.mainActivitiesDesc ?? null, disabled: !store.getValue().isEditable },
        [
          GovukValidators.required('Enter the primary purpose of the installation'),
          GovukValidators.maxLength(10000, 'The activities text should not be more than 10000 characters'),
        ],
      ],
      siteDescription: [
        { value: value?.siteDescription ?? null, disabled: !store.getValue().isEditable },
        [
          GovukValidators.required('Enter the description of the site'),
          GovukValidators.maxLength(10000, 'The description of the site text should not be more than 10000 characters'),
        ],
      ],
    });
  },
};
