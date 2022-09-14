import { MeasurementDevicesTypePipe } from './measurement-devices-type.pipe';

describe('MeasurementDevicesTypePipe', () => {
  const pipe = new MeasurementDevicesTypePipe();
  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should transform the stream description', () => {
    expect(pipe.transform('BALANCE')).toEqual('Balance');
    expect(pipe.transform('BELLOWS_METER')).toEqual('Bellows meter');
    expect(pipe.transform('OTHER')).toEqual('Other');
    expect(pipe.transform('TANK_DIP')).toEqual('Tank dip');
  });

  it('should handle empty value', () => {
    const transformation = pipe.transform(null);

    expect(transformation).toEqual('');
  });

  it('should handle undefined value', () => {
    const transformation = pipe.transform(undefined);

    expect(transformation).toEqual('');
  });
});
