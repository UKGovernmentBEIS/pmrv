import { DaysRemainingPipe } from './days-remaining.pipe';

describe('DaysRemainingPipe', () => {
  const pipe = new DaysRemainingPipe();

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should return empty if null value', () => {
    const transformation = pipe.transform(null);

    expect(transformation).toEqual('');
  });

  it('should return empty if undefined value', () => {
    const transformation = pipe.transform(undefined);

    expect(transformation).toEqual('');
  });

  it('should return 0 value', () => {
    const transformation = pipe.transform(0);

    expect(transformation).toEqual('Overdue');
  });

  it('should return value', () => {
    const transformation = pipe.transform(13);

    expect(transformation).toEqual('13');
  });

  it('should return Overdue if negative value', () => {
    const transformation = pipe.transform(-13);

    expect(transformation).toEqual('Overdue');
  });
});
