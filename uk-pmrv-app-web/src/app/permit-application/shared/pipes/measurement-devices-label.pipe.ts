import { Pipe, PipeTransform } from '@angular/core';

import { map, Observable } from 'rxjs';

import { MeasurementDevicesTypePipe } from '../../measurement-devices/measurement-devices-summary/measurement-devices-type.pipe';
import { PermitApplicationStore } from '../../store/permit-application.store';

@Pipe({
  name: 'measurementDevicesLabel',
})
export class MeasurementDevicesLabelPipe implements PipeTransform {
  constructor(private readonly store: PermitApplicationStore) {}

  transform(deviceId: string): Observable<unknown> {
    const measurementDevicesType = new MeasurementDevicesTypePipe();

    return this.store.getTask('measurementDevicesOrMethods').pipe(
      map((devices) => devices.find((device) => device.id === deviceId)),
      map((device) =>
        device
          ? `${device.reference}, ${measurementDevicesType.transform(device.type)} ${
              device.uncertaintySpecified ? `, Specified uncertainty ±${device.specifiedUncertaintyPercentage}` : ''
            }`
          : null,
      ),
    );
  }
}
