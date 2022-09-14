import { FactoryProvider, InjectionToken } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

export const PEER_REVIEW_DECISION_FORM = new InjectionToken<FormGroup>('Peer review decision form');

export const peerReviewDecisionFormProvider: FactoryProvider = {
  provide: PEER_REVIEW_DECISION_FORM,
  useFactory: (fb: FormBuilder) =>
    fb.group({
      type: [null, GovukValidators.required('Enter your decision')],
      notes: [
        null,
        [GovukValidators.required('Enter notes'), GovukValidators.maxLength(10000, 'Enter up to 10000 characters')],
      ],
    }),
  deps: [FormBuilder],
};
