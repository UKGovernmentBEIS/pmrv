import { InjectionToken } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

import { CalculationMonitoringApproach } from 'pmrv-api';

import { ProcedureFormComponent } from '../../../../shared/procedure-form/procedure-form.component';
import { PermitApplicationStore } from '../../../../store/permit-application.store';

export const RECONCILIATION_FORM = new InjectionToken<FormGroup>('Appropriateness task form');

export const reconciliationFormProvider = {
  provide: RECONCILIATION_FORM,
  deps: [FormBuilder, PermitApplicationStore],
  useFactory: (fb: FormBuilder, store: PermitApplicationStore) => {
    const state = store.getValue();
    const value = (state.permit.monitoringApproaches?.CALCULATION as CalculationMonitoringApproach).samplingPlan
      ?.details?.yearEndReconciliation;

    return fb.group({
      exist: [
        { value: value?.exist ?? null, disabled: !state.isEditable },
        { validators: GovukValidators.required('Select Yes or No'), updateOn: 'change' },
      ],
      procedureForm: fb.group(ProcedureFormComponent.controlsFactory(value?.procedureForm)),
    });
  },
};
