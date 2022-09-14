import { SubtaskNamePipe } from './subtask-name.pipe';

describe('SubtaskNamePipe', () => {
  let pipe: SubtaskNamePipe;

  beforeEach(async () => {
    pipe = new SubtaskNamePipe();
  });

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should transform value', () => {
    expect(pipe.transform('CALCULATION_Calorific')).toEqual('Net calorific value');
    expect(pipe.transform('CALCULATION_Emission_Factor')).toEqual('Emission factor');
    expect(pipe.transform('CALCULATION_Oxidation_Factor')).toEqual('Oxidation factor');
    expect(pipe.transform('CALCULATION_Carbon_Content')).toEqual('Carbon content');
    expect(pipe.transform('CALCULATION_Conversion_Factor')).toEqual('Conversion factor');
    expect(pipe.transform('CALCULATION_Biomass_Fraction')).toEqual('Biomass fraction');
  });
});
