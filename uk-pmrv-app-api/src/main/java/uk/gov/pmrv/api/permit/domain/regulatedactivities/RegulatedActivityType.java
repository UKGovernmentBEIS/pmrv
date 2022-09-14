package uk.gov.pmrv.api.permit.domain.regulatedactivities;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RegulatedActivityType {
    
    //combustion
    COMBUSTION(
            RegulatedActivitySection.COMBUSTION,
            "Combustion", 
            GreenHouseGasesCategory.CARBON_DIOXIDE),
    
    //refining
    MINERAL_OIL_REFINING(
            RegulatedActivitySection.REFINING, 
            "Mineral oil refining", 
            GreenHouseGasesCategory.CARBON_DIOXIDE),
    
    //metals
    COKE_PRODUCTION(
            RegulatedActivitySection.METALS, 
            "Coke production", 
            GreenHouseGasesCategory.CARBON_DIOXIDE),
    ORE_ROASTING_OR_SINTERING(
            RegulatedActivitySection.METALS, 
            "Metal ore roasting or sintering", 
            GreenHouseGasesCategory.CARBON_DIOXIDE),
    PIG_IRON_STEEL_PRODUCTION(
            RegulatedActivitySection.METALS, 
            "Pig iron or steel production", 
            GreenHouseGasesCategory.CARBON_DIOXIDE),
    FERROUS_METALS_PRODUCTION(
            RegulatedActivitySection.METALS, 
            "Ferrous metals production or processing", 
            GreenHouseGasesCategory.CARBON_DIOXIDE),
    NON_FERROUS_METALS_PRODUCTION(
            RegulatedActivitySection.METALS, 
            "Non-ferrous metals production or processing", 
            GreenHouseGasesCategory.CARBON_DIOXIDE),
    PRIMARY_ALUMINIUM_PRODUCTION(
            RegulatedActivitySection.METALS, 
            "Primary aluminium production", 
            GreenHouseGasesCategory.CARBON_DIOXIDE),
    SECONDARY_ALUMINIUM_PRODUCTION(
            RegulatedActivitySection.METALS, 
            "Secondary aluminium production", 
            GreenHouseGasesCategory.CARBON_DIOXIDE_AND_PERFUOROCARBONS),
    
    //minerals
    CEMENT_CLINKER_PRODUCTION(
            RegulatedActivitySection.MINERALS, 
            "Cement clinker production", 
            GreenHouseGasesCategory.CARBON_DIOXIDE),
    LIME_OR_CALCINATION_OF_DOLOMITE_OR_MAGNESITE(
            RegulatedActivitySection.MINERALS, 
            "Lime or calcination of dolomite or magnesite", 
            GreenHouseGasesCategory.CARBON_DIOXIDE),
    CERAMICS_MANUFACTURING(
            RegulatedActivitySection.MINERALS, 
            "Ceramics manufacturing", 
            GreenHouseGasesCategory.CARBON_DIOXIDE),
    GYPSUM_OR_PLASTERBOARD_PRODUCTION(
            RegulatedActivitySection.MINERALS, 
            "Gypsum or plasterboard production or processing", 
            GreenHouseGasesCategory.CARBON_DIOXIDE),
    
    //glass and mineral wool
    GLASS_MANUFACTURING(
            RegulatedActivitySection.GLASS_AND_MINERAL_WOOL, 
            "Glass manufacturing", 
            GreenHouseGasesCategory.CARBON_DIOXIDE),
    MINERAL_WOOL_MANUFACTURING(
            RegulatedActivitySection.GLASS_AND_MINERAL_WOOL, 
            "Mineral wool manufacturing", 
            GreenHouseGasesCategory.CARBON_DIOXIDE),
    
    //pulp and paper
    PULP_PRODUCTION(
            RegulatedActivitySection.PULP_AND_PAPER, 
            "Pulp production", 
            GreenHouseGasesCategory.CARBON_DIOXIDE),
    PAPER_OR_CARDBOARD_PRODUCTION(
            RegulatedActivitySection.PULP_AND_PAPER, 
            "Paper or cardboard production", 
            GreenHouseGasesCategory.CARBON_DIOXIDE),
    
    //chemicals
    CARBON_BLACK_PRODUCTION(
            RegulatedActivitySection.CHEMICALS, 
            "Carbon black production", 
            GreenHouseGasesCategory.CARBON_DIOXIDE),
    BULK_ORGANIC_CHEMICAL_PRODUCTION(
            RegulatedActivitySection.CHEMICALS, 
            "Bulk organic chemical production", 
            GreenHouseGasesCategory.CARBON_DIOXIDE),
    GLYOXAL_GLYOXYLIC_ACID_PRODUCTION(
            RegulatedActivitySection.CHEMICALS, 
            "Glyoxal and glyoxylic acid production", 
            GreenHouseGasesCategory.CARBON_DIOXIDE_AND_NITROUS_OXIDE),
    NITRIC_ACID_PRODUCTION(
            RegulatedActivitySection.CHEMICALS, 
            "Nitric acid production", 
            GreenHouseGasesCategory.CARBON_DIOXIDE_AND_NITROUS_OXIDE),
    ADIPIC_ACID_PRODUCTION(
            RegulatedActivitySection.CHEMICALS, 
            "Adipic acid production", 
            GreenHouseGasesCategory.CARBON_DIOXIDE_AND_NITROUS_OXIDE),
    AMMONIA_PRODUCTION(
            RegulatedActivitySection.CHEMICALS, 
            "Ammonia production", 
            GreenHouseGasesCategory.CARBON_DIOXIDE),
    SODA_ASH_AND_SODIUM_BICARBONATE_PRODUCTION(
            RegulatedActivitySection.CHEMICALS, 
            "Soda ash (Na2CO3) and sodium bicarbonate (NaHCO3) production", 
            GreenHouseGasesCategory.CARBON_DIOXIDE),
    HYDROGEN_AND_SYNTHESIS_GAS_PRODUCTION(
            RegulatedActivitySection.CHEMICALS, 
            "Hydrogen and synthesis gas production", 
            GreenHouseGasesCategory.CARBON_DIOXIDE),
    
    //carbon capture and storage
    CAPTURE_OF_GREENHOUSE_GASES_UNDER_DIRECTIVE(
            RegulatedActivitySection.CARBON_CAPTURE_AND_STORAGE, 
            "Capture of greenhouse gases under Directive 2009/31/EC", 
            GreenHouseGasesCategory.CARBON_DIOXIDE),
    TRANSPORT_OF_GREENHOUSE_GASES_UNDER_DIRECTIVE(
            RegulatedActivitySection.CARBON_CAPTURE_AND_STORAGE, 
            "Transport of greenhouse gases under Directive 2009/31/EC", 
            GreenHouseGasesCategory.CARBON_DIOXIDE),
    STORAGE_OF_GREENHOUSE_GASES_UNDER_DIRECTIVE(
            RegulatedActivitySection.CARBON_CAPTURE_AND_STORAGE, 
            "Storage of greenhouse gases under Directive 2009/31/EC", 
            GreenHouseGasesCategory.CARBON_DIOXIDE);
    
    private RegulatedActivitySection section;
    private String description;
    private GreenHouseGasesCategory greenHouseCategory;
}
