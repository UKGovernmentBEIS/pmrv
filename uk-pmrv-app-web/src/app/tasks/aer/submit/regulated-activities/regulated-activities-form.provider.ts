import { AsyncValidatorFn, FormBuilder, ValidationErrors } from '@angular/forms';

import { first, map, Observable } from 'rxjs';

import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import { CommonTasksState } from '@tasks/store/common-tasks.state';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { AerApplicationSubmitRequestTaskPayload } from 'pmrv-api';

export const regulatedActivitiesFormFactory = {
  provide: AER_TASK_FORM,
  deps: [FormBuilder, CommonTasksStore],
  useFactory: (fb: FormBuilder, store: CommonTasksStore) =>
    fb.group(
      {},
      {
        asyncValidators: [missingCapacity(store), missingCrfCodes(store), missingIndustrialCrfCode(store)],
      },
    ),
};

function missingCrfCodes(stateChanges: Observable<CommonTasksState>): AsyncValidatorFn {
  return (): Observable<ValidationErrors | null> =>
    stateChanges.pipe(
      first(),
      map((state) =>
        (
          state.requestTaskItem.requestTask.payload as AerApplicationSubmitRequestTaskPayload
        ).aer.regulatedActivities?.every((activity) => !!activity.energyCrf || !!activity.industrialCrf)
          ? null
          : { missingCrf: 'Select at least one CRF code' },
      ),
    );
}

function missingIndustrialCrfCode(stateChanges: Observable<CommonTasksState>): AsyncValidatorFn {
  return (): Observable<ValidationErrors | null> =>
    stateChanges.pipe(
      first(),
      map((state) =>
        (
          state.requestTaskItem.requestTask.payload as AerApplicationSubmitRequestTaskPayload
        ).aer.regulatedActivities?.every(
          (activity) =>
            !activity.hasIndustrialCrf ||
            (!activity.energyCrf && !activity.industrialCrf) ||
            (activity.hasIndustrialCrf && activity.industrialCrf),
        )
          ? null
          : { missingIndustrialCrf: 'Missing CRF code from industrial processes sector' },
      ),
    );
}

function missingCapacity(stateChanges: Observable<CommonTasksState>): AsyncValidatorFn {
  return (): Observable<ValidationErrors | null> =>
    stateChanges.pipe(
      first(),
      map((state) =>
        (
          state.requestTaskItem.requestTask.payload as AerApplicationSubmitRequestTaskPayload
        ).aer.regulatedActivities?.every((activity) => !!activity.capacity)
          ? null
          : { missingCapacity: 'Enter total capacity' },
      ),
    );
}
