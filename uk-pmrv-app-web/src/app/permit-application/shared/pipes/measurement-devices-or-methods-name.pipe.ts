import { Pipe, PipeTransform } from '@angular/core';

import { MeasurementDeviceOrMethod } from 'pmrv-api';

import { MeasurementDevicesTypePipe } from '../../measurement-devices/measurement-devices-summary/measurement-devices-type.pipe';

@Pipe({ name: 'measurementDeviceOrMethodName' })
export class MeasurementDeviceOrMethodNamePipe implements PipeTransform {
  constructor(private measurementDevicesTypePipe: MeasurementDevicesTypePipe) {}

  transform(measurementDevicesOrMethod: MeasurementDeviceOrMethod): string {
    const name = `${measurementDevicesOrMethod.reference}, ${this.measurementDevicesTypePipe.transform(
      measurementDevicesOrMethod.type,
    )}`;

    return measurementDevicesOrMethod.uncertaintySpecified
      ? `${name}, Specified uncertainty \u00b1${measurementDevicesOrMethod.specifiedUncertaintyPercentage}%`
      : name;
  }
}
