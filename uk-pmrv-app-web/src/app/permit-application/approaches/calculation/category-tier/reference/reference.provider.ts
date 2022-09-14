import { FormBuilder, FormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { GovukValidators } from 'govuk-components';

import { PERMIT_TASK_FORM } from '../../../../shared/permit-task-form.token';
import { PermitApplicationStore } from '../../../../store/permit-application.store';
import { getSubtaskData } from '../category-tier';

export const referenceProvider = {
  provide: PERMIT_TASK_FORM,
  deps: [FormBuilder, PermitApplicationStore, ActivatedRoute],
  useFactory: (fb: FormBuilder, store: PermitApplicationStore, route: ActivatedRoute): FormGroup => {
    const index = Number(route.snapshot.paramMap.get('index'));
    const state = store.getValue();
    const disabled = !state.isEditable;

    const subtaskData = getSubtaskData(state, index, route.snapshot.data.statusKey);

    return fb.group({
      type: [
        { value: subtaskData?.standardReferenceSource?.type ?? null, disabled },
        GovukValidators.required('Select an option'),
      ],
      otherTypeDetails: [
        { value: subtaskData?.standardReferenceSource?.otherTypeDetails ?? null, disabled },
        [GovukValidators.required('Enter details'), GovukValidators.maxLength(500, 'Enter up to 500 characters')],
      ],
      defaultValue: [
        { value: subtaskData?.standardReferenceSource?.defaultValue ?? null, disabled },
        [GovukValidators.maxLength(500, 'Enter up to 500 characters')],
      ],
    });
  },
};
