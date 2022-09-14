import { FormBuilder, FormControl, FormGroup } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

import { DateInputValidators } from '../../../../../../projects/govuk-components/src/lib/date-input/date-input.validators';
import { PERMIT_TASK_FORM } from '../../../shared/permit-task-form.token';
import { PermitApplicationStore } from '../../../store/permit-application.store';

export const emissionsFormProvider = {
  provide: PERMIT_TASK_FORM,
  deps: [FormBuilder, PermitApplicationStore],
  useFactory: (fb: FormBuilder, store: PermitApplicationStore) => {
    const state = store.getValue();
    const value = state.determination;
    const disabled = !state.isEditable;

    return fb.group({
      annualEmissionsTargets: fb.array(
        (value?.annualEmissionsTargets &&
          Object.keys(value.annualEmissionsTargets).map((key) =>
            createAnotherEmissionsTarget(key, value.annualEmissionsTargets[key], disabled),
          )) ?? [createAnotherEmissionsTarget(null, null, disabled)],
      ),
    });
  },
};

export function createAnotherEmissionsTarget(year?, emissions?, disabled = false): FormGroup {
  return new FormGroup({
    year: new FormControl({ value: year ?? null, disabled }, [
      GovukValidators.required('Enter a year value'),
      GovukValidators.pattern('[0-9]*', 'Enter a valid year value e.g. 2022'),
      GovukValidators.builder(
        `Enter a valid year value e.g. 2022`,
        DateInputValidators.dateFieldValidator('year', 1900, 2100),
      ),
    ]),
    emissions: new FormControl({ value: emissions ?? null, disabled }, [
      GovukValidators.required('Enter tonnes CO2e'),
      GovukValidators.notNaN('Enter a valid tonnes CO2e value'),
      GovukValidators.pattern('[0-9]*\\.?[0-9]{1}', 'Enter 1 decimal place only'),
      GovukValidators.min(0.1, 'Enter a valid tonnes CO2e value'),
    ]),
  });
}
