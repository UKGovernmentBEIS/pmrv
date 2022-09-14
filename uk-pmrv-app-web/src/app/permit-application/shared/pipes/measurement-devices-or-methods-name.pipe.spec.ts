import { TestBed } from '@angular/core/testing';

import { mockClass } from '../../../../testing';
import { MeasurementDevicesTypePipe } from '../../measurement-devices/measurement-devices-summary/measurement-devices-type.pipe';
import { mockPermitApplyPayload } from '../../testing/mock-permit-apply-action';
import { MeasurementDeviceOrMethodNamePipe } from './measurement-devices-or-methods-name.pipe';

describe('MeasurementDeviceOrMethodNamePipe', () => {
  let pipe: MeasurementDeviceOrMethodNamePipe;

  const measurementDevicesTypePipe = mockClass(MeasurementDevicesTypePipe);

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [MeasurementDeviceOrMethodNamePipe],
      providers: [{ provide: MeasurementDevicesTypePipe, useValue: measurementDevicesTypePipe }],
    });

    pipe = new MeasurementDeviceOrMethodNamePipe(measurementDevicesTypePipe);
  });

  it('should create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should transform measurement device', () => {
    measurementDevicesTypePipe.transform.mockReturnValueOnce('devices type');

    const mockedDevice = mockPermitApplyPayload.permit.measurementDevicesOrMethods[0];

    expect(pipe.transform(mockedDevice)).toEqual(
      `${mockedDevice.reference}, devices type, Specified uncertainty \u00b12%`,
    );

    expect(measurementDevicesTypePipe.transform).toHaveBeenCalledTimes(1);
    expect(measurementDevicesTypePipe.transform).toHaveBeenCalledWith(mockedDevice.type);
  });
});
