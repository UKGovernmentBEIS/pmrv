import { SecondsToMinutesPipe } from './seconds-to-minutes.pipe';

describe('SecondsToMinutesPipe', () => {
  const pipe = new SecondsToMinutesPipe();

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should return 1 minute', () => {
    expect(pipe.transform(65)).toEqual('1 minute');
  });

  it('should return less than a minute', () => {
    expect(pipe.transform(55)).toEqual('less than a minute');
  });

  it('should return 2 minutes', () => {
    expect(pipe.transform(120)).toEqual('2 minutes');
  });

  it('should do something', () => {
    expect(pipe.transform(undefined)).toEqual('');
  });
});
