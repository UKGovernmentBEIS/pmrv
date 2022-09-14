import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { PFCMonitoringApproach } from 'pmrv-api';

import { PERMIT_TASK_FORM } from '../../../shared/permit-task-form.token';
import { ProcedureFormComponent } from '../../../shared/procedure-form/procedure-form.component';
import { PermitApplicationStore } from '../../../store/permit-application.store';

export const efficiencyFormProvider = {
  provide: PERMIT_TASK_FORM,
  deps: [FormBuilder, PermitApplicationStore, ActivatedRoute],
  useFactory: (fb: FormBuilder, store: PermitApplicationStore) => {
    const state = store.getValue();
    const value = (state.permit.monitoringApproaches?.PFC as PFCMonitoringApproach)?.collectionEfficiency;

    return fb.group(ProcedureFormComponent.controlsFactory(value, !state.isEditable));
  },
};
