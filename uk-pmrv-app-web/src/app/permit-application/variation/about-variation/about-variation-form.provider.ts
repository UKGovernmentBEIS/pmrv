import { FormBuilder } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

import { PERMIT_TASK_FORM } from '../../shared/permit-task-form.token';
import { PermitApplicationStore } from '../../store/permit-application.store';

export const aboutVariationFormProvider = {
  provide: PERMIT_TASK_FORM,
  deps: [FormBuilder, PermitApplicationStore],
  useFactory: (fb: FormBuilder, store: PermitApplicationStore) => {
    const state = store.getState();
    const permitVariationDetails = state?.permitVariationDetails;

    return fb.group({
      reason: [permitVariationDetails?.reason ?? null, GovukValidators.required('Enter details of the change')],
    });
  },
};
