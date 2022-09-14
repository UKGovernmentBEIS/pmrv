import { InjectionToken } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';

import { CalculationMonitoringApproach } from 'pmrv-api';

import { RequestTaskFileService } from '../../../../../shared/services/request-task-file-service/request-task-file.service';
import { ProcedureFormComponent } from '../../../../shared/procedure-form/procedure-form.component';
import { PermitApplicationStore } from '../../../../store/permit-application.store';

export const PLAN_FORM = new InjectionToken<FormGroup>('Procedure plan task form');

export const planFormProvider = {
  provide: PLAN_FORM,
  deps: [FormBuilder, PermitApplicationStore, RequestTaskFileService],
  useFactory: (fb: FormBuilder, store: PermitApplicationStore, requestTaskFileService: RequestTaskFileService) => {
    const state = store.getValue();
    const value = (state.permit.monitoringApproaches?.CALCULATION as CalculationMonitoringApproach).samplingPlan
      ?.details?.procedurePlan;

    return fb.group({
      procedurePlan: fb.group(ProcedureFormComponent.controlsFactory(value)),
      planIds: requestTaskFileService.buildFormControl(
        store,
        value?.procedurePlanIds ?? [],
        'permitAttachments',
        store.getFileUploadSectionAttachmentActionContext(),
        false,
        !state.isEditable,
      ),
    });
  },
};
