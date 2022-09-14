import { Pipe, PipeTransform } from '@angular/core';

@Pipe({ name: 'activityItemName' })
export class ActivityItemNamePipe implements PipeTransform {
  transform(activityItem?: string): string {
    switch (activityItem) {
      case '_1_A_1_A_PUBLIC_ELECTRICITY_AND_HEAT_PRODUCTION':
        return '1.A.1.a Public Electricity and Heat Production';
      case '_1_A_1_B_PETROLEUM_REFINING':
        return '1.A.1.b Petroleum refining';
      case '_1_A_1_C_MANUFACTURE_OF_SOLID_FUELS_AND_OTHER_ENERGY_INDUSTRIES':
        return '1.A.1.c Manufacture of Solid Fuels and Other Energy Industries';

      case '_1_A_2_A_IRON_AND_STEEL':
        return '1.A.2.a Iron and Steel';
      case '_1_A_2_B_NON_FERROUS_METALS':
        return '1.A.2.b Non-ferrous Metals';
      case '_1_A_2_C_CHEMICALS':
        return '1.A.2.c Chemicals';
      case '_1_A_2_D_PULP_PAPER_AND_PRINT':
        return '1.A.2.d Pulp, Paper and Print';
      case '_1_A_2_E_FOOD_PROCESSING_BEVERAGES_AND_TOBACCO':
        return '1.A.2.e Food Processing, Beverages and Tobacco';
      case '_1_A_2_F_NON_METALLIC_MINERALS':
        return '1.A.2.f Non-metallic minerals';
      case '_1_A_2_GVII_MOBILE_COMBUSTION_IN_MANUFACTURING_INDUSTRIES_AND_CONSTRUCTION':
        return '1.A.2.gvii Mobile combustion in manufacturing industries and construction';
      case '_1_A_2_GVIII_STATIONARY_COMBUSTION_IN_MANUFACTURING_AND_CONSTRUCTION':
        return '1.A.2.gviii Stationary combustion in manufacturing and construction: Other';

      case '_1_A_3_AI_INTERNATIONAL_AVIATION':
        return '1.A.3.ai International Aviation';
      case '_1_A_3_AII_CIVIL_AVIATION':
        return '1.A.3.aii Civil Aviation';
      case '_1_A_3_B_ROAD_TRANSPORTATION':
        return '1.A.3.b Road Transportation';
      case '_1_A_3_C_RAILWAYS':
        return '1.A.3.c Railways';
      case '_1_A_3_DI_INTERNATIONAL_NAVIGATION':
        return '1.A.3.di International Navigation';
      case '_1_A_3_DII_NATIONAL_NAVIGATION':
        return '1.A.3.dii National Navigation';
      case '_1_A_3_E_OTHER':
        return '1.A.3.e Other';

      case '_1_A_4_A_COMMERCIAL_INSTITUTIONAL_COMBUSTION':
        return '1.A.4.a Commercial / Institutional Combustion';
      case '_1_A_4_B_RESIDENTIAL':
        return '1.A.4.b Residential';
      case '_1_A_4_C_AGRICULTURE_FORESTRY_FISHING':
        return '1.A.4.c Agriculture / Forestry / Fishing';

      case '_1_A_5_A_OTHER_STATIONARY_INCLUDING_MILITARY':
        return '1.A.5.a Other, Stationary (including Military)';
      case '_1_A_5_B_OTHER_MOBILE_INCLUDING_MILITARY':
        return '1.A.5.b Other, Mobile (including military)';

      case '_1_B_1_A_COAL_MINING_AND_HANDLING':
        return '1.B.1.a Coal Mining and Handling';
      case '_1_B_1_B_SOLID_FUEL_TRANSFORMATION':
        return '1.B.1.b Solid fuel transformation';
      case '_1_B_1_C_OTHER':
        return '1.B.1.c Other';

      case '_1_B_2_A_OIL':
        return '1.B.2.a Oil';
      case '_1_B_2_B_NATURAL_GAS':
        return '1.B.2.b Natural gas';
      case '_1_B_2_C_VENTING_AND_FLARING':
        return '1.B.2.c Venting and flaring';

      case '_2_A_1_CEMENT_PRODUCTION':
        return '2.A.1 Cement Production';
      case '_2_A_2_LIME_PRODUCTION':
        return '2.A.2 Lime Production';
      case '_2_A_3_GLASS_PRODUCTION':
        return '2.A.3 Glass Production';
      case '_2_A_4_OTHER_PROCESS_USES_OF_CARBONATES':
        return '2.A.4 Other Process uses of Carbonates';

      case '_2_B_1_AMMONIA_PRODUCTION':
        return '2.B.1 Ammonia Production';
      case '_2_B_2_NITRIC_ACID_PRODUCTION':
        return '2.B.2 Nitric Acid Production';
      case '_2_B_3_ADIPIC_ACID_PRODUCTION':
        return '2.B.3 Adipic Acid Production';
      case '_2_B_4_CAPROLACTAM_GLYOXAL_AND_GLYOXYLIC_ACID_PRODUCTION':
        return '2.B.4 Caprolactam, Glyoxal and Glyoxylic Acid Production';
      case '_2_B_5_CARBIDE_PRODUCTION':
        return '2.B.5 Carbide production';
      case '_2_B_6_TITANIUM_DIOXIDE_PRODUCTION':
        return '2.B.6 Titanium Dioxide Production';
      case '_2_B_7_SODA_ASH_PRODUCTION':
        return '2.B.7 Soda Ash Production';
      case '_2_B_8_PETROCHEMICAL_AND_CARBON_BLACK_PRODUCTION':
        return '2.B.8 Petrochemical and Carbon Black Production';
      case '_2_B_9_FLUOROCHEMICAL_PRODUCTION':
        return '2.B.9 Fluorochemical Production';
      case '_2_B_10_OTHER':
        return '2.B.10 Other';

      case '_2_C_1_IRON_AND_STEEL_PRODUCTION':
        return '2.C.1 Iron and Steel production';
      case '_2_C_2_FERROALLOYS_PRODUCTION':
        return '2.C.2 Ferroalloys Production';
      case '_2_C_3_ALUMINIUM_PRODUCTION':
        return '2.C.3 Aluminium Production';
      case '_2_C_4_MAGNESIUM_PRODUCTION':
        return '2.C.4 Magnesium Production';
      case '_2_C_5_LEAD_PRODUCTION':
        return '2.C.5 Lead Production';
      case '_2_C_6_ZINC_PRODUCTION':
        return '2.C.6 Zinc Production';
      case '_2_C_7_OTHER_METAL_PRODUCTION':
        return '2.C.7 Other Metal Production';

      case '_2_D_1_LUBRICANT_USE':
        return '2.D.1 Lubricant Use';
      case '_2_D_2_PARAFFIN_WAX_USE':
        return '2.D.2 Paraffin Wax Use';
      case '_2_D_3_OTHER':
        return '2.D.3 Other';

      case '_2_E_1_INTEGRATED_CIRCUIT_OR_SEMICONDUCTOR':
        return '2.E.1 Integrated Circuit or Semiconductor';
      case '_2_E_2_TFT_FLAT_PANEL_DISPLAY':
        return '2.E.2 TFT Flat Panel Display';
      case '_2_E_3_PHOTOVOLTAICS':
        return '2.E.3 Photovoltaics';
      case '_2_E_4_HEAT_TRANSFER_FLUID':
        return '2.E.4 Heat Transfer Fluid';
      case '_2_E_5_OTHER':
        return '2.E.5 Other';

      case '_2_F_1_REFRIGERATION_AND_AIR_CONDITIONING_EQUIPMENT':
        return '2.F.1 Refrigeration and Air Conditioning Equipment';
      case '_2_F_2_FOAM_BLOWING_AGENTS':
        return '2.F.2 Foam Blowing Agents';
      case '_2_F_3_FIRE_EXTINGUISHERS':
        return '2.F.3 Fire Extinguishers';
      case '_2_F_4_AEROSOLS':
        return '2.F.4 Aerosols';
      case '_2_F_5_SOLVENTS':
        return '2.F.5 Solvents';
      case '_2_F_6_OTHER':
        return '2.F.6 Other';

      case '_2_G_1_ELECTRICAL_EQUIPMENT':
        return '2.G.1 Electrical Equipment';
      case '_2_G_2_SF6_AND_PFCS_FROM_OTHER_PRODUCT_USE':
        return '2.G.2 SF6 and PFCs from Other Product Use';
      case '_2_G_3_N2O_FROM_PRODUCT_USES':
        return '2.G.3 N2O from Product Uses';
      case '_2_G_4_OTHER':
        return '2.G.4 Other';

      case '_2_H_OTHER':
        return '2.H Other';
      default:
        return '';
    }
  }
}
