import { ApproachDescriptionPipe } from './approach-description.pipe';

describe('MonitoringApproachPipe', () => {
  let pipe: ApproachDescriptionPipe;

  beforeEach(async () => {
    pipe = new ApproachDescriptionPipe();
  });

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should transform value', () => {
    expect(pipe.transform('CALCULATION')).toEqual('Calculation');
    expect(pipe.transform('MEASUREMENT')).toEqual('Measurement');
    expect(pipe.transform('FALLBACK')).toEqual('Fall-back');
    expect(pipe.transform('N2O')).toEqual('Nitrous oxide (N2O)');
    expect(pipe.transform('PFC')).toEqual('Perfluorocarbons (PFC)');
    expect(pipe.transform('INHERENT_CO2')).toEqual('Inherent CO2');
    expect(pipe.transform('TRANSFERRED_CO2')).toEqual('Transferred CO2');
  });
});
