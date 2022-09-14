import { FormBuilder, FormGroup, ValidationErrors, ValidatorFn } from '@angular/forms';

import { GovukDatePipe } from '@shared/pipes/govuk-date.pipe';
import moment from 'moment';

import { GovukValidators } from 'govuk-components';

import { PermitSurrenderReviewDeterminationGrant } from 'pmrv-api';

import { PERMIT_SURRENDER_TASK_FORM } from '../../../../core/permit-surrender-task-form.token';
import { PermitSurrenderStore } from '../../../../store/permit-surrender.store';

export const noticeDateFormProvider = {
  provide: PERMIT_SURRENDER_TASK_FORM,
  deps: [FormBuilder, PermitSurrenderStore],
  useFactory: (fb: FormBuilder, store: PermitSurrenderStore) => {
    const state = store.getValue();
    const reviewDetermination = state.reviewDetermination as PermitSurrenderReviewDeterminationGrant;

    return fb.group({
      noticeDate: [
        {
          value: reviewDetermination?.noticeDate ? new Date(reviewDetermination.noticeDate) : null,
          disabled: !state.isEditable,
        },
        {
          validators: [GovukValidators.required('Enter the effective date of the notice'), minDateValidator()],
        },
      ],
    });
  },
};

function minDateValidator(): ValidatorFn {
  return (group: FormGroup): ValidationErrors => {
    const govukDatePipe = new GovukDatePipe();
    const after28Days = moment().add(28, 'd').set({ hour: 0, minute: 0, second: 0, millisecond: 0 });
    return moment(group.value).isAfter(after28Days)
      ? null
      : {
          invalidNoticeDate: `Effective date must be the same as or after ${govukDatePipe.transform(
            new Date(after28Days.clone().add(1, 'd').toISOString()),
          )}`,
        };
  };
}
