import { InjectionToken } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup, ValidatorFn } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

import { PermitNotificationApplicationReviewRequestTaskPayload } from 'pmrv-api';

import { CommonTasksStore } from '../../../store/common-tasks.store';

export const REVIEW_FORM = new InjectionToken<FormGroup>('Review form');

export const decisionFormProvider = {
  provide: REVIEW_FORM,
  deps: [FormBuilder, CommonTasksStore],
  useFactory: (fb: FormBuilder, store: CommonTasksStore) => {
    const state = store.getState();

    const { reviewDecision } = state.requestTaskItem.requestTask
      .payload as PermitNotificationApplicationReviewRequestTaskPayload;

    return fb.group({
      type: [
        reviewDecision?.type === 'ACCEPTED' ? true : reviewDecision?.type === 'REJECTED' ? false : null,
        { validators: GovukValidators.required('Select a decision'), updateOn: 'change' },
      ],
      officialNotice: [
        reviewDecision?.officialNotice ?? null,
        [GovukValidators.required('Enter a summary'), GovukValidators.maxLength(10000, 'Enter up to 10000 characters')],
      ],
      followUpResponseRequired: [
        reviewDecision?.followUp?.followUpResponseRequired ?? null,
        { validators: GovukValidators.required('Select yes or no') },
      ],
      followUpRequest: [
        reviewDecision?.followUp?.followUpRequest ?? null,
        [GovukValidators.required('Enter details'), GovukValidators.maxLength(10000, 'Enter up to 10000 characters')],
      ],
      followUpResponseExpirationDate: [
        reviewDecision?.followUp?.followUpResponseExpirationDate
          ? new Date(reviewDecision?.followUp?.followUpResponseExpirationDate)
          : null,
        [futureDateValidator(), GovukValidators.required('Enter a date')],
      ],
      notes: [reviewDecision?.notes ?? null, GovukValidators.maxLength(10000, 'Enter up to 10000 characters')],
    });
  },
};

function futureDateValidator(): ValidatorFn {
  return (control: AbstractControl): { [key: string]: string } | null => {
    return control.value && control.value < new Date() ? { invalidDate: `The date must be in the future` } : null;
  };
}
