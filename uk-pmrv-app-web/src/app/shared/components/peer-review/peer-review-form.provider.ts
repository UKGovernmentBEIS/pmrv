import { FactoryProvider, InjectionToken } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

export const PEER_REVIEW_FORM = new InjectionToken<FormGroup>('Notify operator form');

export const peerReviewFormFactory: FactoryProvider = {
  provide: PEER_REVIEW_FORM,
  useFactory: (fb: FormBuilder) =>
    fb.group({
      assignees: [null, GovukValidators.required('Select a peer reviewer')],
    }),
  deps: [FormBuilder],
};
