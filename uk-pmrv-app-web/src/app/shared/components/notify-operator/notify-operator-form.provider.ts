import { FactoryProvider, InjectionToken } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

export const NOTIFY_OPERATOR_FORM = new InjectionToken<FormGroup>('Notify operator form');

export const notifyOperatorFormFactory: FactoryProvider = {
  provide: NOTIFY_OPERATOR_FORM,
  useFactory: (fb: FormBuilder) =>
    fb.group({
      users: [[], { updateOn: 'change' }],
      contacts: [[], { updateOn: 'change' }],
      assignees: [null, GovukValidators.required('Select a name to appear on the official notice document.')],
    }),
  deps: [FormBuilder],
};
