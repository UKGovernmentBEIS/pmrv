import { FormBuilder } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

import { PERMIT_TASK_FORM } from '../shared/permit-task-form.token';
import { PermitApplicationStore } from '../store/permit-application.store';

export const estimatedEmissionsFormProvider = {
  provide: PERMIT_TASK_FORM,
  deps: [FormBuilder, PermitApplicationStore],
  useFactory: (fb: FormBuilder, store: PermitApplicationStore) => {
    const state = store.getState();
    const quantity = state.permit?.estimatedAnnualEmissions?.quantity;
    const disabled = !state.isEditable;

    return fb.group(
      {
        quantity: [
          { value: quantity ?? null, disabled },
          [
            GovukValidators.required('Enter tonnes CO2e'),
            GovukValidators.notNaN('Enter a valid tonnes CO2e value'),
            GovukValidators.pattern('[0-9]*\\.?[0-9]{1}', 'Enter 1 decimal place only'),
            GovukValidators.min(0.1, 'Enter a valid tonnes CO2e value'),
          ],
        ],
      },
      { updateOn: 'change' },
    );
  },
};
