import { RegulatedActivityTypePipe } from './regulated-activity-type.pipe';

describe('RegulatedActivityTypePipe', () => {
  const pipe = new RegulatedActivityTypePipe();

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should transform the types', () => {
    expect(pipe.transform('ADIPIC_ACID_PRODUCTION')).toEqual('Adipic acid production');
    expect(pipe.transform('AMMONIA_PRODUCTION')).toEqual('Ammonia production');
    expect(pipe.transform('BULK_ORGANIC_CHEMICAL_PRODUCTION')).toEqual('Bulk organic chemical production');
    expect(pipe.transform('CAPTURE_OF_GREENHOUSE_GASES_UNDER_DIRECTIVE')).toEqual(
      'Capture of greenhouse gases under Directive 2009/31/EC',
    );
    expect(pipe.transform('CARBON_BLACK_PRODUCTION')).toEqual('Carbon black production');
    expect(pipe.transform('CEMENT_CLINKER_PRODUCTION')).toEqual('Cement clinker production');
    expect(pipe.transform('CERAMICS_MANUFACTURING')).toEqual('Ceramics manufacturing');
    expect(pipe.transform('COKE_PRODUCTION')).toEqual('Coke production');
    expect(pipe.transform('COMBUSTION')).toEqual('Combustion');
    expect(pipe.transform('FERROUS_METALS_PRODUCTION')).toEqual('Ferrous metals production or processing');
    expect(pipe.transform('GLASS_MANUFACTURING')).toEqual('Glass manufacturing');
    expect(pipe.transform('GLYOXAL_GLYOXYLIC_ACID_PRODUCTION')).toEqual('Glyoxal and glyoxylic acid production');
    expect(pipe.transform('GYPSUM_OR_PLASTERBOARD_PRODUCTION')).toEqual(
      'Gypsum or plasterboard production or processing',
    );
    expect(pipe.transform('HYDROGEN_AND_SYNTHESIS_GAS_PRODUCTION')).toEqual('Hydrogen and synthesis gas production');
    expect(pipe.transform('LIME_OR_CALCINATION_OF_DOLOMITE_OR_MAGNESITE')).toEqual(
      'Lime or calcination of dolomite or magnesite',
    );
    expect(pipe.transform('MINERAL_OIL_REFINING')).toEqual('Mineral oil refining');
    expect(pipe.transform('MINERAL_WOOL_MANUFACTURING')).toEqual('Mineral wool manufacturing');
    expect(pipe.transform('NITRIC_ACID_PRODUCTION')).toEqual('Nitric acid production');
    expect(pipe.transform('NON_FERROUS_METALS_PRODUCTION')).toEqual('Non-ferrous metals production or processing');
    expect(pipe.transform('ORE_ROASTING_OR_SINTERING')).toEqual('Metal ore roasting or sintering');
    expect(pipe.transform('PAPER_OR_CARDBOARD_PRODUCTION')).toEqual('Paper or cardboard production');
    expect(pipe.transform('PIG_IRON_STEEL_PRODUCTION')).toEqual('Pig iron or steel production');
    expect(pipe.transform('PRIMARY_ALUMINIUM_PRODUCTION')).toEqual('Primary aluminium production');
    expect(pipe.transform('PULP_PRODUCTION')).toEqual('Pulp production');
    expect(pipe.transform('SECONDARY_ALUMINIUM_PRODUCTION')).toEqual('Secondary aluminium production');
    expect(pipe.transform('SODA_ASH_AND_SODIUM_BICARBONATE_PRODUCTION')).toEqual(
      'Soda ash (Na2CO3) and sodium bicarbonate (NaHCO3) production',
    );
    expect(pipe.transform('STORAGE_OF_GREENHOUSE_GASES_UNDER_DIRECTIVE')).toEqual(
      'Storage of greenhouse gases under Directive 2009/31/EC',
    );
    expect(pipe.transform('TRANSPORT_OF_GREENHOUSE_GASES_UNDER_DIRECTIVE')).toEqual(
      'Transport of greenhouse gases under Directive 2009/31/EC',
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
