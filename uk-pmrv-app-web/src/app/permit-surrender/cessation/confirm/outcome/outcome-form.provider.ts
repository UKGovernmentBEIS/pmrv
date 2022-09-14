import { FormBuilder } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

import { PERMIT_SURRENDER_TASK_FORM } from '../../../core/permit-surrender-task-form.token';
import { PermitSurrenderStore } from '../../../store/permit-surrender.store';

export const outcomeFormProvider = {
  provide: PERMIT_SURRENDER_TASK_FORM,
  deps: [FormBuilder, PermitSurrenderStore],
  useFactory: (fb: FormBuilder, store: PermitSurrenderStore) => {
    const state = store.getValue();
    const disabled = !state.isEditable;

    return fb.group({
      determinationOutcome: [
        { value: state.cessation?.determinationOutcome ?? null, disabled },
        GovukValidators.required('Select Approved or Rejected'),
      ],
    });
  },
};
