import { FormBuilder } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

import { RequestTaskFileService } from '../../shared/services/request-task-file-service/request-task-file.service';
import { PERMIT_TASK_FORM } from '../shared/permit-task-form.token';
import { PermitApplicationStore } from '../store/permit-application.store';

export const monitoringMethodologyPlanAddFormFactory = {
  provide: PERMIT_TASK_FORM,
  deps: [FormBuilder, PermitApplicationStore, RequestTaskFileService],
  useFactory: (fb: FormBuilder, store: PermitApplicationStore) => {
    const state = store.getValue();
    const value = state.permit.monitoringMethodologyPlans;

    return fb.group({
      exist: [
        { value: value?.exist ?? null, disabled: !state.isEditable },
        { validators: GovukValidators.required('Select Yes or No') },
      ],
    });
  },
};
