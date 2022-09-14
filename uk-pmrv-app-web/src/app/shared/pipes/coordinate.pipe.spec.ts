import { CoordinatePipe } from './coordinate.pipe';

describe('CoordinatePipe', () => {
  const pipe = new CoordinatePipe();

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should transform coordinates', () => {
    const transformation = pipe.transform({
      cardinalDirection: 'EAST',
      degree: 1,
      minute: 1,
      second: 1,
    });

    expect(transformation).toEqual(`1° 1' 1" east`);
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
