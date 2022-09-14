package uk.gov.pmrv.api.workflow.request.flow.permitsurrender.service;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderReviewDeterminationGrant;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderReviewDeterminationType;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Validated
@Service
class PermitSurrenderReviewDeterminationGrantHandler implements PermitSurrenderReviewDeterminationHandler<PermitSurrenderReviewDeterminationGrant> {

    public void handleDeterminationUponDecision(PermitSurrenderApplicationReviewRequestTaskPayload taskPayload,
            PermitSurrenderReviewDecision reviewDecision) {
        if(reviewDecision.getType() != PermitSurrenderReviewDecisionType.ACCEPTED) {
            taskPayload.setReviewDetermination(null);
        }
    }

    public void validateDecisionUponDetermination(PermitSurrenderApplicationReviewRequestTaskPayload taskPayload) {
        PermitSurrenderReviewDecision reviewDecision = taskPayload.getReviewDecision();
        if(reviewDecision == null ||
                reviewDecision.getType() != PermitSurrenderReviewDecisionType.ACCEPTED) {
            throw new BusinessException(ErrorCode.FORM_VALIDATION);
        }
    }
    
    public void validateReview(PermitSurrenderReviewDecision reviewDecision, PermitSurrenderReviewDeterminationGrant reviewDetermination) {
        if(reviewDecision.getType() != PermitSurrenderReviewDecisionType.ACCEPTED) {
            throw new BusinessException(ErrorCode.FORM_VALIDATION);
        }
    }
    
    @Override
    public PermitSurrenderReviewDeterminationType getType() {
        return PermitSurrenderReviewDeterminationType.GRANTED;
    }

}
