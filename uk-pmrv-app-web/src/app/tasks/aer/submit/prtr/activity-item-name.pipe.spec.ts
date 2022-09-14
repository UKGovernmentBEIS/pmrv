import { ActivityItemNamePipe } from '@tasks/aer/submit/prtr/activity-item-name.pipe';

describe('ActivityItemNamePipe', () => {
  let pipe: ActivityItemNamePipe;

  beforeEach(async () => {
    pipe = new ActivityItemNamePipe();
  });

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should transform value', () => {
    expect(pipe.transform('_1_A_1_A_PUBLIC_ELECTRICITY_AND_HEAT_PRODUCTION')).toEqual(
      '1.A.1.a Public Electricity and Heat Production',
    );
    expect(pipe.transform('_1_A_1_B_PETROLEUM_REFINING')).toEqual('1.A.1.b Petroleum refining');
    expect(pipe.transform('_1_A_1_C_MANUFACTURE_OF_SOLID_FUELS_AND_OTHER_ENERGY_INDUSTRIES')).toEqual(
      '1.A.1.c Manufacture of Solid Fuels and Other Energy Industries',
    );

    expect(pipe.transform('_1_A_2_A_IRON_AND_STEEL')).toEqual('1.A.2.a Iron and Steel');
    expect(pipe.transform('_1_A_2_B_NON_FERROUS_METALS')).toEqual('1.A.2.b Non-ferrous Metals');
    expect(pipe.transform('_1_A_2_C_CHEMICALS')).toEqual('1.A.2.c Chemicals');
    expect(pipe.transform('_1_A_2_D_PULP_PAPER_AND_PRINT')).toEqual('1.A.2.d Pulp, Paper and Print');
    expect(pipe.transform('_1_A_2_E_FOOD_PROCESSING_BEVERAGES_AND_TOBACCO')).toEqual(
      '1.A.2.e Food Processing, Beverages and Tobacco',
    );
    expect(pipe.transform('_1_A_2_F_NON_METALLIC_MINERALS')).toEqual('1.A.2.f Non-metallic minerals');
    expect(pipe.transform('_1_A_2_GVII_MOBILE_COMBUSTION_IN_MANUFACTURING_INDUSTRIES_AND_CONSTRUCTION')).toEqual(
      '1.A.2.gvii Mobile combustion in manufacturing industries and construction',
    );
    expect(pipe.transform('_1_A_2_GVIII_STATIONARY_COMBUSTION_IN_MANUFACTURING_AND_CONSTRUCTION')).toEqual(
      '1.A.2.gviii Stationary combustion in manufacturing and construction: Other',
    );

    expect(pipe.transform('_1_A_3_AI_INTERNATIONAL_AVIATION')).toEqual('1.A.3.ai International Aviation');
    expect(pipe.transform('_1_A_3_AII_CIVIL_AVIATION')).toEqual('1.A.3.aii Civil Aviation');
    expect(pipe.transform('_1_A_3_B_ROAD_TRANSPORTATION')).toEqual('1.A.3.b Road Transportation');
    expect(pipe.transform('_1_A_3_C_RAILWAYS')).toEqual('1.A.3.c Railways');
    expect(pipe.transform('_1_A_3_DI_INTERNATIONAL_NAVIGATION')).toEqual('1.A.3.di International Navigation');
    expect(pipe.transform('_1_A_3_DII_NATIONAL_NAVIGATION')).toEqual('1.A.3.dii National Navigation');
    expect(pipe.transform('_1_A_3_E_OTHER')).toEqual('1.A.3.e Other');

    expect(pipe.transform('_1_A_4_A_COMMERCIAL_INSTITUTIONAL_COMBUSTION')).toEqual(
      '1.A.4.a Commercial / Institutional Combustion',
    );
    expect(pipe.transform('_1_A_4_B_RESIDENTIAL')).toEqual('1.A.4.b Residential');
    expect(pipe.transform('_1_A_4_C_AGRICULTURE_FORESTRY_FISHING')).toEqual('1.A.4.c Agriculture / Forestry / Fishing');

    expect(pipe.transform('_1_A_5_A_OTHER_STATIONARY_INCLUDING_MILITARY')).toEqual(
      '1.A.5.a Other, Stationary (including Military)',
    );
    expect(pipe.transform('_1_A_5_B_OTHER_MOBILE_INCLUDING_MILITARY')).toEqual(
      '1.A.5.b Other, Mobile (including military)',
    );

    expect(pipe.transform('_1_B_1_A_COAL_MINING_AND_HANDLING')).toEqual('1.B.1.a Coal Mining and Handling');
    expect(pipe.transform('_1_B_1_B_SOLID_FUEL_TRANSFORMATION')).toEqual('1.B.1.b Solid fuel transformation');
    expect(pipe.transform('_1_B_1_C_OTHER')).toEqual('1.B.1.c Other');

    expect(pipe.transform('_1_B_2_A_OIL')).toEqual('1.B.2.a Oil');
    expect(pipe.transform('_1_B_2_B_NATURAL_GAS')).toEqual('1.B.2.b Natural gas');
    expect(pipe.transform('_1_B_2_C_VENTING_AND_FLARING')).toEqual('1.B.2.c Venting and flaring');

    expect(pipe.transform('_2_A_1_CEMENT_PRODUCTION')).toEqual('2.A.1 Cement Production');
    expect(pipe.transform('_2_A_2_LIME_PRODUCTION')).toEqual('2.A.2 Lime Production');
    expect(pipe.transform('_2_A_3_GLASS_PRODUCTION')).toEqual('2.A.3 Glass Production');
    expect(pipe.transform('_2_A_4_OTHER_PROCESS_USES_OF_CARBONATES')).toEqual('2.A.4 Other Process uses of Carbonates');

    expect(pipe.transform('_2_B_1_AMMONIA_PRODUCTION')).toEqual('2.B.1 Ammonia Production');
    expect(pipe.transform('_2_B_2_NITRIC_ACID_PRODUCTION')).toEqual('2.B.2 Nitric Acid Production');
    expect(pipe.transform('_2_B_3_ADIPIC_ACID_PRODUCTION')).toEqual('2.B.3 Adipic Acid Production');
    expect(pipe.transform('_2_B_4_CAPROLACTAM_GLYOXAL_AND_GLYOXYLIC_ACID_PRODUCTION')).toEqual(
      '2.B.4 Caprolactam, Glyoxal and Glyoxylic Acid Production',
    );
    expect(pipe.transform('_2_B_5_CARBIDE_PRODUCTION')).toEqual('2.B.5 Carbide production');
    expect(pipe.transform('_2_B_6_TITANIUM_DIOXIDE_PRODUCTION')).toEqual('2.B.6 Titanium Dioxide Production');
    expect(pipe.transform('_2_B_7_SODA_ASH_PRODUCTION')).toEqual('2.B.7 Soda Ash Production');
    expect(pipe.transform('_2_B_8_PETROCHEMICAL_AND_CARBON_BLACK_PRODUCTION')).toEqual(
      '2.B.8 Petrochemical and Carbon Black Production',
    );
    expect(pipe.transform('_2_B_9_FLUOROCHEMICAL_PRODUCTION')).toEqual('2.B.9 Fluorochemical Production');
    expect(pipe.transform('_2_B_10_OTHER')).toEqual('2.B.10 Other');

    expect(pipe.transform('_2_C_1_IRON_AND_STEEL_PRODUCTION')).toEqual('2.C.1 Iron and Steel production');
    expect(pipe.transform('_2_C_2_FERROALLOYS_PRODUCTION')).toEqual('2.C.2 Ferroalloys Production');
    expect(pipe.transform('_2_C_3_ALUMINIUM_PRODUCTION')).toEqual('2.C.3 Aluminium Production');
    expect(pipe.transform('_2_C_4_MAGNESIUM_PRODUCTION')).toEqual('2.C.4 Magnesium Production');
    expect(pipe.transform('_2_C_5_LEAD_PRODUCTION')).toEqual('2.C.5 Lead Production');
    expect(pipe.transform('_2_C_6_ZINC_PRODUCTION')).toEqual('2.C.6 Zinc Production');
    expect(pipe.transform('_2_C_7_OTHER_METAL_PRODUCTION')).toEqual('2.C.7 Other Metal Production');

    expect(pipe.transform('_2_D_1_LUBRICANT_USE')).toEqual('2.D.1 Lubricant Use');
    expect(pipe.transform('_2_D_2_PARAFFIN_WAX_USE')).toEqual('2.D.2 Paraffin Wax Use');
    expect(pipe.transform('_2_D_3_OTHER')).toEqual('2.D.3 Other');

    expect(pipe.transform('_2_E_1_INTEGRATED_CIRCUIT_OR_SEMICONDUCTOR')).toEqual(
      '2.E.1 Integrated Circuit or Semiconductor',
    );
    expect(pipe.transform('_2_E_2_TFT_FLAT_PANEL_DISPLAY')).toEqual('2.E.2 TFT Flat Panel Display');
    expect(pipe.transform('_2_E_3_PHOTOVOLTAICS')).toEqual('2.E.3 Photovoltaics');
    expect(pipe.transform('_2_E_4_HEAT_TRANSFER_FLUID')).toEqual('2.E.4 Heat Transfer Fluid');
    expect(pipe.transform('_2_E_5_OTHER')).toEqual('2.E.5 Other');

    expect(pipe.transform('_2_F_1_REFRIGERATION_AND_AIR_CONDITIONING_EQUIPMENT')).toEqual(
      '2.F.1 Refrigeration and Air Conditioning Equipment',
    );
    expect(pipe.transform('_2_F_2_FOAM_BLOWING_AGENTS')).toEqual('2.F.2 Foam Blowing Agents');
    expect(pipe.transform('_2_F_3_FIRE_EXTINGUISHERS')).toEqual('2.F.3 Fire Extinguishers');
    expect(pipe.transform('_2_F_4_AEROSOLS')).toEqual('2.F.4 Aerosols');
    expect(pipe.transform('_2_F_5_SOLVENTS')).toEqual('2.F.5 Solvents');
    expect(pipe.transform('_2_F_6_OTHER')).toEqual('2.F.6 Other');

    expect(pipe.transform('_2_G_1_ELECTRICAL_EQUIPMENT')).toEqual('2.G.1 Electrical Equipment');
    expect(pipe.transform('_2_G_2_SF6_AND_PFCS_FROM_OTHER_PRODUCT_USE')).toEqual(
      '2.G.2 SF6 and PFCs from Other Product Use',
    );
    expect(pipe.transform('_2_G_3_N2O_FROM_PRODUCT_USES')).toEqual('2.G.3 N2O from Product Uses');
    expect(pipe.transform('_2_G_4_OTHER')).toEqual('2.G.4 Other');

    expect(pipe.transform('_2_H_OTHER')).toEqual('2.H Other');

    expect(pipe.transform(undefined)).toEqual('');
  });
});
