import { RegulatedActivity } from 'pmrv-api';

export type RegulatedActivitiesFormGroup = {
  COMBUSTION_GROUP: Extract<RegulatedActivity['type'], 'COMBUSTION'>[];
  REFINING_GROUP: Extract<RegulatedActivity['type'], 'MINERAL_OIL_REFINING'>[];
  METAL_GROUP: Extract<
    RegulatedActivity['type'],
    | 'COKE_PRODUCTION'
    | 'ORE_ROASTING_OR_SINTERING'
    | 'PIG_IRON_STEEL_PRODUCTION'
    | 'FERROUS_METALS_PRODUCTION'
    | 'NON_FERROUS_METALS_PRODUCTION'
    | 'PRIMARY_ALUMINIUM_PRODUCTION'
    | 'SECONDARY_ALUMINIUM_PRODUCTION'
  >[];
  MINERAL_GROUP: Extract<
    RegulatedActivity['type'],
    | 'CEMENT_CLINKER_PRODUCTION'
    | 'LIME_OR_CALCINATION_OF_DOLOMITE_OR_MAGNESITE'
    | 'CERAMICS_MANUFACTURING'
    | 'GYPSUM_OR_PLASTERBOARD_PRODUCTION'
  >[];
  GLASS_GROUP: Extract<RegulatedActivity['type'], 'GLASS_MANUFACTURING' | 'MINERAL_WOOL_MANUFACTURING'>[];
  PULP_GROUP: Extract<RegulatedActivity['type'], 'PULP_PRODUCTION' | 'PAPER_OR_CARDBOARD_PRODUCTION'>[];
  CHEMICAL_GROUP: Extract<
    RegulatedActivity['type'],
    | 'CARBON_BLACK_PRODUCTION'
    | 'BULK_ORGANIC_CHEMICAL_PRODUCTION'
    | 'GLYOXAL_GLYOXYLIC_ACID_PRODUCTION'
    | 'NITRIC_ACID_PRODUCTION'
    | 'ADIPIC_ACID_PRODUCTION'
    | 'AMMONIA_PRODUCTION'
    | 'SODA_ASH_AND_SODIUM_BICARBONATE_PRODUCTION'
    | 'HYDROGEN_AND_SYNTHESIS_GAS_PRODUCTION'
  >[];
  CARBON_GROUP: Extract<
    RegulatedActivity['type'],
    | 'CAPTURE_OF_GREENHOUSE_GASES_UNDER_DIRECTIVE'
    | 'TRANSPORT_OF_GREENHOUSE_GASES_UNDER_DIRECTIVE'
    | 'STORAGE_OF_GREENHOUSE_GASES_UNDER_DIRECTIVE'
  >[];
};

export const formGroupOptions: {
  [K in keyof RegulatedActivitiesFormGroup]: RegulatedActivity['type'][];
} = {
  COMBUSTION_GROUP: ['COMBUSTION'],
  REFINING_GROUP: ['MINERAL_OIL_REFINING'],
  METAL_GROUP: [
    'COKE_PRODUCTION',
    'ORE_ROASTING_OR_SINTERING',
    'PIG_IRON_STEEL_PRODUCTION',
    'FERROUS_METALS_PRODUCTION',
    'NON_FERROUS_METALS_PRODUCTION',
    'PRIMARY_ALUMINIUM_PRODUCTION',
    'SECONDARY_ALUMINIUM_PRODUCTION',
  ],
  MINERAL_GROUP: [
    'CEMENT_CLINKER_PRODUCTION',
    'LIME_OR_CALCINATION_OF_DOLOMITE_OR_MAGNESITE',
    'CERAMICS_MANUFACTURING',
    'GYPSUM_OR_PLASTERBOARD_PRODUCTION',
  ],
  GLASS_GROUP: ['GLASS_MANUFACTURING', 'MINERAL_WOOL_MANUFACTURING'],
  PULP_GROUP: ['PULP_PRODUCTION', 'PAPER_OR_CARDBOARD_PRODUCTION'],
  CHEMICAL_GROUP: [
    'CARBON_BLACK_PRODUCTION',
    'BULK_ORGANIC_CHEMICAL_PRODUCTION',
    'GLYOXAL_GLYOXYLIC_ACID_PRODUCTION',
    'NITRIC_ACID_PRODUCTION',
    'ADIPIC_ACID_PRODUCTION',
    'AMMONIA_PRODUCTION',
    'SODA_ASH_AND_SODIUM_BICARBONATE_PRODUCTION',
    'HYDROGEN_AND_SYNTHESIS_GAS_PRODUCTION',
  ],
  CARBON_GROUP: [
    'CAPTURE_OF_GREENHOUSE_GASES_UNDER_DIRECTIVE',
    'TRANSPORT_OF_GREENHOUSE_GASES_UNDER_DIRECTIVE',
    'STORAGE_OF_GREENHOUSE_GASES_UNDER_DIRECTIVE',
  ],
};

export const activityGroupMap: Record<keyof RegulatedActivitiesFormGroup, string> = {
  COMBUSTION_GROUP: 'Combustion',
  REFINING_GROUP: 'Refining',
  METAL_GROUP: 'Metals',
  MINERAL_GROUP: 'Minerals',
  GLASS_GROUP: 'Glass and mineral wool',
  PULP_GROUP: 'Pulp and paper',
  CHEMICAL_GROUP: 'Chemicals',
  CARBON_GROUP: 'Carbon capture and storage',
};

export const unitOptions: RegulatedActivity['capacityUnit'][] = [
  'MW_TH',
  'KW_TH',
  'MVA',
  'KVA',
  'KW',
  'MW',
  'TONNES_PER_DAY',
  'TONNES_PER_HOUR',
  'TONNES_PER_ANNUM',
  'KG_PER_DAY',
  'KG_PER_HOUR',
];

export const activityHintMap: Partial<Record<RegulatedActivity['type'], string>> = {
  COMBUSTION:
    'At installations with a total rated thermal input exceeding 20MW. Installations excluded are those for the incineration of municipal or hazardous waste.',
  ORE_ROASTING_OR_SINTERING: 'Including sulphide ore, and pelletisation of the ores',
  PIG_IRON_STEEL_PRODUCTION:
    'Including continuous casting. Primary or secondary fusion, at a capacity exceeding 2,5 tonnes per hour.',
  FERROUS_METALS_PRODUCTION:
    'Including ferroalloys. At installations with a total rated thermal input exceeding 20MW. Processing includes rolling mills, re-heaters, annealing furnaces, smitheries, foundries, coating and pickling.',
  NON_FERROUS_METALS_PRODUCTION:
    'Including production of alloys, refining and foundry casting. At installations with a total rated thermal input exceeding 20MW, and including any fuels used as reducing agents.',
  SECONDARY_ALUMINIUM_PRODUCTION: 'At installations with a total rated thermal input exceeding 20MW',
  CEMENT_CLINKER_PRODUCTION:
    'In rotary kilns with a production capacity exceeding 500 tonnes per day, or in other furnaces with a production capacity exceeding 50 tonnes per day',
  LIME_OR_CALCINATION_OF_DOLOMITE_OR_MAGNESITE:
    'In rotary kilns or other furnaces with a production capacity exceeding 50 tonnes per day',
  CERAMICS_MANUFACTURING:
    'With a production capacity exceeding 75 tonnes per day of products manufactured by firing, including roofing tiles, bricks, refractory bricks, tiles, stoneware and porcelain',
  GYPSUM_OR_PLASTERBOARD_PRODUCTION:
    'Including drying or calcination of gypsum and other gypsum products. At installations with a total rated thermal input exceeding 20MW.',
  GLASS_MANUFACTURING: 'Including glass fibre. At installations with a melting capacity exceeding 20 tonnes per day.',
  MINERAL_WOOL_MANUFACTURING:
    'At installations manufacturing insulation material using glass, rock or slag with a melting capacity exceeding 20 tonnes per day',
  PULP_PRODUCTION: 'Pulp from timber or other fibrous materials',
  PAPER_OR_CARDBOARD_PRODUCTION: 'At installations with a production capacity exceeding 20 tonnes per day',
  CARBON_BLACK_PRODUCTION:
    'Involving the carbonisation of organic substances such as oils, tars, cracker and distillation residues. At installations with a total rated thermal input exceeding 20MW.',
  BULK_ORGANIC_CHEMICAL_PRODUCTION:
    'By cracking, reforming, partial or full oxidation or similar processes. At installations with a production capacity exceeding 100 tonnes per day.',
  HYDROGEN_AND_SYNTHESIS_GAS_PRODUCTION:
    'By reforming or partial oxidation. At installations with a production capacity exceeding 25 tonnes per day.',
  CAPTURE_OF_GREENHOUSE_GASES_UNDER_DIRECTIVE:
    'Capture is for transportation and geological storage at a permitted site',
};
