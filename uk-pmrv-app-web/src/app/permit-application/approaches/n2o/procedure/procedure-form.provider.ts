import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { N2OMonitoringApproach, ProcedureForm } from 'pmrv-api';

import { PERMIT_TASK_FORM } from '../../../shared/permit-task-form.token';
import { ProcedureFormComponent } from '../../../shared/procedure-form/procedure-form.component';
import { PermitApplicationStore } from '../../../store/permit-application.store';

export const procedureFormProvider = {
  provide: PERMIT_TASK_FORM,
  deps: [FormBuilder, PermitApplicationStore, ActivatedRoute],
  useFactory: (fb: FormBuilder, store: PermitApplicationStore, route: ActivatedRoute) => {
    const state = store.getValue();
    const keys = route.snapshot.data.taskKey.split('.');
    const taskKey = keys[keys.length - 1];
    const value = (state.permit.monitoringApproaches?.N2O as N2OMonitoringApproach)[taskKey] as ProcedureForm;

    return fb.group(ProcedureFormComponent.controlsFactory(value, !state.isEditable));
  },
};
