import { AsyncValidatorFn, FormBuilder, ValidationErrors } from '@angular/forms';

import { first, map, Observable } from 'rxjs';

import { TransferredCO2MonitoringApproach } from 'pmrv-api';

import { PERMIT_TASK_FORM } from '../../../shared/permit-task-form.token';
import { PermitApplicationState } from '../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../store/permit-application.store';

export const measurementDevicesFormProvider = {
  provide: PERMIT_TASK_FORM,
  deps: [FormBuilder, PermitApplicationStore],
  useFactory: (fb: FormBuilder, store: PermitApplicationStore) =>
    fb.group(
      {},
      {
        asyncValidators: [atLeastOneMeasurementDevice(store)],
      },
    ),
};

function atLeastOneMeasurementDevice(stateChanges: Observable<PermitApplicationState>): AsyncValidatorFn {
  return (): Observable<ValidationErrors | null> =>
    stateChanges.pipe(
      first(),
      map((state) => {
        const accounting = (state.permit.monitoringApproaches.TRANSFERRED_CO2 as TransferredCO2MonitoringApproach)
          .accountingEmissions;

        return !accounting.chemicallyBound
          ? !accounting.accountingEmissionsDetails.measurementDevicesOrMethods.every((id) =>
              state.permit.measurementDevicesOrMethods.some((device) => device.id === id),
            )
            ? accounting.accountingEmissionsDetails.measurementDevicesOrMethods.some((id) =>
                state.permit.measurementDevicesOrMethods.some((device) => device.id === id),
              )
              ? { measurementDeviceNotExist: 'Check your Answer - Error' }
              : { measurementDeviceNotExist: 'Select at least one measurement device' }
            : null
          : null;
      }),
    );
}
