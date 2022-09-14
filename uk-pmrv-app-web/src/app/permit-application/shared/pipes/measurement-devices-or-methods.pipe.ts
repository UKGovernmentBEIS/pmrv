import { Pipe, PipeTransform } from '@angular/core';

import { MeasurementDeviceOrMethod } from 'pmrv-api';

@Pipe({ name: 'measurementDeviceOrMethod' })
export class MeasurementDeviceOrMethodPipe implements PipeTransform {
  transform(
    measurementDevicesOrMethods: MeasurementDeviceOrMethod[],
    measurementDeviceOrMethodId: string,
  ): MeasurementDeviceOrMethod {
    return measurementDevicesOrMethods.find((device) => device.id === measurementDeviceOrMethodId);
  }
}
