package uk.gov.pmrv.api.reporting.domain.pollutantregistercodes;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PollutantRegisterActivity {

    _1_A_1_A_PUBLIC_ELECTRICITY_AND_HEAT_PRODUCTION("1.A.1.a Public Electricity and Heat Production"),
    _1_A_1_B_PETROLEUM_REFINING("1.A.1.b Petroleum refining"),
    _1_A_1_C_MANUFACTURE_OF_SOLID_FUELS_AND_OTHER_ENERGY_INDUSTRIES("1.A.1.c Manufacture of Solid Fuels and Other Energy Industries"),

    _1_A_2_A_IRON_AND_STEEL("1.A.2.a Iron and Steel"),
    _1_A_2_B_NON_FERROUS_METALS("1.A.2.b Non-ferrous Metals"),
    _1_A_2_C_CHEMICALS("1.A.2.c Chemicals"),
    _1_A_2_D_PULP_PAPER_AND_PRINT("1.A.2.d Pulp, Paper and Print"),
    _1_A_2_E_FOOD_PROCESSING_BEVERAGES_AND_TOBACCO("1.A.2.e Food Processing, Beverages and Tobacco"),
    _1_A_2_F_NON_METALLIC_MINERALS("1.A.2.f Non-metallic minerals"),
    _1_A_2_GVII_MOBILE_COMBUSTION_IN_MANUFACTURING_INDUSTRIES_AND_CONSTRUCTION("1.A.2.gvii Mobile combustion in manufacturing industries and construction"),
    _1_A_2_GVIII_STATIONARY_COMBUSTION_IN_MANUFACTURING_AND_CONSTRUCTION("1.A.2.gviii Stationary combustion in manufacturing and construction: Other"),

    _1_A_3_AI_INTERNATIONAL_AVIATION("1.A.3.ai International Aviation"),
    _1_A_3_AII_CIVIL_AVIATION("1.A.3.aii Civil Aviation"),
    _1_A_3_B_ROAD_TRANSPORTATION("1.A.3.b Road Transportation"),
    _1_A_3_C_RAILWAYS("1.A.3.c Railways"),
    _1_A_3_DI_INTERNATIONAL_NAVIGATION("1.A.3.di International Navigation"),
    _1_A_3_DII_NATIONAL_NAVIGATION("1.A.3.dii National Navigation"),
    _1_A_3_E_OTHER("1.A.3.e Other"),

    _1_A_4_A_COMMERCIAL_INSTITUTIONAL_COMBUSTION("1.A.4.a Commercial / Institutional Combustion"),
    _1_A_4_B_RESIDENTIAL("1.A.4.b Residential"),
    _1_A_4_C_AGRICULTURE_FORESTRY_FISHING("1.A.4.c Agriculture / Forestry / Fishing"),

    _1_A_5_A_OTHER_STATIONARY_INCLUDING_MILITARY("1.A.5.a Other, Stationary (including Military)"),
    _1_A_5_B_OTHER_MOBILE_INCLUDING_MILITARY("1.A.5.a Other, Mobile (including Military)"),

    _1_B_1_A_COAL_MINING_AND_HANDLING("1.B.1.a Coal Mining and Handling"),
    _1_B_1_B_SOLID_FUEL_TRANSFORMATION("1.B.1.b Solid fuel transformation"),
    _1_B_1_C_OTHER("1.B.1.c Other"),

    _1_B_2_A_OIL("1.B.2.a Oil"),
    _1_B_2_B_NATURAL_GAS("1.B.2.b Natural gas"),
    _1_B_2_C_VENTING_AND_FLARING("1.B.2.c Venting and flaring"),

    _2_A_1_CEMENT_PRODUCTION("2.A.1 Cement Production"),
    _2_A_2_LIME_PRODUCTION("2.A.2 Lime Production"),
    _2_A_3_GLASS_PRODUCTION("2.A.3 Glass Production"),
    _2_A_4_OTHER_PROCESS_USES_OF_CARBONATES("2.A.4 Other Process uses of Carbonates"),

    _2_B_1_AMMONIA_PRODUCTION("2.B.1 Ammonia Production"),
    _2_B_2_NITRIC_ACID_PRODUCTION("2.B.2 Nitric Acid Production"),
    _2_B_3_ADIPIC_ACID_PRODUCTION("2.B.3 Adipic Acid Production"),
    _2_B_4_CAPROLACTAM_GLYOXAL_AND_GLYOXYLIC_ACID_PRODUCTION("2.B.4 Caprolactam, Glyoxal and Glyoxylic Acid Production"),
    _2_B_5_CARBIDE_PRODUCTION("2.B.5 Carbide production"),
    _2_B_6_TITANIUM_DIOXIDE_PRODUCTION("2.B.6 Titanium Dioxide Production"),
    _2_B_7_SODA_ASH_PRODUCTION("2.B.7 Soda Ash Production"),
    _2_B_8_PETROCHEMICAL_AND_CARBON_BLACK_PRODUCTION("2.B.8 Petrochemical and Carbon Black Production"),
    _2_B_9_FLUOROCHEMICAL_PRODUCTION("2.B.9 Fluorochemical Production"),
    _2_B_10_OTHER("2.B.10 Other"),

    _2_C_1_IRON_AND_STEEL_PRODUCTION("2.C.1 Iron and Steel production"),
    _2_C_2_FERROALLOYS_PRODUCTION("2.C.2 Ferroalloys Production"),
    _2_C_3_ALUMINIUM_PRODUCTION("2.C.3 Aluminium Production"),
    _2_C_4_MAGNESIUM_PRODUCTION("2.C.4 Magnesium Production"),
    _2_C_5_LEAD_PRODUCTION("2.C.5 Lead Production"),
    _2_C_6_ZINC_PRODUCTION("2.C.6 Zinc Production"),
    _2_C_7_OTHER_METAL_PRODUCTION("2.C.7 Other Metal Production"),

    _2_D_1_LUBRICANT_USE("2.D.1 Lubricant Use"),
    _2_D_2_PARAFFIN_WAX_USE("2.D.2 Paraffin Wax Use"),
    _2_D_3_OTHER("2.D.3 Other"),

    _2_E_1_INTEGRATED_CIRCUIT_OR_SEMICONDUCTOR("2.E.1 Integrated Circuit or Semiconductor"),
    _2_E_2_TFT_FLAT_PANEL_DISPLAY("2.E.2 TFT Flat Panel Display"),
    _2_E_3_PHOTOVOLTAICS("2.E.3 Photovoltaics"),
    _2_E_4_HEAT_TRANSFER_FLUID("2.E.4 Heat Transfer Fluid"),
    _2_E_5_OTHER("2.E.5 Other"),

    _2_F_1_REFRIGERATION_AND_AIR_CONDITIONING_EQUIPMENT("2.F.1 Refrigeration and Air Conditioning Equipment"),
    _2_F_2_FOAM_BLOWING_AGENTS("2.F.2 Foam Blowing Agents"),
    _2_F_3_FIRE_EXTINGUISHERS("2.F.3 Fire Extinguishers"),
    _2_F_4_AEROSOLS("2.F.4 Aerosols"),
    _2_F_5_SOLVENTS("2.F.5 Solvents"),
    _2_F_6_OTHER("2.F.6 Other"),

    _2_G_1_ELECTRICAL_EQUIPMENT("2.G.1 Electrical Equipment"),
    _2_G_2_SF6_AND_PFCS_FROM_OTHER_PRODUCT_USE("2.G.2 SF6 and PFCs from Other Product Use"),
    _2_G_3_N2O_FROM_PRODUCT_USES("2.G.3 N2O from Product Uses"),
    _2_G_4_OTHER("2.G.4 Other"),

    _2_H_OTHER("2.H Other"),
    ;

    private final String description;


}
