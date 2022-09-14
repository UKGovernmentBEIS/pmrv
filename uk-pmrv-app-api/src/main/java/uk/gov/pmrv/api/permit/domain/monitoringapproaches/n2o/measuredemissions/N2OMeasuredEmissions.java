package uk.gov.pmrv.api.permit.domain.monitoringapproaches.n2o.measuredemissions;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.common.domain.dto.validation.SpELExpression;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.MeasuredEmissions;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@SpELExpression(expression = "{(#tier eq 'TIER_1' or #tier eq 'TIER_2') == (#isHighestRequiredTier != null)}", 
    message = "permit.n2oMonitoringApproach.n2oMeasuredEmissions.isHighestRequiredTier_tier_1_2")
@SpELExpression(expression = "{(#tier eq 'TIER_3') == (#isHighestRequiredTier == null)}", 
    message = "permit.n2oMonitoringApproach.n2oMeasuredEmissions.isHighestRequiredTier_tier_3")
public class N2OMeasuredEmissions extends MeasuredEmissions {

    @NotNull
    private N2OMeasuredEmissionsTier tier;

}
