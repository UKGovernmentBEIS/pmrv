import { FormBuilder } from '@angular/forms';

import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

export const industrialCrfCodeFormProvider = {
  provide: AER_TASK_FORM,
  deps: [FormBuilder, CommonTasksStore],
  useFactory: (fb: FormBuilder, store: CommonTasksStore) => {
    const state = store.getState();
    const group = fb.group(
      {
        industrialCrfCategory: [null],
        industrialCrf: [null, GovukValidators.required('Select a CRF code')],
      },
      {
        updateOn: 'change',
      },
    );
    if (!state.isEditable) {
      group.disable();
    }
    return group;
  },
};
