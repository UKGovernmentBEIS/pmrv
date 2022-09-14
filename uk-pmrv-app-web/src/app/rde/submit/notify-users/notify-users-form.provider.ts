import { FactoryProvider, InjectionToken } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

import { RdeStore } from '../../store/rde.store';

export const RDE_FORM = new InjectionToken<FormGroup>('Rde form');

export const notifyUsersFormFactory: FactoryProvider = {
  provide: RDE_FORM,
  deps: [FormBuilder, RdeStore],
  useFactory: (fb: FormBuilder, store: RdeStore) => {
    const state = store.getState();

    return fb.group({
      users: [state?.rdePayload?.operators ?? null, { updateOn: 'change' }],
      assignees: [
        state?.rdePayload?.signatory ?? null,
        GovukValidators.required('Select a name to appear on the official notice document.'),
      ],
    });
  },
};
