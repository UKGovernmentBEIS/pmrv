import { FormBuilder } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

import { PERMIT_SURRENDER_TASK_FORM } from '../../../core/permit-surrender-task-form.token';
import { PermitSurrenderStore } from '../../../store/permit-surrender.store';

export const allowancesNumberFormProvider = {
  provide: PERMIT_SURRENDER_TASK_FORM,
  deps: [FormBuilder, PermitSurrenderStore],
  useFactory: (fb: FormBuilder, store: PermitSurrenderStore) => {
    const state = store.getValue();
    const disabled = !state.isEditable;

    return fb.group({
      numberOfSurrenderAllowances: [
        { value: state.cessation?.numberOfSurrenderAllowances ?? null, disabled },
        [
          GovukValidators.required('Enter the number of allowances that were surrendered'),
          GovukValidators.min(0, 'The min value is 0'),
          GovukValidators.max(99999999, 'The max limit value is 99,999,999'),
          GovukValidators.pattern('[0-9]*', 'Insert valid number of allowances'),
        ],
      ],
    });
  },
};
