import { FormBuilder } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

import { TransferredCO2MonitoringApproach } from 'pmrv-api';

import { PERMIT_TASK_FORM } from '../../../shared/permit-task-form.token';
import { PermitApplicationStore } from '../../../store/permit-application.store';

export const accountingFormProvider = {
  provide: PERMIT_TASK_FORM,
  deps: [FormBuilder, PermitApplicationStore],
  useFactory: (fb: FormBuilder, store: PermitApplicationStore) => {
    const state = store.getValue();
    const value = (state.permit.monitoringApproaches?.TRANSFERRED_CO2 as TransferredCO2MonitoringApproach)
      ?.accountingEmissions;

    return fb.group({
      chemicallyBound: [
        { value: value?.chemicallyBound ?? null, disabled: !state.isEditable },
        { validators: GovukValidators.required('Select Yes or No') },
      ],
    });
  },
};
