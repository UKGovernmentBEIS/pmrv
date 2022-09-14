package uk.gov.pmrv.api.permit.domain.monitoringapproaches.common;

import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.common.domain.dto.validation.SpELExpression;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SpELExpression(expression = "{T(java.lang.Boolean).FALSE.equals(#isHighestRequiredTier) == (#noHighestRequiredTierJustification != null)}", 
    message = "permit.monitoringapproach.common.highestRequiredTier")
public class HighestRequiredTier {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean isHighestRequiredTier;
    
    @Valid
    private NoHighestRequiredTierJustification noHighestRequiredTierJustification;
}