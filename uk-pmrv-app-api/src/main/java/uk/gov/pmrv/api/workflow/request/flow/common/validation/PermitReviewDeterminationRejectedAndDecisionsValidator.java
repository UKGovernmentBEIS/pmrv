package uk.gov.pmrv.api.workflow.request.flow.common.validation;

import org.springframework.stereotype.Service;

import uk.gov.pmrv.api.workflow.request.flow.common.domain.ReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.permit.PermitPayloadGroupDecidable;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.permit.PermitReviewDecision;

@Service
public class PermitReviewDeterminationRejectedAndDecisionsValidator<T extends PermitReviewDecision> {

    public boolean isValid(final PermitPayloadGroupDecidable<T> taskPayload) {
        return taskPayload.getReviewGroupDecisions().values().stream()
                .anyMatch(dec -> dec.getType().equals(ReviewDecisionType.REJECTED));
    }
    
}
