import { SourceStreamTypePipe } from './source-streams-type.pipe';

describe('SourceStreamTypePipe', () => {
  const pipe = new SourceStreamTypePipe();

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should transform the types', () => {
    expect(pipe.transform('CARBON_BLACK_MASS_BALANCE_METHODOLOGY')).toEqual('Carbon black: Mass balance methodology');
    expect(pipe.transform('LIME_DOLOMITE_MAGNESITE_ALKALI_EARTH_OXIDE_METHOD_B')).toEqual(
      'Lime / dolomite / magnesite: Alkali earth oxide (Method B)',
    );
    expect(pipe.transform('METAL_ORE_CARBONATE_INPUT')).toEqual('Metal ore: Carbonate input');
    expect(pipe.transform('PRIMARY_ALUMINIUM_MASS_BALANCE_METHODOLOGY')).toEqual(
      'Primary aluminium: Mass balance methodology',
    );
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
