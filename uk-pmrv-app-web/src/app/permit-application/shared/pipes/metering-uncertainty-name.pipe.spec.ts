import { MeteringUncertaintyNamePipe } from './metering-uncertainty-name.pipe';

describe('MeteringUncertaintyNamePipe', () => {
  let pipe: MeteringUncertaintyNamePipe;

  beforeEach(async () => {
    pipe = new MeteringUncertaintyNamePipe();
  });

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should transform value', () => {
    expect(pipe.transform('LESS_OR_EQUAL_1_5')).toEqual('1.5% or less');
    expect(pipe.transform('LESS_OR_EQUAL_2_5')).toEqual('2.5% or less');
    expect(pipe.transform('LESS_OR_EQUAL_5_0')).toEqual('5% or less');
    expect(pipe.transform('LESS_OR_EQUAL_7_5')).toEqual('7.5% or less');
    expect(pipe.transform('LESS_OR_EQUAL_10_0')).toEqual('10% or less');
    expect(pipe.transform('LESS_OR_EQUAL_12_5')).toEqual('12.5% or less');
    expect(pipe.transform('LESS_OR_EQUAL_15_0')).toEqual('15% or less');
    expect(pipe.transform('LESS_OR_EQUAL_17_5')).toEqual('17.5% or less');
    expect(pipe.transform('N_A')).toEqual('N/A');
    expect(pipe.transform(undefined)).toEqual('');
  });
});
