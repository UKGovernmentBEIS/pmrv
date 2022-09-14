package uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculation;

import javax.validation.Valid;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.common.domain.dto.validation.SpELExpression;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SpELExpression(expression = "{#exist == (#details != null)}", message = "permit.calculationApproach.samplingPlan.exist")
public class SamplingPlan {
    
    private boolean exist;
    
    @Valid
    private SamplingPlanDetails details;
    
}
