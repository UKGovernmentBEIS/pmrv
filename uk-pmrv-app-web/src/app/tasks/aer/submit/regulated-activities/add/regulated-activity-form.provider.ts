import {
  AbstractControl,
  AsyncValidatorFn,
  FormBuilder,
  FormGroup,
  ValidationErrors,
  ValidatorFn,
} from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { first, map, Observable } from 'rxjs';

import { formGroupOptions } from '@shared/components/regulated-activities/regulated-activities-form-options';
import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import { AerApplicationSubmitRequestTaskPayload, RegulatedActivity } from 'pmrv-api';

export const regulatedActivityFormProvider = {
  provide: AER_TASK_FORM,
  deps: [FormBuilder, CommonTasksStore, ActivatedRoute],
  useFactory: (fb: FormBuilder, store: CommonTasksStore, route: ActivatedRoute) => {
    const state = store.getState();
    const activities =
      (state.requestTaskItem.requestTask.payload as AerApplicationSubmitRequestTaskPayload).aer?.regulatedActivities ??
      [];
    const activity = activities?.find((activity) => activity.id === route.snapshot.paramMap.get('activityId'));
    const category = findCategory(activity);
    const activityId = route.snapshot.paramMap.get('activityId');
    const group = fb.group(
      {
        activityCategory: [category ?? null],
        activity: [activity?.type ?? null],
      },
      {
        updateOn: 'change',
        validators: [atLeastOneRequiredValidator('Select an activity')],
        asyncValidators: [duplicateCode(store, activityId)],
      },
    );
    if (!state.isEditable) {
      group.disable();
    }
    return group;
  },
};

function findCategory(activity: RegulatedActivity): string {
  return (
    Object.entries(formGroupOptions).find((e) => e[1].includes(activity?.type) && e[1].length === 1)?.[1][0] ||
    Object.entries(formGroupOptions).find((e) => e[1].includes(activity?.type))?.[0]
  );
}

function atLeastOneRequiredValidator(message: string): ValidatorFn {
  return GovukValidators.builder(message, (group: FormGroup) => {
    const activityCategoryValue = group.get('activityCategory').value;
    const activityValue = group.get('activity').value;
    return activityValue ||
      Object.values(formGroupOptions)
        .reduce((acc, value) => acc.concat(value), [])
        .includes(activityCategoryValue)
      ? null
      : { atLeastOneRequired: true };
  });
}

function duplicateCode(store: CommonTasksStore, activityId: string): AsyncValidatorFn {
  return (control: AbstractControl): Observable<ValidationErrors | null> => {
    const value = control.get('activity').value || control.get('activityCategory').value;
    return store.pipe(
      first(),
      map((state) =>
        !(
          state.requestTaskItem.requestTask.payload as AerApplicationSubmitRequestTaskPayload
        ).aer.regulatedActivities?.find((a) => a.type === value && a.id !== activityId)
          ? null
          : { duplicateActivity: 'You have already added this regulated activity' },
      ),
    );
  };
}
