package uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain;

import java.util.HashMap;
import java.util.Map;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.permit.PermitReviewGroup;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload extends RequestTaskActionPayload {

    @NotNull
    private PermitReviewGroup group;

    @NotNull
    @Valid
    private PermitIssuanceReviewDecision decision;
    
    @Builder.Default
    private Map<String, Boolean> reviewSectionsCompleted = new HashMap<>();
}
