import { FormBuilder } from '@angular/forms';

import { PERMIT_TASK_FORM } from '../../../shared/permit-task-form.token';
import { PermitApplicationStore } from '../../../store/permit-application.store';

export const reasonFormProvider = {
  provide: PERMIT_TASK_FORM,
  deps: [FormBuilder, PermitApplicationStore],
  useFactory: (fb: FormBuilder, store: PermitApplicationStore) => {
    const state = store.getValue();
    const value = state.determination;

    return fb.group({
      reason: [
        {
          value: value?.reason ?? null,
          disabled: !state.isEditable,
        },
      ],
    });
  },
};
