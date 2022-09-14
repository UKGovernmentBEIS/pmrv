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
    message = "permit.calculationApproach.tier.conversionFactor.exist.tier")
@SpELExpression(expression = "{(#tier eq 'TIER_1' || #tier eq 'NO_TIER') == (#isHighestRequiredTier != null)}",
    message = "permit.calculationApproach.tier.conversionFactor.tier_and_isHighestRequiredTier")
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#defaultValueApplied) == (#standardReferenceSource != null)}",
    message = "permit.calculationApproach.tier.common.defaultValueApplied.standardReferenceSourceType")
public class CalculationConversionFactor {

    private boolean exist;

    private CalculationConversionFactorTier tier;

    @Valid
    @JsonUnwrapped
    private HighestRequiredTier highestRequiredTier;

    private Boolean defaultValueApplied;
    
    @Valid
    private CalculationConversionFactorStandardReferenceSource standardReferenceSource;

    @Valid
    @JsonUnwrapped
    private CalculationAnalysisMethodData calculationAnalysisMethodData;
}
