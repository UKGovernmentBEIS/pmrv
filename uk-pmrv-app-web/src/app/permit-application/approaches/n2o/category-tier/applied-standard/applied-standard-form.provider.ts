import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { N2OMonitoringApproach } from 'pmrv-api';

import { AppliedStandardFormComponent } from '../../../../shared/approaches/applied-standard-form.component';
import { PERMIT_TASK_FORM } from '../../../../shared/permit-task-form.token';
import { PermitApplicationStore } from '../../../../store/permit-application.store';

export const appliedStandardFormProvider = {
  provide: PERMIT_TASK_FORM,
  deps: [FormBuilder, PermitApplicationStore, ActivatedRoute],
  useFactory: (fb: FormBuilder, store: PermitApplicationStore, route: ActivatedRoute) => {
    const state = store.getValue();
    const value =
      (state.permit.monitoringApproaches.N2O as N2OMonitoringApproach)?.sourceStreamCategoryAppliedTiers?.[
        Number(route.snapshot.paramMap.get('index'))
      ]?.appliedStandard ?? null;

    return fb.group(AppliedStandardFormComponent.controlsFactory(value, !state.isEditable));
  },
};
