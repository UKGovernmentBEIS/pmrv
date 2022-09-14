package uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculation;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
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
@SpELExpression(expression = "{#exist == (#tier != null)}", message = "permit.calculationApproach.tier.carbonContent.exist.tier")
@SpELExpression(expression = "{(#tier eq 'TIER_3') == (#oneThirdRule != null)}",
    message = "permit.calculationApproach.tier.carbonContent.tier_3.oneThirdRule")
@SpELExpression(expression = "{(#tier eq 'TIER_2B' || #tier eq 'TIER_2A' || #tier eq 'TIER_1' || #tier eq 'NO_TIER') == (#isHighestRequiredTier != null)}",
    message = "permit.calculationApproach.tier.carbonContent.tier_and_isHighestRequiredTier")
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#defaultValueApplied) == (#standardReferenceSource != null)}",
    message = "permit.calculationApproach.tier.common.defaultValueApplied.standardReferenceSourceType")
public class CalculationCarbonContent {

    private boolean exist;

    private CalculationCarbonContentTier tier;

    @Valid
    @JsonUnwrapped
    private HighestRequiredTier highestRequiredTier;

    private Boolean oneThirdRule;

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Set<UUID> oneThirdRuleFiles = new HashSet<>();

    private Boolean defaultValueApplied;

    @Valid
    private CalculationCarbonContentStandardReferenceSource standardReferenceSource;

    @Valid
    @JsonUnwrapped
    private CalculationAnalysisMethodData calculationAnalysisMethodData;
}
