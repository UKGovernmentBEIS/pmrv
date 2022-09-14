import { FormBuilder } from '@angular/forms';

import { PFCMonitoringApproach } from 'pmrv-api';

import { PERMIT_TASK_FORM } from '../../../../shared/permit-task-form.token';
import { ProcedureFormComponent } from '../../../../shared/procedure-form/procedure-form.component';
import { PermitApplicationStore } from '../../../../store/permit-application.store';

export const determinationInstallationFormProvider = {
  provide: PERMIT_TASK_FORM,
  deps: [FormBuilder, PermitApplicationStore],
  useFactory: (fb: FormBuilder, store: PermitApplicationStore) => {
    const state = store.getValue();

    return fb.group(
      ProcedureFormComponent.controlsFactory(
        (state.permit.monitoringApproaches?.PFC as PFCMonitoringApproach).tier2EmissionFactor.determinationInstallation,
        !state.isEditable,
      ),
    );
  },
};
