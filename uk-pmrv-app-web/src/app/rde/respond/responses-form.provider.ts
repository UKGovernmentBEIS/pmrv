import { InjectionToken } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

import { RdeStore } from '../store/rde.store';

export const RDE_FORM = new InjectionToken<FormGroup>('Rde form');

export const responseFormProvider = {
  provide: RDE_FORM,
  deps: [FormBuilder, RdeStore],
  useFactory: (fb: FormBuilder, store: RdeStore) => {
    const state = store.getState();

    return fb.group({
      decision: [{ value: null, disabled: !state.isEditable }, GovukValidators.required('Select a decision')],
      reason: [
        { value: null, disabled: !state.isEditable },
        [GovukValidators.required('Enter a reason'), GovukValidators.maxLength(10000, 'Enter up to 10000 characters')],
      ],
    });
  },
};
