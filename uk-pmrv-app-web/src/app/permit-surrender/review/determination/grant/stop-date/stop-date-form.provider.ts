import { FormBuilder } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

import { PermitSurrenderReviewDeterminationGrant } from 'pmrv-api';

import { PERMIT_SURRENDER_TASK_FORM } from '../../../../core/permit-surrender-task-form.token';
import { PermitSurrenderStore } from '../../../../store/permit-surrender.store';

export const stopDateFormProvider = {
  provide: PERMIT_SURRENDER_TASK_FORM,
  deps: [FormBuilder, PermitSurrenderStore],
  useFactory: (fb: FormBuilder, store: PermitSurrenderStore) => {
    const state = store.getValue();
    const reviewDetermination = state.reviewDetermination as PermitSurrenderReviewDeterminationGrant;

    return fb.group({
      stopDate: [
        {
          value: reviewDetermination?.stopDate ? new Date(reviewDetermination.stopDate) : null,
          disabled: !state.isEditable,
        },
        GovukValidators.required('Enter the date regulated activities at the installation stopped'),
      ],
    });
  },
};
