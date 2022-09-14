import { FormBuilder, FormControl, FormGroup } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

import { MeasurementDevice, TransferredCO2MonitoringApproach } from 'pmrv-api';

import { PERMIT_TASK_FORM } from '../../../shared/permit-task-form.token';
import { PermitApplicationStore } from '../../../store/permit-application.store';

export const temperatureFormProvider = {
  provide: PERMIT_TASK_FORM,
  deps: [FormBuilder, PermitApplicationStore],
  useFactory: (fb: FormBuilder, store: PermitApplicationStore) => {
    const state = store.getValue();
    const value = (state.permit.monitoringApproaches?.TRANSFERRED_CO2 as TransferredCO2MonitoringApproach)
      ?.temperaturePressure;

    return fb.group({
      exist: [
        { value: value?.exist ?? null, disabled: !state.isEditable },
        { validators: GovukValidators.required('Select Yes or No'), updateOn: 'change' },
      ],
      measurementDevices: fb.array(
        value?.measurementDevices?.map(createAnotherMeasurementDevice) ?? [createAnotherMeasurementDevice()],
      ),
    });
  },
};

export function createAnotherMeasurementDevice(value?: MeasurementDevice): FormGroup {
  return new FormGroup({
    reference: new FormControl(value?.reference ?? null, [
      GovukValidators.required('Provide a reference'),
      GovukValidators.maxLength(
        10000,
        'The reference of the measurement device should not be more than 10000 characters',
      ),
    ]),
    type: new FormControl(value?.type ?? null, [GovukValidators.required('Select a type of measurement device')]),
    otherTypeName: new FormControl({ value: value?.otherTypeName ?? null, disabled: value?.type !== 'OTHER' }, [
      GovukValidators.required('Enter a short name'),
      GovukValidators.maxLength(10000, 'The short name should not be more than 10000 characters'),
    ]),
    location: new FormControl(value?.location ?? null, [
      GovukValidators.required('Enter a location'),
      GovukValidators.maxLength(
        10000,
        'The location of the measurement device should not be more than 10000 characters',
      ),
    ]),
  });
}
