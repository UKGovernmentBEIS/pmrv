import { AppliedTierPipe } from './applied-tier.pipe';

describe('AppliedTierPipe', () => {
  const pipe = new AppliedTierPipe();
  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should transform the tier literal', () => {
    expect(pipe.transform('NO_TIER')).toEqual('No tier');
    expect(pipe.transform('TIER_1')).toEqual('Tier 1');
    expect(pipe.transform('TIER_2')).toEqual('Tier 2');
    expect(pipe.transform('TIER_3')).toEqual('Tier 3');
    expect(pipe.transform('TIER_4')).toEqual('Tier 4');
  });
});
