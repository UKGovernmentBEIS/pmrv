import { CapitalizeFirstPipe } from './capitalize-first.pipe';

describe('CapitalizeFirstPipe', () => {
  it('create an instance', () => {
    const pipe = new CapitalizeFirstPipe();
    expect(pipe).toBeTruthy();
  });

  it('should capitalize only the first letter', () => {
    const pipe = new CapitalizeFirstPipe();
    expect(pipe.transform('CAPITALIZE First')).toBe('Capitalize first');
  });
});
