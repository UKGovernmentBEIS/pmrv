import { AsyncValidatorFn, FormBuilder, ValidationErrors } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { first, map, Observable } from 'rxjs';

import { areMeasMeasuredEmissionsDevicesValid } from '../../approaches/measurement/measurement-status';
import { areN2OMeasuredEmissionsDevicesValid } from '../../approaches/n2o/n2o-status';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { PERMIT_TASK_FORM } from '../permit-task-form.token';

export const measurementDevicesFormProvider = {
  provide: PERMIT_TASK_FORM,
  deps: [FormBuilder, PermitApplicationStore, ActivatedRoute],
  useFactory: (fb: FormBuilder, store: PermitApplicationStore, route: ActivatedRoute) => {
    const index = Number(route.snapshot.paramMap.get('index'));
    const { taskKey } = route.snapshot.data;
    return fb.group(
      {},
      {
        asyncValidators: [validateMeasurementDevices(store, taskKey, index)],
      },
    );
  },
};

function validateMeasurementDevices(store: PermitApplicationStore, taskKey: string, index: number): AsyncValidatorFn {
  return (): Observable<ValidationErrors | null> =>
    store.pipe(
      first(),
      map((state) => {
        const areMeasuredEmissionsDevicesValid =
          taskKey === 'MEASUREMENT'
            ? areMeasMeasuredEmissionsDevicesValid(state, index)
            : areN2OMeasuredEmissionsDevicesValid(state, index);

        return areMeasuredEmissionsDevicesValid
          ? null
          : { validMeasurementDevicesOrMethods: 'Select at least one measurement device' };
      }),
    );
}
