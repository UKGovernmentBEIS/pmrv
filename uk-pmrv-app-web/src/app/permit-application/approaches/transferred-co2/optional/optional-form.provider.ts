import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { GovukValidators } from 'govuk-components';

import { ProcedureOptionalForm, TransferredCO2MonitoringApproach } from 'pmrv-api';

import { PERMIT_TASK_FORM } from '../../../shared/permit-task-form.token';
import { ProcedureFormComponent } from '../../../shared/procedure-form/procedure-form.component';
import { PermitApplicationStore } from '../../../store/permit-application.store';

export const optionalFormProvider = {
  provide: PERMIT_TASK_FORM,
  deps: [FormBuilder, PermitApplicationStore, ActivatedRoute],
  useFactory: (fb: FormBuilder, store: PermitApplicationStore, route: ActivatedRoute) => {
    const state = store.getValue();
    const keys = route.snapshot.data.taskKey.split('.');
    const taskKey = keys[keys.length - 1];
    const value = (state.permit.monitoringApproaches?.TRANSFERRED_CO2 as TransferredCO2MonitoringApproach)[
      taskKey
    ] as ProcedureOptionalForm;

    return fb.group({
      exist: [
        { value: value?.exist ?? null, disabled: !state.isEditable },
        { validators: GovukValidators.required('Select Yes or No'), updateOn: 'change' },
      ],
      procedureForm: fb.group(ProcedureFormComponent.controlsFactory(value?.procedureForm)),
    });
  },
};
