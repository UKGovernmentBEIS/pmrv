import { FormBuilder } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

import { N2OMonitoringApproach } from 'pmrv-api';

import { PERMIT_TASK_FORM } from '../../../shared/permit-task-form.token';
import { PermitApplicationStore } from '../../../store/permit-application.store';

export const descriptionFormProvider = {
  provide: PERMIT_TASK_FORM,
  deps: [FormBuilder, PermitApplicationStore],
  useFactory: (fb: FormBuilder, store: PermitApplicationStore) => {
    const state = store.getValue();
    const value = (state.permit.monitoringApproaches?.N2O as N2OMonitoringApproach)?.approachDescription;

    return fb.group({
      approachDescription: [
        { value: value ?? null, disabled: !state.isEditable },
        [
          GovukValidators.required('Enter approach description'),
          GovukValidators.maxLength(30000, 'The approach description should not be more than 30000 characters'),
        ],
      ],
    });
  },
};
