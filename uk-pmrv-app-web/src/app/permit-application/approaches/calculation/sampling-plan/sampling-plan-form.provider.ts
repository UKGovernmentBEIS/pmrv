import { InjectionToken } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

import { CalculationMonitoringApproach } from 'pmrv-api';

import { ProcedureFormComponent } from '../../../shared/procedure-form/procedure-form.component';
import { PermitApplicationStore } from '../../../store/permit-application.store';

export const SAMPLING_PLAN_FORM = new InjectionToken<FormGroup>('Sampling plan task form');

export const samplingPlanFormProvider = {
  provide: SAMPLING_PLAN_FORM,
  deps: [FormBuilder, PermitApplicationStore],
  useFactory: (fb: FormBuilder, store: PermitApplicationStore) => {
    const state = store.getValue();
    const value = (state.permit.monitoringApproaches?.CALCULATION as CalculationMonitoringApproach).samplingPlan;

    return fb.group({
      exist: [
        { value: value?.exist ?? null, disabled: !state.isEditable },
        { validators: GovukValidators.required('Select Yes or No'), updateOn: 'change' },
      ],
      analysis: fb.group(ProcedureFormComponent.controlsFactory(value?.details?.analysis)),
    });
  },
};
