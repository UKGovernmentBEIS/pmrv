import { FormBuilder } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

import { InherentCO2MonitoringApproach } from 'pmrv-api';

import { PERMIT_TASK_FORM } from '../../../shared/permit-task-form.token';
import { PermitApplicationStore } from '../../../store/permit-application.store';

export const descriptionFormProvider = {
  provide: PERMIT_TASK_FORM,
  deps: [FormBuilder, PermitApplicationStore],
  useFactory: (fb: FormBuilder, store: PermitApplicationStore) => {
    const state = store.getValue();
    const value = (state.permit.monitoringApproaches?.INHERENT_CO2 as InherentCO2MonitoringApproach)
      ?.approachDescription;

    return fb.group({
      approachDescription: [
        { value: value ?? null, disabled: !state.isEditable },
        [
          GovukValidators.required('Enter the approach description used to determine inherent CO2'),
          GovukValidators.maxLength(
            30000,
            'The approach description used to determine inherent CO2 should not be more than 30000 characters',
          ),
        ],
      ],
    });
  },
};
