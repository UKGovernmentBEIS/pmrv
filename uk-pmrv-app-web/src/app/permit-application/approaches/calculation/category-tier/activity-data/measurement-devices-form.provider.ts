import { AsyncValidatorFn, FormBuilder, ValidationErrors } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { first, map, Observable } from 'rxjs';

import { PERMIT_TASK_FORM } from '../../../../shared/permit-task-form.token';
import { PermitApplicationStore } from '../../../../store/permit-application.store';
import { areActivityDataValid } from '../../calculation-status';

export const measurementDevicesFormProvider = {
  provide: PERMIT_TASK_FORM,
  deps: [FormBuilder, PermitApplicationStore, ActivatedRoute],
  useFactory: (fb: FormBuilder, store: PermitApplicationStore, route: ActivatedRoute) => {
    const index = Number(route.snapshot.paramMap.get('index'));
    return fb.group(
      {},
      {
        asyncValidators: [validateMeasurementDevices(store, index)],
      },
    );
  },
};

function validateMeasurementDevices(store: PermitApplicationStore, index: number): AsyncValidatorFn {
  return (): Observable<ValidationErrors | null> =>
    store.pipe(
      first(),
      map((state) => {
        return areActivityDataValid(state, index)
          ? null
          : { validMeasurementDevicesOrMethods: 'Select at least one measurement device' };
      }),
    );
}
