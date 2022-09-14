package uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculation;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.common.domain.dto.validation.SpELExpression;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.CalculationAnalysisMethodData;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.HighestRequiredTier;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SpELExpression(expression = "{#exist == (#tier != null)}", 
    message = "permit.calculationApproach.tier.oxidationFactor.exist.tier")
@SpELExpression(expression = "{(#tier eq 'TIER_1' || #tier eq 'TIER_2' || #tier eq 'NO_TIER') == (#isHighestRequiredTier != null)}",
    message = "permit.calculationApproach.tier.oxidationFactor.tier_and_highestRequiredTier")
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#defaultValueApplied)  == (#standardReferenceSource != null)}",
    message = "permit.calculationApproach.tier.common.defaultValueApplied.standardReferenceSourceType")
public class CalculationOxidationFactor {

    private boolean exist;
    
    private CalculationOxidationFactorTier tier;
    
    @Valid
    @JsonUnwrapped
    private HighestRequiredTier highestRequiredTier;
    
    private Boolean defaultValueApplied;

    @Valid
    private CalculationOxidationFactorStandardReferenceSource standardReferenceSource;
    
    @Valid
    @JsonUnwrapped
    private CalculationAnalysisMethodData calculationAnalysisMethodData;
}
