import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { GovukValidators } from 'govuk-components';

import { MeasurementDeviceOrMethod } from 'pmrv-api';

import { IdGeneratorService } from '../../../shared/services/id-generator.service';
import { PERMIT_TASK_FORM } from '../../shared/permit-task-form.token';
import { PermitApplicationStore } from '../../store/permit-application.store';

export const measurementDevicesAddFormFactory = {
  provide: PERMIT_TASK_FORM,
  deps: [FormBuilder, ActivatedRoute, PermitApplicationStore, IdGeneratorService],
  useFactory: (
    fb: FormBuilder,
    route: ActivatedRoute,
    store: PermitApplicationStore,
    idGeneratorService: IdGeneratorService,
  ) => {
    const measurementDevices = store.permit.measurementDevicesOrMethods.find(
      (device) => device.id === route.snapshot.paramMap.get('deviceId'),
    );

    return fb.group({
      id: [measurementDevices?.id ?? idGeneratorService.generateId()],
      reference: [measurementDevices?.reference ?? null, GovukValidators.required('Provide a reference')],
      type: [measurementDevices?.type ?? null, GovukValidators.required('Select a type of measurement device')],
      otherTypeName: [
        { value: measurementDevices?.otherTypeName ?? null, disabled: measurementDevices?.type !== 'OTHER' },
        GovukValidators.required('Enter a measurement device'),
      ],
      measurementRange: [
        measurementDevices?.measurementRange ?? null,
        GovukValidators.required('Enter a measurement range'),
      ],
      meteringRangeUnits: [
        measurementDevices?.meteringRangeUnits ?? null,
        GovukValidators.required('Enter the metering range units'),
      ],
      uncertaintySpecified: [
        measurementDevices?.uncertaintySpecified ?? null,
        GovukValidators.required('Select Yes or No'),
      ],
      specifiedUncertaintyPercentage: [
        measurementDevices?.specifiedUncertaintyPercentage ?? null,
        GovukValidators.builder(
          'Enter a specified uncertainty percentage between 0-99.999 and to a maximum of 3 decimal places',
          (control) =>
            [Validators.required, Validators.min(0), Validators.max(100), Validators.pattern('[0-9]*\\.?[0-9]{1,3}')]
              .map((validator) => validator(control))
              .filter((error) => !!error)
              .reduce((_, error) => error, null),
        ),
      ],
      location: [measurementDevices?.location ?? null, GovukValidators.required('Enter a location')],
    });
  },
};

export const typeOptions: MeasurementDeviceOrMethod['type'][] = [
  'BALANCE',
  'BELLOWS_METER',
  'BELT_WEIGHER',
  'CORIOLIS_METER',
  'ELECTRONIC_VOLUME_CONVERSION_INSTRUMENT',
  'GAS_CHROMATOGRAPH',
  'LEVEL_GAUGE',
  'ORIFICE_METER',
  'OTHER',
  'OVALRAD_METER',
  'ROTARY_METER',
  'TANK_DIP',
  'TURBINE_METER',
  'ULTRASONIC_METER',
  'VENTURI_METER',
  'VORTEX_METER',
  'WEIGHBRIDGE',
  'WEIGHSCALE',
];
