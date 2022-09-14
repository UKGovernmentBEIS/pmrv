import { FormBuilder } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

import { PERMIT_SURRENDER_TASK_FORM } from '../../../../core/permit-surrender-task-form.token';
import { PermitSurrenderStore } from '../../../../store/permit-surrender.store';

export const reasonFormProvider = {
  provide: PERMIT_SURRENDER_TASK_FORM,
  deps: [FormBuilder, PermitSurrenderStore],
  useFactory: (fb: FormBuilder, store: PermitSurrenderStore) => {
    const state = store.getValue();
    const reviewDetermination = state.reviewDetermination;

    return fb.group({
      reason: [
        {
          value: reviewDetermination?.reason ?? null,
          disabled: !state.isEditable,
        },
        [
          GovukValidators.required(
            reviewDetermination.type === 'GRANTED'
              ? 'Enter a note to support the grant decision'
              : 'Enter a reason to support the reject decision',
          ),
          GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
        ],
      ],
    });
  },
};
