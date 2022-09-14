package uk.gov.pmrv.api.permit.domain.monitoringapproaches.pfc;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.common.domain.dto.validation.SpELExpression;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.HighestRequiredTier;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SpELExpression(expression = "{(#tier eq 'TIER_1') == (#isHighestRequiredTier != null)}", 
    message = "permit.pfcMonitoringApproach.sourceStreamCategoryAppliedTiers.emissionfactor.isHighestRequiredTier")
public class PFCEmissionFactor {
    
    @NotNull
    private PFCEmissionFactorTier tier;
    
    @Valid
    @JsonUnwrapped
    private HighestRequiredTier highestRequiredTier;
    
}