package uk.gov.pmrv.api.workflow.request.flow.permitsurrender.service;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderReviewDeterminationDeemWithdraw;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderReviewDeterminationType;

@Validated
@Service
class PermitSurrenderReviewDeterminationDeemWithdrawHandler implements PermitSurrenderReviewDeterminationHandler<PermitSurrenderReviewDeterminationDeemWithdraw> {

    public void handleDeterminationUponDecision(PermitSurrenderApplicationReviewRequestTaskPayload taskPayload,
            PermitSurrenderReviewDecision reviewDecision) {
        // ok
    }

    @Override
    public void validateDecisionUponDetermination(PermitSurrenderApplicationReviewRequestTaskPayload taskPayload) {
        // ok
    }

    @Override
    public void validateReview(PermitSurrenderReviewDecision reviewDecision, PermitSurrenderReviewDeterminationDeemWithdraw reviewDetermination) {
        // ok
    }
    
    @Override
    public PermitSurrenderReviewDeterminationType getType() {
        return PermitSurrenderReviewDeterminationType.DEEMED_WITHDRAWN;
    }

}
