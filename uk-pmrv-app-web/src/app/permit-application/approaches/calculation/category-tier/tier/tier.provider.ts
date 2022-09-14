import { FormBuilder, FormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { GovukValidators } from 'govuk-components';

import { PERMIT_TASK_FORM } from '../../../../shared/permit-task-form.token';
import { PermitApplicationStore } from '../../../../store/permit-application.store';
import { getSubtaskData } from '../category-tier';
import { formTierOptionsMap } from './tier.map';

export const tierProvider = {
  provide: PERMIT_TASK_FORM,
  deps: [FormBuilder, PermitApplicationStore, ActivatedRoute],
  useFactory: (fb: FormBuilder, store: PermitApplicationStore, route: ActivatedRoute): FormGroup => {
    const index = Number(route.snapshot.paramMap.get('index'));
    const state = store.getValue();
    const disabled = !state.isEditable;

    const statusKey = route.snapshot.data.statusKey;
    const subtaskData = getSubtaskData(state, index, statusKey);

    const formGroup = {
      tier: [{ value: subtaskData?.tier ?? null, disabled }, GovukValidators.required('Select a tier')],
      ...getTierOptionsConditionalControls(formTierOptionsMap[statusKey], subtaskData, disabled),
    };

    return fb.group(formGroup);
  },
};

function getTierOptionsConditionalControls(tierOptions, parameterData, disabled) {
  const conditionalControls = [];

  tierOptions.options.forEach((option) => {
    if (option.hasConditionalContent) {
      conditionalControls[`isHighestRequiredTier_${option.value}`] = [
        { value: parameterData?.tier === option.value ? parameterData?.isHighestRequiredTier : null, disabled },
        GovukValidators.required('Select Yes or No'),
      ];
    }
  });

  return conditionalControls;
}
