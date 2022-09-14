import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import { AerApplicationSubmitRequestTaskPayload } from 'pmrv-api';

export const capacityFormProvider = {
  provide: AER_TASK_FORM,
  deps: [FormBuilder, CommonTasksStore, ActivatedRoute],
  useFactory: (fb: FormBuilder, store: CommonTasksStore, route: ActivatedRoute) => {
    const state = store.getState();
    const activities =
      (state.requestTaskItem.requestTask.payload as AerApplicationSubmitRequestTaskPayload).aer?.regulatedActivities ??
      [];
    const activity = activities?.find((activity) => activity.id === route.snapshot.paramMap.get('activityId'));
    const group = fb.group(
      {
        activityCapacity: [
          activity?.capacity ?? null,
          [GovukValidators.required('Enter total capacity'), GovukValidators.positiveNumber()],
        ],
        activityCapacityUnit: [activity?.capacityUnit ?? null, GovukValidators.required('Enter units')],
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
