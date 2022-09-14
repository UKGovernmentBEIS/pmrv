import { FormBuilder } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

import { PERMIT_SURRENDER_TASK_FORM } from '../../../core/permit-surrender-task-form.token';
import { PermitSurrenderStore } from '../../../store/permit-surrender.store';

const getTwoDecimalPlacesValidator = GovukValidators.pattern(
  '[0-9]*\\.?[0-9]{0,2}',
  'Enter the number of emissions to 2 decimal place',
);

export const emissionsFormProvider = {
  provide: PERMIT_SURRENDER_TASK_FORM,
  deps: [FormBuilder, PermitSurrenderStore],
  useFactory: (fb: FormBuilder, store: PermitSurrenderStore) => {
    const state = store.getValue();
    const disabled = !state.isEditable;

    return fb.group({
      annualReportableEmissions: [
        { value: state.cessation?.annualReportableEmissions ?? null, disabled },
        [
          GovukValidators.required('Enter the annual reportable emissions'),
          GovukValidators.min(0.1, 'Annual reportable emissions tonnes of CO2e must be between 0 and 99,999,999.99'),
          GovukValidators.max(
            99999999.999,
            'Annual reportable emissions tonnes of CO2e must be between 0 and 99,999,999.99',
          ),
          getTwoDecimalPlacesValidator,
        ],
      ],
    });
  },
};
