package uk.gov.pmrv.api.permit.domain.monitoringapproaches.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.common.domain.dto.validation.SpELExpression;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#isCostUnreasonable) || T(java.lang.Boolean).TRUE.equals(#isTechnicallyInfeasible)}", 
    message = "permit.monitoringapproach.common.noHighestRequiredTierJustification.at_least_one_is_required")
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#isTechnicallyInfeasible) == (#technicalInfeasibilityExplanation != null)}", 
    message = "permit.monitoringapproach.common.noHighestRequiredTierJustification.isTechnicallyInfeasible")
public class NoHighestRequiredTierJustification {
    
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean isCostUnreasonable;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean isTechnicallyInfeasible;

    @Size(max = 10000)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String technicalInfeasibilityExplanation;

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Set<UUID> files = new HashSet<>();
}