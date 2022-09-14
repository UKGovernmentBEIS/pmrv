import { FormBuilder } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

import { N2OMonitoringApproach } from 'pmrv-api';

import { PERMIT_TASK_FORM } from '../../../shared/permit-task-form.token';
import { ProcedureFormComponent } from '../../../shared/procedure-form/procedure-form.component';
import { PermitApplicationStore } from '../../../store/permit-application.store';

export const gasFormProvider = {
  provide: PERMIT_TASK_FORM,
  deps: [FormBuilder, PermitApplicationStore],
  useFactory: (fb: FormBuilder, store: PermitApplicationStore) => {
    const state = store.getValue();
    const value = (state.permit.monitoringApproaches?.N2O as N2OMonitoringApproach)?.gasFlowCalculation;

    return fb.group({
      exist: [
        { value: value?.exist ?? null, disabled: !state.isEditable },
        { validators: GovukValidators.required('Select Yes or No'), updateOn: 'change' },
      ],
      procedureForm: fb.group(ProcedureFormComponent.controlsFactory(value?.procedureForm)),
    });
  },
};
