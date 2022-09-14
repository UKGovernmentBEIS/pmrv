import { FormBuilder } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

import { PFCMonitoringApproach } from 'pmrv-api';

import { PERMIT_TASK_FORM } from '../../../shared/permit-task-form.token';
import { PermitApplicationStore } from '../../../store/permit-application.store';

export const descriptionFormProvider = {
  provide: PERMIT_TASK_FORM,
  deps: [FormBuilder, PermitApplicationStore],
  useFactory: (fb: FormBuilder, store: PermitApplicationStore) => {
    const state = store.getValue();
    const value = (state.permit.monitoringApproaches?.PFC as PFCMonitoringApproach)?.approachDescription;

    return fb.group({
      approachDescription: [
        { value: value ?? null, disabled: !state.isEditable },
        [
          GovukValidators.required('Enter the approach description used to determine perfluorocarbons'),
          GovukValidators.maxLength(
            30000,
            'The approach description used to determine perfluorocarbons should not be more than 30000 characters',
          ),
        ],
      ],
    });
  },
};
