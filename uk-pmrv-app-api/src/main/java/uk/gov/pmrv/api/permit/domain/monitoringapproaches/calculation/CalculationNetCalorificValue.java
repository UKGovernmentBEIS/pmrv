package uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculation;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.common.domain.dto.validation.SpELExpression;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.CalculationAnalysisMethodData;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.HighestRequiredTier;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@SpELExpression(expression = "{#exist == (#tier != null)}",
        message = "permit.calculationApproach.tier.netCalorificValue.exist.tier")
@SpELExpression(expression = "{(#tier eq 'TIER_1' || #tier eq 'TIER_2A' || #tier eq 'TIER_2B' || #tier eq 'NO_TIER') == (#isHighestRequiredTier != null)}",
        message = "permit.calculationApproach.tier.netCalorificValue.isHighestRequiredTier")
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#defaultValueApplied)  == (#standardReferenceSource != null)}",
    message = "permit.calculationApproach.tier.common.defaultValueApplied.standardReferenceSourceType")
public class CalculationNetCalorificValue {

    private boolean exist;

    private CalculationNetCalorificValueTier tier;

    @Valid
    @JsonUnwrapped
    private HighestRequiredTier highestRequiredTier;
    
    private Boolean defaultValueApplied;

    @Valid
    private CalculationNetCalorificValueStandardReferenceSource standardReferenceSource;

    @Valid
    @JsonUnwrapped
    private CalculationAnalysisMethodData calculationAnalysisMethodData;
}
