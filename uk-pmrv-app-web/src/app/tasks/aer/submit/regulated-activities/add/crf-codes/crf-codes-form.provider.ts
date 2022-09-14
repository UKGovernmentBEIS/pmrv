import { FormBuilder, FormGroup, ValidatorFn } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import { AerApplicationSubmitRequestTaskPayload } from 'pmrv-api';

export const crfCodesFormProvider = {
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
        hasEnergyCrf: [[activity?.hasEnergyCrf] ?? null],
        hasIndustrialCrf: [[activity?.hasIndustrialCrf] ?? null],
      },
      {
        validators: [atLeastOneRequiredValidator('Select at least one sector')],
        updateOn: 'change',
      },
    );
    if (!state.isEditable) {
      group.disable();
    }
    return group;
  },
};

function atLeastOneRequiredValidator(message: string): ValidatorFn {
  return GovukValidators.builder(message, (group: FormGroup) => {
    const hasEnergyCrf = group.get('hasEnergyCrf').value?.[0] ?? false;
    const hasIndustrialCrf = group.get('hasIndustrialCrf').value?.[0] ?? false;
    return !!hasEnergyCrf || !!hasIndustrialCrf ? null : { atLeastOneRequired: true };
  });
}
