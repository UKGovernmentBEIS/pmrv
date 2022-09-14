import { FormBuilder } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

import { PermitSurrenderReviewDeterminationReject } from 'pmrv-api';

import { PERMIT_SURRENDER_TASK_FORM } from '../../../../core/permit-surrender-task-form.token';
import { PermitSurrenderStore } from '../../../../store/permit-surrender.store';

export const refundFormProvider = {
  provide: PERMIT_SURRENDER_TASK_FORM,
  deps: [FormBuilder, PermitSurrenderStore],
  useFactory: (fb: FormBuilder, store: PermitSurrenderStore) => {
    const state = store.getValue();
    const reviewDetermination = state.reviewDetermination as PermitSurrenderReviewDeterminationReject;
    const disabled = !state.isEditable;

    return fb.group({
      shouldFeeBeRefundedToOperator: [
        {
          value: reviewDetermination?.shouldFeeBeRefundedToOperator ?? null,
          disabled,
        },
        GovukValidators.required('Select Yes or No'),
      ],
    });
  },
};
