package uk.gov.pmrv.api.permit.domain.estimatedannualemissions;

import java.math.BigDecimal;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.common.domain.dto.validation.SpELExpression;
import uk.gov.pmrv.api.permit.domain.PermitSection;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SpELExpression(expression = "{(#quantity?.scale() < 2)}", message = "permit.estimatedAnnualEmissions.quantity")
public class EstimatedAnnualEmissions implements PermitSection {

    @NotNull
    @Positive
    private BigDecimal quantity;
}
