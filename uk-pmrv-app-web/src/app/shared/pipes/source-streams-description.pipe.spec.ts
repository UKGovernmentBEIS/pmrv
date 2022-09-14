import { SourceStreamDescriptionPipe } from './source-streams-description.pipe';

describe('SourceStreamDescriptionPipe', () => {
  const pipe = new SourceStreamDescriptionPipe();

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should transform the stream description', () => {
    expect(pipe.transform('ACETYLENE')).toEqual('Acetylene');
    expect(pipe.transform('ANTHRACITE')).toEqual('Anthracite');
    expect(pipe.transform('BIODIESELS')).toEqual('Biodiesels');
    expect(pipe.transform('BIOGASOLINE')).toEqual('Biogasoline');
    expect(pipe.transform('BIOMASS')).toEqual('Biomass');
    expect(pipe.transform('BITUMEN')).toEqual('Bitumen');
    expect(pipe.transform('BLAST_FURNACE_GAS')).toEqual('Blast Furnace Gas');
    expect(pipe.transform('BLENDED_FUEL_GAS')).toEqual('Blended Fuel Gas');
    expect(pipe.transform('CARBON_MONOXIDE')).toEqual('Carbon Monoxide');
    expect(pipe.transform('CHARCOAL')).toEqual('Charcoal');
    expect(pipe.transform('COAL')).toEqual('Coal');
    expect(pipe.transform('COAL_TAR')).toEqual('Coal Tar');
    expect(pipe.transform('COKE')).toEqual('Coke');
    expect(pipe.transform('COKE_OVEN_COKE_LIGNITE_COKE')).toEqual('Coke Oven Coke & Lignite Coke');
    expect(pipe.transform('COKE_OVEN_GAS')).toEqual('Coke Oven Gas');
    expect(pipe.transform('COKING_COAL')).toEqual('Coking Coal');
    expect(pipe.transform('COLLIERY_METHANE')).toEqual('Colliery Methane');
    expect(pipe.transform('CRUDE_OIL')).toEqual('Crude Oil');
    expect(pipe.transform('ETHANE')).toEqual('Ethane');
    expect(pipe.transform('FUEL_GAS')).toEqual('Fuel Gas');
    expect(pipe.transform('FUEL_OIL')).toEqual('Fuel Oil');
    expect(pipe.transform('GAS_COKE')).toEqual('Gas Coke');
    expect(pipe.transform('GAS_DIESEL_OIL')).toEqual('Gas/Diesel Oil');
    expect(pipe.transform('GAS_OIL')).toEqual('Gas/Oil');
    expect(pipe.transform('GAS_WORKS')).toEqual('Gas Works');
    expect(pipe.transform('HIGH_PRESSURE_FLARE_GAS')).toEqual('High Pressure Flare Gas');
    expect(pipe.transform('IMPORT_FUEL_GAS')).toEqual('Import Fuel Gas');
    expect(pipe.transform('INDUSTRIAL_WASTES')).toEqual('Industrial Wastes');
    expect(pipe.transform('KEROSENE_OTHER_THAN_JET_KEROSENE')).toEqual('Kerosene (other than jet kerosene)');
    expect(pipe.transform('LANDFILL_GAS')).toEqual('Landfill Gas');
    expect(pipe.transform('LIGNITE')).toEqual('Lignite');
    expect(pipe.transform('LIQUEFIED_PETROLEUM_GASES')).toEqual('Liquefied Petroleum Gases');
    expect(pipe.transform('LOW_LOW_PRESSURE_FLARE_GAS')).toEqual('Low Low Pressure Flare Gas');
    expect(pipe.transform('LOW_PRESSURE_FLARE_GAS')).toEqual('Low Pressure Flare Gas');
    expect(pipe.transform('LUBRICANTS')).toEqual('Lubricants');
    expect(pipe.transform('MATERIAL')).toEqual('Material');
    expect(pipe.transform('MEDIUM_PRESSURE_FLARE_GAS')).toEqual('Medium Pressure Flare Gas');
    expect(pipe.transform('METHANE')).toEqual('Methane');
    expect(pipe.transform('MOTOR_GASOLINE')).toEqual('Motor Gasoline');
    expect(pipe.transform('MSW')).toEqual('MSW');
    expect(pipe.transform('NAPHTHA')).toEqual('Naphtha');
    expect(pipe.transform('NATURAL_GAS')).toEqual('Natural Gas');
    expect(pipe.transform('NATURAL_GAS_LIQUIDS')).toEqual('Natural Gas Liquids');
    expect(pipe.transform('NON_BIOMASS_PACKAGING_WASTE')).toEqual('Non-biomass Packaging Waste');
    expect(pipe.transform('OIL_SHALE_AND_TAR_SANDS')).toEqual('Oil Shale and Tar Sands');
    expect(pipe.transform('OPG')).toEqual('OPG');
    expect(pipe.transform('ORIMULSION')).toEqual('Orimulsion');
    expect(pipe.transform('OTHER')).toEqual('Other');
    expect(pipe.transform('OTHER_BIOGAS')).toEqual('Other Biogas');
    expect(pipe.transform('OTHER_BITUMINOUS_COAL')).toEqual('Other Bituminous Coal');
    expect(pipe.transform('OTHER_LIQUID_BIOFUELS')).toEqual('Other Liquid Biofuels');
    expect(pipe.transform('OTHER_PETROLEUM_PRODUCTS')).toEqual('Other Petroleum Products');
    expect(pipe.transform('OTHER_PRIMARY_SOLID_BIOMASS')).toEqual('Other Primary Solid Biomass');
    expect(pipe.transform('OXYGEN_STEEL_FURNACE_GAS')).toEqual('Oxygen Steel Furnace Gas');
    expect(pipe.transform('PARAFFIN_WAXES')).toEqual('Paraffin Waxes');
    expect(pipe.transform('PATENT_FUEL')).toEqual('Patent Fuel');
    expect(pipe.transform('PEAT')).toEqual('Peat');
    expect(pipe.transform('PETROL')).toEqual('Petrol');
    expect(pipe.transform('PETROLEUM_COKE')).toEqual('Petroleum Coke');
    expect(pipe.transform('PILOT_AND_PURGE_FLARE_GAS')).toEqual('Pilot and Purge Flare Gas');
    expect(pipe.transform('PILOT_FLARE_GAS')).toEqual('Pilot Flare Gas');
    expect(pipe.transform('PROPANE')).toEqual('Propane');
    expect(pipe.transform('REFINERY_FEEDSTOCKS')).toEqual('Refinery Feedstocks');
    expect(pipe.transform('REFINERY_GAS')).toEqual('Refinery Gas');
    expect(pipe.transform('RESIDUAL_FUEL_OIL')).toEqual('Residual Fuel Oil');
    expect(pipe.transform('SCRAP_TYRES')).toEqual('Scrap Tyres');
    expect(pipe.transform('SHALE_OIL')).toEqual('Shale Oil');
    expect(pipe.transform('SLUDGE_GAS')).toEqual('Sludge Gas');
    expect(pipe.transform('SOUR_GAS')).toEqual('Sour Gas');
    expect(pipe.transform('SOUR_GAS_FLARE')).toEqual('Sour Gas Flare');
    expect(pipe.transform('SSF')).toEqual('SSF');
    expect(pipe.transform('SUB_BITUMINOUS_COAL')).toEqual('Sub-Bituminous Coal');
    expect(pipe.transform('WASTE_OILS')).toEqual('Waste Oils');
    expect(pipe.transform('WASTE_SOLVENTS')).toEqual('Waste Solvents');
    expect(pipe.transform('WASTE_TYRES')).toEqual('Waste Tyres');
    expect(pipe.transform('WHITE_SPIRIT_SBP')).toEqual('White Spirit & SBP');
    expect(pipe.transform('WOOD_WOOD_WASTE')).toEqual('Wood/Wood Waste');
    expect(pipe.transform(null)).toEqual('');
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
