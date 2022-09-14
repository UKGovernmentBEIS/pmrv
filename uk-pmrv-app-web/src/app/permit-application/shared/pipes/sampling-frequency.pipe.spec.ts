import { SamplingFrequencyPipe } from './sampling-frequency.pipe';

describe('SamplingFrequencyPipe', () => {
  const pipe = new SamplingFrequencyPipe();

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should transform the sampling frequency literal', () => {
    expect(pipe.transform('CONTINUOUS')).toEqual('Continuous');
    expect(pipe.transform('DAILY')).toEqual('Daily');
    expect(pipe.transform('WEEKLY')).toEqual('Weekly');
    expect(pipe.transform('MONTHLY')).toEqual('Monthly');
    expect(pipe.transform('QUARTERLY')).toEqual('Quarterly');
    expect(pipe.transform('BI_ANNUALLY')).toEqual('Biannual');
    expect(pipe.transform('ANNUALLY')).toEqual('Annual');
  });
});
