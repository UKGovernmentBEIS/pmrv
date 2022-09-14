package uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculation;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.common.domain.dto.validation.SpELExpression;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.HighestRequiredTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.MeteringUncertainty;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@SpELExpression(expression = "{(#tier eq 'NO_TIER' or #tier eq 'TIER_1' or #tier eq 'TIER_2' or #tier eq 'TIER_3') == (#isHighestRequiredTier != null)}",
    message = "permit.calculationApproach.tier.activityData.isHighestRequiredTier")
public class CalculationActivityData {
    
    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @NotEmpty
    @JsonDeserialize(as = LinkedHashSet.class)
    private Set<String> measurementDevicesOrMethods = new LinkedHashSet<>();
    
    @NotNull
    private MeteringUncertainty uncertainty;
    
    @NotNull
    private CalculationActivityDataTier tier;

    @Valid
    @JsonUnwrapped
    private HighestRequiredTier highestRequiredTier;
}
