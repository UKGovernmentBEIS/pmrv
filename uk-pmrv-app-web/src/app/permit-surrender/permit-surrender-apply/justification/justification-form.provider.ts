import { FormBuilder } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

import { PermitSurrender } from 'pmrv-api';

import { PERMIT_SURRENDER_TASK_FORM } from '../../core/permit-surrender-task-form.token';
import { PermitSurrenderStore } from '../../store/permit-surrender.store';

export const justificationFormProvider = {
  provide: PERMIT_SURRENDER_TASK_FORM,
  deps: [FormBuilder, PermitSurrenderStore],
  useFactory: (fb: FormBuilder, store: PermitSurrenderStore) => {
    const state = store.getValue();
    const permitSurrender = state.permitSurrender as PermitSurrender;

    return fb.group({
      justification: [
        {
          value: permitSurrender?.justification ? permitSurrender.justification : null,
          disabled: !state.isEditable,
        },
        [
          GovukValidators.required('Enter a reason for why activities have stopped'),
          GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
        ],
      ],
    });
  },
};
