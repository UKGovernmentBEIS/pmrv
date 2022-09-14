import { CapacityUnitPipe } from './capacity-unit.pipe';

describe('CapacityUnitPipe', () => {
  const pipe = new CapacityUnitPipe();

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should transform capacity unit', () => {
    expect(pipe.transform('MW_TH')).toEqual('MW(th)');
    expect(pipe.transform('KW_TH')).toEqual('kW(th)');
    expect(pipe.transform('MVA')).toEqual('MVA');
    expect(pipe.transform('KVA')).toEqual('kVA');
    expect(pipe.transform('KW')).toEqual('kW');
    expect(pipe.transform('MW')).toEqual('MW');
    expect(pipe.transform('TONNES_PER_DAY')).toEqual('tonnes/day');
    expect(pipe.transform('TONNES_PER_HOUR')).toEqual('tonnes/hour');
    expect(pipe.transform('TONNES_PER_ANNUM')).toEqual('tonnes/annum');
    expect(pipe.transform('KG_PER_DAY')).toEqual('kg/day');
    expect(pipe.transform('KG_PER_HOUR')).toEqual('kg/hour');
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
