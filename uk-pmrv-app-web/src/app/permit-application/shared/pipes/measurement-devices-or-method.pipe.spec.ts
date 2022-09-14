import { TestBed } from '@angular/core/testing';

import { mockPermitApplyPayload } from '../../testing/mock-permit-apply-action';
import { MeasurementDeviceOrMethodPipe } from './measurement-devices-or-methods.pipe';

describe('MeasurementDeviceOrMethodPipe', () => {
  let pipe: MeasurementDeviceOrMethodPipe;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [MeasurementDeviceOrMethodPipe],
    });
  });

  beforeEach(() => (pipe = new MeasurementDeviceOrMethodPipe()));

  it('should create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should find a measurement device by id', () => {
    expect(
      pipe.transform(
        mockPermitApplyPayload.permit.measurementDevicesOrMethods,
        mockPermitApplyPayload.permit.measurementDevicesOrMethods[0].id,
      ),
    ).toEqual(mockPermitApplyPayload.permit.measurementDevicesOrMethods[0]);
  });
});
