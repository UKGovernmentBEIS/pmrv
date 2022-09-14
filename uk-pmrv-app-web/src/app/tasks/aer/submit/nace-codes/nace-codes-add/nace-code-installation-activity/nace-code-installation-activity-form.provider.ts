import {
  AbstractControl,
  AsyncValidatorFn,
  FormBuilder,
  FormGroup,
  ValidationErrors,
  ValidatorFn,
} from '@angular/forms';

import { first, map, Observable } from 'rxjs';

import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import { AerApplicationSubmitRequestTaskPayload } from 'pmrv-api';

export const naceCodeInstallationActivityFormProvider = {
  provide: AER_TASK_FORM,
  deps: [FormBuilder, CommonTasksStore],
  useFactory: (fb: FormBuilder, store: CommonTasksStore) =>
    fb.group(
      {
        installationActivity: [null],
        installationActivityChild: [null],
      },
      {
        updateOn: 'change',
        validators: [atLeastOneRequiredValidator('Select a category')],
        asyncValidators: [duplicateCode(store)],
      },
    ),
};

export function atLeastOneRequiredValidator(message: string): ValidatorFn {
  return GovukValidators.builder(message, (group: FormGroup) => {
    const childValue = group.get('installationActivityChild').value;
    const activityValue = group.get('installationActivity').value;
    // activity value should start with a '_' to be a nace code. If it does not, it is a parent and a child has to be chosen.
    return childValue || (activityValue && activityValue.startsWith('_')) ? null : { atLeastOneRequired: true };
  });
}

export function duplicateCode(store: CommonTasksStore): AsyncValidatorFn {
  return (control: AbstractControl): Observable<ValidationErrors | null> => {
    const value = control.get('installationActivityChild').value || control.get('installationActivity').value;
    return store.pipe(
      first(),
      map((state) =>
        !(
          state.requestTaskItem.requestTask.payload as AerApplicationSubmitRequestTaskPayload
        ).aer.naceCodes?.codes?.includes(value)
          ? null
          : { duplicateCode: 'You have already added this NACE code' },
      ),
    );
  };
}
