package uk.gov.pmrv.api.workflow.request.flow.permitsurrender.service;

import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderReviewDetermination;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderReviewDeterminationType;

interface PermitSurrenderReviewDeterminationHandler<T extends PermitSurrenderReviewDetermination> {

    void handleDeterminationUponDecision(PermitSurrenderApplicationReviewRequestTaskPayload taskPayload,
            PermitSurrenderReviewDecision reviewDecision);

    void validateDecisionUponDetermination(PermitSurrenderApplicationReviewRequestTaskPayload taskPayload);
    
    void validateReview(PermitSurrenderReviewDecision reviewDecision, T reviewDetermination);

    PermitSurrenderReviewDeterminationType getType();
}
