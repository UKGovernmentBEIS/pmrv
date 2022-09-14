package uk.gov.pmrv.api.migration.permit.regulatedactivities;

import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;
import org.springframework.util.NumberUtils;
import uk.gov.pmrv.api.permit.domain.regulatedactivities.CapacityUnit;
import uk.gov.pmrv.api.permit.domain.regulatedactivities.RegulatedActivityType;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Log4j2
@UtilityClass
public class RegulatedActivitiesMapper {
    
    private final Map<String, RegulatedActivityType> etsToPmrvMap = new HashMap<>();
    
    static {
        etsToPmrvMap.put("Combustion", RegulatedActivityType.COMBUSTION);
        etsToPmrvMap.put("Manufacture of ceramics", RegulatedActivityType.CERAMICS_MANUFACTURING);
        etsToPmrvMap.put("Manufacture of glass", RegulatedActivityType.GLASS_MANUFACTURING);
        etsToPmrvMap.put("Manufacture of mineral wool", RegulatedActivityType.MINERAL_WOOL_MANUFACTURING);
        etsToPmrvMap.put("Metal ore roasting or sintering", RegulatedActivityType.ORE_ROASTING_OR_SINTERING);
        etsToPmrvMap.put("Production of ammonia", RegulatedActivityType.AMMONIA_PRODUCTION);
        etsToPmrvMap.put("Production of bulk organic chemicals", RegulatedActivityType.BULK_ORGANIC_CHEMICAL_PRODUCTION);
        etsToPmrvMap.put("Production of cement clinker", RegulatedActivityType.CEMENT_CLINKER_PRODUCTION);
        etsToPmrvMap.put("Production of coke", RegulatedActivityType.COKE_PRODUCTION);
        etsToPmrvMap.put("Production of hydrogen and synthesis gas", RegulatedActivityType.HYDROGEN_AND_SYNTHESIS_GAS_PRODUCTION);
        etsToPmrvMap.put("Production of lime,  or calcination of dolomite/magnesite", RegulatedActivityType.LIME_OR_CALCINATION_OF_DOLOMITE_OR_MAGNESITE);
        etsToPmrvMap.put("Production of nitric acid", RegulatedActivityType.NITRIC_ACID_PRODUCTION);
        etsToPmrvMap.put("Production of paper or cardboard", RegulatedActivityType.PAPER_OR_CARDBOARD_PRODUCTION);
        etsToPmrvMap.put("Production of pig iron or steel", RegulatedActivityType.PIG_IRON_STEEL_PRODUCTION);
        etsToPmrvMap.put("Production of primary aluminium", RegulatedActivityType.PRIMARY_ALUMINIUM_PRODUCTION);
        etsToPmrvMap.put("Production of pulp", RegulatedActivityType.PULP_PRODUCTION);
        etsToPmrvMap.put("Production of secondary aluminium", RegulatedActivityType.SECONDARY_ALUMINIUM_PRODUCTION);
        etsToPmrvMap.put("Production of soda ash and sodium bicarbonate", RegulatedActivityType.SODA_ASH_AND_SODIUM_BICARBONATE_PRODUCTION);
        etsToPmrvMap.put("Production or processing of ferrous metals", RegulatedActivityType.FERROUS_METALS_PRODUCTION);
        etsToPmrvMap.put("Production or processing of gypsum or plasterboard", RegulatedActivityType.GYPSUM_OR_PLASTERBOARD_PRODUCTION);
        etsToPmrvMap.put("Refining of mineral oil", RegulatedActivityType.MINERAL_OIL_REFINING);
    }

    private final List<String> etsCapacityUnits = List.of("MWth", "MWthinput");

    public uk.gov.pmrv.api.permit.domain.regulatedactivities.RegulatedActivities toPmrvRegulatedActivities(
            List<RegulatedActivity> etsRegulatedActivities) {
        uk.gov.pmrv.api.permit.domain.regulatedactivities.RegulatedActivities regulatedActivities = 
                new uk.gov.pmrv.api.permit.domain.regulatedactivities.RegulatedActivities();
        
        for(RegulatedActivity etsAct : etsRegulatedActivities) {
            //resolve type
            RegulatedActivityType pmrvType = etsToPmrvMap.get(etsAct.getType());
            if(pmrvType == null) {
                log.error("PMRV type not found for ets type: " + etsAct.getType());
                continue;
            }
            
            uk.gov.pmrv.api.permit.domain.regulatedactivities.RegulatedActivity pmrvAct = 
                    new uk.gov.pmrv.api.permit.domain.regulatedactivities.RegulatedActivity();
            pmrvAct.setId(UUID.randomUUID().toString());
            pmrvAct.setType(pmrvType);
            pmrvAct.setCapacity(NumberUtils.parseNumber(etsAct.getQuantity(), BigDecimal.class));

            // Resolve capacity unit
            Arrays.stream(CapacityUnit.values())
                    .filter(c -> c.getDescription().equalsIgnoreCase(etsAct.getQuantityUnit().replace(" ","")))
                    .findFirst()
                    .ifPresentOrElse(pmrvAct::setCapacityUnit, () -> {
                        if(etsCapacityUnits.contains(etsAct.getQuantityUnit().replace(" ",""))){
                            pmrvAct.setCapacityUnit(CapacityUnit.MW_TH);
                        }
            });

            regulatedActivities.getRegulatedActivities().add(pmrvAct);
        }
        
        return regulatedActivities;
    }

    public static RegulatedActivityType resolvePmrvRegulatedActivityType(String etsRegulatedActivity) {
        return etsToPmrvMap.get(etsRegulatedActivity);
    }

}
