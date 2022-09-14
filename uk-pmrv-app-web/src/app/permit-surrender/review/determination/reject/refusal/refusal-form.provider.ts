import { FormBuilder } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

import { PermitSurrenderReviewDeterminationReject } from 'pmrv-api';

import { PERMIT_SURRENDER_TASK_FORM } from '../../../../core/permit-surrender-task-form.token';
import { PermitSurrenderStore } from '../../../../store/permit-surrender.store';

export const refusalFormProvider = {
  provide: PERMIT_SURRENDER_TASK_FORM,
  deps: [FormBuilder, PermitSurrenderStore],
  useFactory: (fb: FormBuilder, store: PermitSurrenderStore) => {
    const state = store.getValue();
    const reviewDetermination = state.reviewDetermination as PermitSurrenderReviewDeterminationReject;
    const disabled = !state.isEditable;

    return fb.group({
      officialRefusalLetter: [
        {
          value: reviewDetermination?.officialRefusalLetter ?? null,
          disabled,
        },
        [
          GovukValidators.required('Enter text to be included in the official notice'),
          GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
        ],
      ],
    });
  },
};
