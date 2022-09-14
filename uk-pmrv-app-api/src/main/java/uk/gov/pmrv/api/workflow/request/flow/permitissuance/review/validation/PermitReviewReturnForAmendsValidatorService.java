package uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.ReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain.PermitIssuanceApplicationReviewRequestTaskPayload;

@Service
@RequiredArgsConstructor
public class PermitReviewReturnForAmendsValidatorService {

    public void validate(final PermitIssuanceApplicationReviewRequestTaskPayload taskPayload) {

        // Validate if operator amends needed exist
        boolean amendExists = taskPayload.getReviewGroupDecisions().values().stream()
                .anyMatch(reviewDecision -> reviewDecision.getType().equals(ReviewDecisionType.OPERATOR_AMENDS_NEEDED));
        if(!amendExists){
            throw new BusinessException(ErrorCode.INVALID_PERMIT_REVIEW);
        }
    }
}
