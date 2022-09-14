package uk.gov.pmrv.api.workflow.request.flow.permitsurrender.service;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderReviewDeterminationReject;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderReviewDeterminationType;

@Validated
@Service
public class PermitSurrenderReviewDeterminationRejectHandler implements PermitSurrenderReviewDeterminationHandler<PermitSurrenderReviewDeterminationReject> {

    @Override
    public void handleDeterminationUponDecision(PermitSurrenderApplicationReviewRequestTaskPayload taskPayload,
                                                PermitSurrenderReviewDecision reviewDecision) {
        if(reviewDecision.getType() != PermitSurrenderReviewDecisionType.REJECTED) {
            taskPayload.setReviewDetermination(null);
        }
    }

    @Override
    public void validateDecisionUponDetermination(PermitSurrenderApplicationReviewRequestTaskPayload taskPayload) {
        PermitSurrenderReviewDecision reviewDecision = taskPayload.getReviewDecision();
        if(reviewDecision == null ||
                reviewDecision.getType() != PermitSurrenderReviewDecisionType.REJECTED) {
            throw new BusinessException(ErrorCode.FORM_VALIDATION);
        }
    }
    
    @Override
    public void validateReview(PermitSurrenderReviewDecision reviewDecision, PermitSurrenderReviewDeterminationReject reviewDetermination) {
        if(reviewDecision.getType() != PermitSurrenderReviewDecisionType.REJECTED) {
            throw new BusinessException(ErrorCode.FORM_VALIDATION);
        }
    }

    @Override
    public PermitSurrenderReviewDeterminationType getType() {
        return PermitSurrenderReviewDeterminationType.REJECTED;
    }

    
}
