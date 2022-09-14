package uk.gov.pmrv.api.workflow.request.flow.common.domain.permit;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.ReviewDecisionType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class PermitReviewDecision {

    @NotNull
    private ReviewDecisionType type;

    @Size(max = 10000)
    private String notes;
}
