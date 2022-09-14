import { FormBuilder } from '@angular/forms';

import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import { AerApplicationSubmitRequestTaskPayload } from 'pmrv-api';

export const prtrFormProvider = {
  provide: AER_TASK_FORM,
  deps: [FormBuilder, CommonTasksStore],
  useFactory: (fb: FormBuilder, store: CommonTasksStore) => {
    const state = store.getValue();

    const item = (state.requestTaskItem.requestTask.payload as AerApplicationSubmitRequestTaskPayload).aer
      ?.pollutantRegisterActivities;

    return fb.group({
      exist: [
        { value: item?.exist ?? null, disabled: !state.isEditable },
        { validators: GovukValidators.required('Enter an option'), updateOn: 'change' },
      ],
    });
  },
};
