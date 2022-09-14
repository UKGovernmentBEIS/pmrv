import { FormBuilder } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

import { FallbackMonitoringApproach } from 'pmrv-api';

import { PERMIT_TASK_FORM } from '../../../shared/permit-task-form.token';
import { PermitApplicationStore } from '../../../store/permit-application.store';

export const descriptionFormProvider = {
  provide: PERMIT_TASK_FORM,
  deps: [FormBuilder, PermitApplicationStore],
  useFactory: (fb: FormBuilder, store: PermitApplicationStore) => {
    const state = store.getValue();
    const value = state.permit.monitoringApproaches?.FALLBACK as FallbackMonitoringApproach;

    return fb.group({
      approachDescription: [
        { value: value?.approachDescription ?? null, disabled: !state.isEditable },
        [
          GovukValidators.required('Enter the approach description used to determine fall-back'),
          GovukValidators.maxLength(
            30000,
            'The approach description used to determine fall-back should not be more than 30000 characters',
          ),
        ],
      ],
      justification: [
        { value: value?.justification ?? null, disabled: !state.isEditable },
        [
          GovukValidators.required('Enter the justification used to determine fall-back'),
          GovukValidators.maxLength(
            10000,
            'The justification used to determine fall-back should not be more than 10000 characters',
          ),
        ],
      ],
    });
  },
};
