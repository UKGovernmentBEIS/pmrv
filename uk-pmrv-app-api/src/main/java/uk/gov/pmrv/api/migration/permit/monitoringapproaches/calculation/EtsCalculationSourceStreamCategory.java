package uk.gov.pmrv.api.migration.permit.monitoringapproaches.calculation;

import java.util.Set;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class EtsCalculationSourceStreamCategory {

    private final String etsAccountId;
    private final String tierId;
    
    // source stream
    private final String sourceStream;
    private final Set<String> emissionSources;
    private final String estimatedEmission;
    private final String calculationMethod;
    private final String sourceStreamCategory;

    // activity data
    private final Set<String> measurementDevices;
    private final String meteringUncertainty;
    private final String activityDataTierApplied;
    private final boolean activityDataIsMaxTier;
    private final boolean activityDataIsHighestTier;
    private final String tierJustification;
    
    // ncv
    private final String netCalorificValueTierApplied;
    private final boolean netCalorificValueIsMaxTier;
    private final boolean netCalorificValueIsHighestTier;
    
    // ef
    private final String emissionFactorTierApplied;
    private final boolean emissionFactorIsMaxTier;
    private final boolean emissionFactorIsHighestTierApplied;
    
    // oxf
    private final String oxidationFactorTierApplied;
    private final boolean oxidationFactorIsMaxTier;
    private final boolean oxidationFactorIsHighestTierApplied;
    
    // cc
    private final String carbonContentTierApplied;
    private final boolean carbonContentIsMaxTier;
    private final boolean carbonContentIsHighestTierApplied;
    
    // cf
    private final String conversionFactorTierApplied;
    private final boolean conversionFactorIsMaxTier;
    private final boolean conversionFactorIsHighestTierApplied;
    
    // bf
    private final String biomassFractionTierApplied;
    private final boolean biomassFractionIsMaxTier;
    private final boolean biomassFractionIsHighestTierApplied;
}
