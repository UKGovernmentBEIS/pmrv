import { FactoryProvider, InjectionToken } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

import { RfiStore } from '../../store/rfi.store';

export const NOTIFY_FORM = new InjectionToken<FormGroup>('Notify form');

export const notifyFormFactory: FactoryProvider = {
  provide: NOTIFY_FORM,
  deps: [FormBuilder, RfiStore],
  useFactory: (fb: FormBuilder, rfiStore: RfiStore) => {
    const operators = rfiStore.getValue().rfiSubmitPayload?.operators;
    const signatory = rfiStore.getValue().rfiSubmitPayload?.signatory;

    return fb.group({
      users: [operators, { updateOn: 'change' }],
      assignee: [signatory, GovukValidators.required('Select a name to appear on the official notice document.')],
    });
  },
};
