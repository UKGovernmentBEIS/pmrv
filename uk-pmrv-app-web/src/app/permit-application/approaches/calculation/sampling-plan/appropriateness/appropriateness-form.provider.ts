import { InjectionToken } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';

import { CalculationMonitoringApproach } from 'pmrv-api';

import { ProcedureFormComponent } from '../../../../shared/procedure-form/procedure-form.component';
import { PermitApplicationStore } from '../../../../store/permit-application.store';

export const APPROPRIATENESS_FORM = new InjectionToken<FormGroup>('Appropriateness task form');

export const appropriatenessFormProvider = {
  provide: APPROPRIATENESS_FORM,
  deps: [FormBuilder, PermitApplicationStore],
  useFactory: (fb: FormBuilder, store: PermitApplicationStore) => {
    const value = (store.permit.monitoringApproaches?.CALCULATION as CalculationMonitoringApproach).samplingPlan
      ?.details?.appropriateness;

    return fb.group(ProcedureFormComponent.controlsFactory(value));
  },
};
