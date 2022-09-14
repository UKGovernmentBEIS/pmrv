export type activitySectionType = '_1' | '_2';
export const activitiesSection: Array<activitySectionType> = ['_1', '_2'];
export type activityFinalStepItemType =
  | '_1_A_1_A'
  | '_1_A_1_B'
  | '_1_A_1_C'
  | '_1_A_2_A'
  | '_1_A_2_B'
  | '_1_A_2_C'
  | '_1_A_2_D'
  | '_1_A_2_E'
  | '_1_A_2_F'
  | '_1_A_2_GVIII'
  | '_1_A_2_GVII'
  | '_1_A_3_AII'
  | '_1_A_3_AI'
  | '_1_A_3_B'
  | '_1_A_3_C'
  | '_1_A_3_DII'
  | '_1_A_3_DI'
  | '_1_A_3_E'
  | '_1_A_4_A'
  | '_1_A_4_B'
  | '_1_A_4_C'
  | '_1_A_5_A'
  | '_1_A_5_B'
  | '_1_B_1_A'
  | '_1_B_1_B'
  | '_1_B_1_C'
  | '_1_B_2_A'
  | '_1_B_2_B'
  | '_1_B_2_C'
  | '_2_A_1'
  | '_2_A_2'
  | '_2_A_3'
  | '_2_A_4'
  | '_2_B_10'
  | '_2_B_1'
  | '_2_B_2'
  | '_2_B_3'
  | '_2_B_4'
  | '_2_B_5'
  | '_2_B_6'
  | '_2_B_7'
  | '_2_B_8'
  | '_2_B_9'
  | '_2_C_1'
  | '_2_C_2'
  | '_2_C_3'
  | '_2_C_4'
  | '_2_C_5'
  | '_2_C_6'
  | '_2_C_7'
  | '_2_D_1'
  | '_2_D_2'
  | '_2_D_3'
  | '_2_E_1'
  | '_2_E_2'
  | '_2_E_3'
  | '_2_E_4'
  | '_2_E_5'
  | '_2_F_1'
  | '_2_F_2'
  | '_2_F_3'
  | '_2_F_4'
  | '_2_F_5'
  | '_2_F_6'
  | '_2_G_1'
  | '_2_G_2'
  | '_2_G_3'
  | '_2_G_4'
  | '_2_H';
export type activityItemType =
  | activitySectionType
  | activityFinalStepItemType
  | '_1_A'
  | '_1_A_1'
  | '_1_A_2'
  | '_1_A_3'
  | '_1_A_4'
  | '_1_A_5'
  | '_1_B'
  | '_1_B_1'
  | '_1_B_2'
  | '_2_A'
  | '_2_B'
  | '_2_C'
  | '_2_D'
  | '_2_E'
  | '_2_F'
  | '_2_G';

export const activitiesChildSection: { [key: string]: activityItemType[] } = {
  _1: ['_1_A', '_1_B'],
  _2: ['_2_A', '_2_B', '_2_C', '_2_D', '_2_E', '_2_F', '_2_G', '_2_H'],
  _2_A: ['_2_A_1', '_2_A_2', '_2_A_3', '_2_A_4'],
  _2_B: ['_2_B_1', '_2_B_10', '_2_B_2', '_2_B_3', '_2_B_4', '_2_B_5', '_2_B_6', '_2_B_7', '_2_B_8', '_2_B_9'],
  _2_C: ['_2_C_1', '_2_C_2', '_2_C_3', '_2_C_4', '_2_C_5', '_2_C_6', '_2_C_7'],
  _2_D: ['_2_D_1', '_2_D_2', '_2_D_3'],
  _2_E: ['_2_E_1', '_2_E_2', '_2_E_3', '_2_E_4', '_2_E_5'],
  _2_F: ['_2_F_1', '_2_F_2', '_2_F_3', '_2_F_4', '_2_F_5', '_2_F_6'],
  _2_G: ['_2_G_1', '_2_G_2', '_2_G_3', '_2_G_4'],
};
export const stepWithSubActivities: activityItemType[] = ['_1_A', '_1_B'];
export const finalWizard: activityItemType[] = ['_1_A', '_1_B', '_2_A', '_2_B', '_2_C', '_2_D', '_2_E', '_2_F', '_2_G'];

export const activityItemNameMap: Record<activityItemType, string> = {
  _1: '1. Energy',
  _1_A: 'A. Fuel Combustion Activities',
  _1_A_1: '1. Energy Industries',
  _1_A_1_A: 'a. Public Electricity and Heat Production',
  _1_A_1_B: 'b. Petroleum refining',
  _1_A_1_C: 'c. Manufacture of Solid Fuels and Other Energy Industries',
  _1_A_2: '2. Manufacturing Industries and Construction',
  _1_A_2_A: 'a. Iron and Steel',
  _1_A_2_B: 'b. Non-ferrous Metals',
  _1_A_2_C: 'c. Chemicals',
  _1_A_2_D: 'd. Pulp, Paper and Print',
  _1_A_2_E: 'e. Food Processing, Beverages and Tobacco',
  _1_A_2_F: 'f. Non-metallic minerals',
  _1_A_2_GVII: 'gvii. Mobile combustion in manufacturing industries and construction',
  _1_A_2_GVIII: 'gviii. Stationary combustion in manufacturing and construction: Other',
  _1_A_3: '3. Transport',
  _1_A_3_AI: 'ai. International Aviation',
  _1_A_3_AII: 'aii. Civil Aviation',
  _1_A_3_B: 'b. Road Transportation',
  _1_A_3_C: 'c. Railways',
  _1_A_3_DI: 'di. International Navigation',
  _1_A_3_DII: 'dii. National Navigation',
  _1_A_3_E: 'e. Other',
  _1_A_4: '4. Other sectors',
  _1_A_4_A: 'a. Commercial / Institutional Combustion',
  _1_A_4_B: 'b. Residential',
  _1_A_4_C: 'c. Agriculture / Forestry / Fishing',
  _1_A_5: '5. Other (not elsewhere specified)',
  _1_A_5_A: 'a. Other, Stationary (including Military)',
  _1_A_5_B: 'b. Other, Mobile (including military)',
  _1_B: 'B. Fugitive Emissions from Fuels',
  _1_B_1: '1. Fugitive Emissions from Solid Fuels',
  _1_B_1_A: 'a. Coal Mining and Handling',
  _1_B_1_B: 'b. Solid fuel transformation',
  _1_B_1_C: 'c. Other',
  _1_B_2: '2. Oil and natural gas',
  _1_B_2_A: 'a. Oil',
  _1_B_2_B: 'b. Natural gas',
  _1_B_2_C: 'c. Venting and flaring',
  _2: '2. Industrial Processes',
  _2_A: 'A. Mineral Products',
  _2_A_1: '1. Cement Production',
  _2_A_2: '2. Lime Production',
  _2_A_3: '3. Glass Production',
  _2_A_4: '4. Other Process uses of Carbonates',
  _2_B: 'B. Chemical Industry',
  _2_B_1: '1. Ammonia Production',
  _2_B_2: '2. Nitric Acid Production',
  _2_B_3: '3. Adipic Acid Production',
  _2_B_4: '4. Caprolactam, Glyoxal and Glyoxylic Acid Production',
  _2_B_5: '5. Carbide production',
  _2_B_6: '6. Titanium Dioxide Production',
  _2_B_7: '7. Soda Ash Production',
  _2_B_8: '8. Petrochemical and Carbon Black Production',
  _2_B_9: '9. Fluorochemical Production',
  _2_B_10: '10. Other',
  _2_C: 'C. Metal Production',
  _2_C_1: '1. Iron and Steel production',
  _2_C_2: '2. Ferroalloys Production',
  _2_C_3: '3. Aluminium Production',
  _2_C_4: '4. Magnesium Production',
  _2_C_5: '5. Lead Production',
  _2_C_6: '6. Zinc Production',
  _2_C_7: '7. Other Metal Production',
  _2_D: 'D. Non-energy Products from Fuels and Solvent Use',
  _2_D_1: '1. Lubricant Use',
  _2_D_2: '2. Paraffin Wax Use',
  _2_D_3: '3. Other',
  _2_E: 'E. Electronics Industry',
  _2_E_1: '1. Integrated Circuit or Semiconductor',
  _2_E_2: '2. TFT Flat Panel Display',
  _2_E_3: '3. Photovoltaics',
  _2_E_4: '4. Heat Transfer Fluid',
  _2_E_5: '5. Other',
  _2_F: 'F. Product Uses as Substitutes for ODS',
  _2_F_1: '1. Refrigeration and Air Conditioning Equipment',
  _2_F_2: '2. Foam Blowing Agents',
  _2_F_3: '3. Fire Extinguishers',
  _2_F_4: '4. Aerosols',
  _2_F_5: '5. Solvents',
  _2_F_6: '6. Other',
  _2_G: 'G. Other Product Manufacture and Use',
  _2_G_1: '1. Electrical Equipment',
  _2_G_2: '2. SF6 and PFCs from Other Product Use',
  _2_G_3: '3. N2O from Product Uses',
  _2_G_4: '4. Other',
  _2_H: 'H. Other',
};

export const activityItemTypeMap: Record<activityFinalStepItemType, string> = {
  _1_A_1_A: '_1_A_1_A_PUBLIC_ELECTRICITY_AND_HEAT_PRODUCTION',
  _1_A_1_B: '_1_A_1_B_PETROLEUM_REFINING',
  _1_A_1_C: '_1_A_1_C_MANUFACTURE_OF_SOLID_FUELS_AND_OTHER_ENERGY_INDUSTRIES',
  _1_A_2_A: '_1_A_2_A_IRON_AND_STEEL',
  _1_A_2_B: '_1_A_2_B_NON_FERROUS_METALS',
  _1_A_2_C: '_1_A_2_C_CHEMICALS',
  _1_A_2_D: '_1_A_2_D_PULP_PAPER_AND_PRINT',
  _1_A_2_E: '_1_A_2_E_FOOD_PROCESSING_BEVERAGES_AND_TOBACCO',
  _1_A_2_F: '_1_A_2_F_NON_METALLIC_MINERALS',
  _1_A_2_GVIII: '_1_A_2_GVIII_STATIONARY_COMBUSTION_IN_MANUFACTURING_AND_CONSTRUCTION',
  _1_A_2_GVII: '_1_A_2_GVII_MOBILE_COMBUSTION_IN_MANUFACTURING_INDUSTRIES_AND_CONSTRUCTION',
  _1_A_3_AII: '_1_A_3_AII_CIVIL_AVIATION',
  _1_A_3_AI: '_1_A_3_AI_INTERNATIONAL_AVIATION',
  _1_A_3_B: '_1_A_3_B_ROAD_TRANSPORTATION',
  _1_A_3_C: '_1_A_3_C_RAILWAYS',
  _1_A_3_DII: '_1_A_3_DII_NATIONAL_NAVIGATION',
  _1_A_3_DI: '_1_A_3_DI_INTERNATIONAL_NAVIGATION',
  _1_A_3_E: '_1_A_3_E_OTHER',
  _1_A_4_A: '_1_A_4_A_COMMERCIAL_INSTITUTIONAL_COMBUSTION',
  _1_A_4_B: '_1_A_4_B_RESIDENTIAL',
  _1_A_4_C: '_1_A_4_C_AGRICULTURE_FORESTRY_FISHING',
  _1_A_5_A: '_1_A_5_A_OTHER_STATIONARY_INCLUDING_MILITARY',
  _1_A_5_B: '_1_A_5_B_OTHER_MOBILE_INCLUDING_MILITARY',
  _1_B_1_A: '_1_B_1_A_COAL_MINING_AND_HANDLING',
  _1_B_1_B: '_1_B_1_B_SOLID_FUEL_TRANSFORMATION',
  _1_B_1_C: '_1_B_1_C_OTHER',
  _1_B_2_A: '_1_B_2_A_OIL',
  _1_B_2_B: '_1_B_2_B_NATURAL_GAS',
  _1_B_2_C: '_1_B_2_C_VENTING_AND_FLARING',
  _2_A_1: '_2_A_1_CEMENT_PRODUCTION',
  _2_A_2: '_2_A_2_LIME_PRODUCTION',
  _2_A_3: '_2_A_3_GLASS_PRODUCTION',
  _2_A_4: '_2_A_4_OTHER_PROCESS_USES_OF_CARBONATES',
  _2_B_10: '_2_B_10_OTHER',
  _2_B_1: '_2_B_1_AMMONIA_PRODUCTION',
  _2_B_2: '_2_B_2_NITRIC_ACID_PRODUCTION',
  _2_B_3: '_2_B_3_ADIPIC_ACID_PRODUCTION',
  _2_B_4: '_2_B_4_CAPROLACTAM_GLYOXAL_AND_GLYOXYLIC_ACID_PRODUCTION',
  _2_B_5: '_2_B_5_CARBIDE_PRODUCTION',
  _2_B_6: '_2_B_6_TITANIUM_DIOXIDE_PRODUCTION',
  _2_B_7: '_2_B_7_SODA_ASH_PRODUCTION',
  _2_B_8: '_2_B_8_PETROCHEMICAL_AND_CARBON_BLACK_PRODUCTION',
  _2_B_9: '_2_B_9_FLUOROCHEMICAL_PRODUCTION',
  _2_C_1: '_2_C_1_IRON_AND_STEEL_PRODUCTION',
  _2_C_2: '_2_C_2_FERROALLOYS_PRODUCTION',
  _2_C_3: '_2_C_3_ALUMINIUM_PRODUCTION',
  _2_C_4: '_2_C_4_MAGNESIUM_PRODUCTION',
  _2_C_5: '_2_C_5_LEAD_PRODUCTION',
  _2_C_6: '_2_C_6_ZINC_PRODUCTION',
  _2_C_7: '_2_C_7_OTHER_METAL_PRODUCTION',
  _2_D_1: '_2_D_1_LUBRICANT_USE',
  _2_D_2: '_2_D_2_PARAFFIN_WAX_USE',
  _2_D_3: '_2_D_3_OTHER',
  _2_E_1: '_2_E_1_INTEGRATED_CIRCUIT_OR_SEMICONDUCTOR',
  _2_E_2: '_2_E_2_TFT_FLAT_PANEL_DISPLAY',
  _2_E_3: '_2_E_3_PHOTOVOLTAICS',
  _2_E_4: '_2_E_4_HEAT_TRANSFER_FLUID',
  _2_E_5: '_2_E_5_OTHER',
  _2_F_1: '_2_F_1_REFRIGERATION_AND_AIR_CONDITIONING_EQUIPMENT',
  _2_F_2: '_2_F_2_FOAM_BLOWING_AGENTS',
  _2_F_3: '_2_F_3_FIRE_EXTINGUISHERS',
  _2_F_4: '_2_F_4_AEROSOLS',
  _2_F_5: '_2_F_5_SOLVENTS',
  _2_F_6: '_2_F_6_OTHER',
  _2_G_1: '_2_G_1_ELECTRICAL_EQUIPMENT',
  _2_G_2: '_2_G_2_SF6_AND_PFCS_FROM_OTHER_PRODUCT_USE',
  _2_G_3: '_2_G_3_N2O_FROM_PRODUCT_USES',
  _2_G_4: '_2_G_4_OTHER',
  _2_H: '_2_H_OTHER',
};
