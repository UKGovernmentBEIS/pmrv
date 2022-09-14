import { FormBuilder, FormGroup, ValidationErrors, ValidatorFn } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import { AerApplicationSubmitRequestTaskPayload } from 'pmrv-api';

import { activityItemTypeMap } from '../activity-item';

export const activityFormProvider = {
  provide: AER_TASK_FORM,
  deps: [FormBuilder, CommonTasksStore, ActivatedRoute],
  useFactory: (fb: FormBuilder, store: CommonTasksStore, route: ActivatedRoute) => {
    const state = store.getValue();
    const activityItem = route.snapshot.queryParams?.activityItem ?? null;

    const activityError = 'Enter the activity';
    const sectorError = !activityItem ? 'Enter the relevant sector' : 'Enter the activity';

    return fb.group({
      activity: [
        { value: null, disabled: !state.isEditable },
        { validators: [GovukValidators.required(sectorError), duplicateActivityItemType(store)], updateOn: 'change' },
      ],
      subActivityA1: [
        { value: null, disabled: !state.isEditable || activityItem !== '_1_A' },
        { validators: [GovukValidators.required(activityError), duplicateActivityItemType(store)], updateOn: 'change' },
      ],
      subActivityA2: [
        { value: null, disabled: !state.isEditable || activityItem !== '_1_A' },
        { validators: [GovukValidators.required(activityError), duplicateActivityItemType(store)], updateOn: 'change' },
      ],
      subActivityA3: [
        { value: null, disabled: !state.isEditable || activityItem !== '_1_A' },
        { validators: [GovukValidators.required(activityError), duplicateActivityItemType(store)], updateOn: 'change' },
      ],
      subActivityA4: [
        { value: null, disabled: !state.isEditable || activityItem !== '_1_A' },
        { validators: [GovukValidators.required(activityError), duplicateActivityItemType(store)], updateOn: 'change' },
      ],
      subActivityA5: [
        { value: null, disabled: !state.isEditable || activityItem !== '_1_A' },
        { validators: [GovukValidators.required(activityError), duplicateActivityItemType(store)], updateOn: 'change' },
      ],
      subActivityB1: [
        { value: null, disabled: !state.isEditable || activityItem !== '_1_B' },
        { validators: [GovukValidators.required(activityError), duplicateActivityItemType(store)], updateOn: 'change' },
      ],
      subActivityB2: [
        { value: null, disabled: !state.isEditable || activityItem !== '_1_B' },
        { validators: [GovukValidators.required(activityError), duplicateActivityItemType(store)], updateOn: 'change' },
      ],
    });
  },
};

export const duplicateActivityItemType = (store: CommonTasksStore): ValidatorFn => {
  return (group: FormGroup): ValidationErrors | null => {
    const activityItemType = activityItemTypeMap[group.value];
    const pollutantRegisterActivities = (
      store.getState().requestTaskItem.requestTask.payload as AerApplicationSubmitRequestTaskPayload
    ).aer.pollutantRegisterActivities?.activities;

    return (pollutantRegisterActivities || []).find((x) => x === activityItemType)
      ? { duplicateCode: 'You have already added this activity' }
      : null;
  };
};
