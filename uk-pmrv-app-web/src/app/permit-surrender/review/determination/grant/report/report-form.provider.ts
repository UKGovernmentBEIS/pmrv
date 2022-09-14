import { FormBuilder } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

import { PermitSurrenderReviewDeterminationGrant } from 'pmrv-api';

import { PERMIT_SURRENDER_TASK_FORM } from '../../../../core/permit-surrender-task-form.token';
import { PermitSurrenderStore } from '../../../../store/permit-surrender.store';

export const reportFormProvider = {
  provide: PERMIT_SURRENDER_TASK_FORM,
  deps: [FormBuilder, PermitSurrenderStore],
  useFactory: (fb: FormBuilder, store: PermitSurrenderStore) => {
    const state = store.getValue();
    const reviewDetermination = state.reviewDetermination as PermitSurrenderReviewDeterminationGrant;
    const disabled = !state.isEditable;

    return fb.group({
      reportRequired: [
        { value: reviewDetermination?.reportRequired ?? null, disabled },
        GovukValidators.required('Select Yes or No'),
      ],
      reportDate: [
        { value: reviewDetermination?.reportDate ? new Date(reviewDetermination.reportDate) : null, disabled },
        GovukValidators.required('Enter the date when the surrender report is required'),
      ],
    });
  },
};
