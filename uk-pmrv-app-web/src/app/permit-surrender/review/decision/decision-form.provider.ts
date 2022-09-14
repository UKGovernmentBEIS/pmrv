import { InjectionToken } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

import { PermitSurrenderStore } from '../../store/permit-surrender.store';

export const REVIEW_DECISION_FORM = new InjectionToken<FormGroup>('Permit surrender decision form');

export const reviewDecisionFormProvider = {
  provide: REVIEW_DECISION_FORM,
  deps: [FormBuilder, PermitSurrenderStore],
  useFactory: (fb: FormBuilder, store: PermitSurrenderStore) => {
    const reviewDecision = store.getState().reviewDecision;

    return fb.group({
      decision: [reviewDecision?.type ?? null, GovukValidators.required('Select a decision for this review group')],
      notes: [
        reviewDecision?.notes ?? null,
        [
          GovukValidators.required('Enter notes for this review group'),
          GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
        ],
      ],
    });
  },
};
