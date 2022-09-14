import { FormBuilder } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

import { PERMIT_SURRENDER_TASK_FORM } from '../../../core/permit-surrender-task-form.token';
import { PermitSurrenderStore } from '../../../store/permit-surrender.store';

export const allowancesDateFormProvider = {
  provide: PERMIT_SURRENDER_TASK_FORM,
  deps: [FormBuilder, PermitSurrenderStore],
  useFactory: (fb: FormBuilder, store: PermitSurrenderStore) => {
    const state = store.getValue();
    const allowancesSurrenderDate = state.cessation.allowancesSurrenderDate;
    const disabled = !state.isEditable;

    return fb.group({
      allowancesSurrenderDate: [
        { value: allowancesSurrenderDate ? new Date(allowancesSurrenderDate) : null, disabled },
        GovukValidators.required('Enter the date when the installation allowances were surrendered'),
      ],
    });
  },
};
