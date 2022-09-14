import { FormBuilder } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

import { PERMIT_SURRENDER_TASK_FORM } from '../../../core/permit-surrender-task-form.token';
import { PermitSurrenderStore } from '../../../store/permit-surrender.store';

export const notesFormProvider = {
  provide: PERMIT_SURRENDER_TASK_FORM,
  deps: [FormBuilder, PermitSurrenderStore],
  useFactory: (fb: FormBuilder, store: PermitSurrenderStore) => {
    const state = store.getValue();
    const disabled = !state.isEditable;

    return fb.group({
      notes: [
        { value: state.cessation?.notes ?? null, disabled },
        [
          GovukValidators.required('Enter notes about the cessation'),
          GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
        ],
      ],
    });
  },
};
