import { InjectionToken } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup, ValidatorFn } from '@angular/forms';

import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';
import { CommonTasksState } from '@tasks/store/common-tasks.state';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import {
  PermitNotificationFollowUpApplicationReviewRequestTaskPayload,
  PermitNotificationFollowUpWaitForAmendsRequestTaskPayload,
  RequestTaskDTO,
} from 'pmrv-api';

export const FOLLOW_UP_REVIEW_DECISION_FORM = new InjectionToken<FormGroup>('Review form');

export const followUpReviewDecisionFormProvider = {
  provide: FOLLOW_UP_REVIEW_DECISION_FORM,
  deps: [FormBuilder, CommonTasksStore, RequestTaskFileService],
  useFactory: (fb: FormBuilder, store: CommonTasksStore, requestTaskFileService: RequestTaskFileService) => {
    const state = store.getState();

    const { reviewDecision } = state.requestTaskItem.requestTask
      .payload as PermitNotificationFollowUpApplicationReviewRequestTaskPayload;

    return fb.group({
      type: [
        reviewDecision?.type ? reviewDecision?.type : null,
        { validators: GovukValidators.required('Select a decision'), updateOn: 'change' },
      ],
      changesRequired: [
        { value: reviewDecision?.changesRequired ?? null, disabled: reviewDecision?.type !== 'AMENDS_NEEDED' },
        [
          GovukValidators.required('Enter all the changes required'),
          GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
        ],
      ],
      files:
        state?.requestTaskItem.requestTask.type !== 'PERMIT_NOTIFICATION_FOLLOW_UP_WAIT_FOR_AMENDS'
          ? requestTaskFileService.buildFormControl(
              store,
              reviewDecision?.files ?? [],
              'followUpAttachments',
              'PERMIT_NOTIFICATION_FOLLOW_UP_UPLOAD_ATTACHMENT',
              false,
              reviewDecision?.type !== 'AMENDS_NEEDED',
            )
          : null,
      dueDate: [
        {
          value: reviewDecision?.dueDate ? new Date(reviewDecision?.dueDate) : null,
          disabled: reviewDecision?.type !== 'AMENDS_NEEDED',
        },
        addDueDateValidators(state),
      ],
      notes: [reviewDecision?.notes ?? null, GovukValidators.maxLength(10000, 'Enter up to 10000 characters')],
    });
  },
};

function addDueDateValidators(state: CommonTasksState) {
  const taskType: RequestTaskDTO['type'] = state.requestTaskItem.requestTask.type;
  const payload = state.requestTaskItem.requestTask.payload;
  const isAmends = 'PERMIT_NOTIFICATION_FOLLOW_UP_APPLICATION_REVIEW' !== taskType;
  const dateToCompare = isAmends
    ? (payload as PermitNotificationFollowUpWaitForAmendsRequestTaskPayload).reviewDecision?.dueDate
    : (payload as PermitNotificationFollowUpApplicationReviewRequestTaskPayload).followUpResponseExpirationDate;

  const dateValidator = dueDateValidator(dateToCompare);

  return isAmends ? [dateValidator, GovukValidators.required('Enter a date')] : [dateValidator];
}

function dueDateValidator(dateToCompare: string): ValidatorFn {
  return (control: AbstractControl): { [key: string]: string } | null => {
    return control.value && control.value < new Date()
      ? { invalidDate: `The date must be in the future` }
      : control.value && control.value <= new Date(dateToCompare)
      ? { invalidDate: `The new due date must be after the current due date` }
      : null;
  };
}
