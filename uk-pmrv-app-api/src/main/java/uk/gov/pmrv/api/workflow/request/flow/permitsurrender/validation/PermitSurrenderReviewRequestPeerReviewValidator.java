package uk.gov.pmrv.api.workflow.request.flow.permitsurrender.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.PeerReviewerTaskAssignmentValidator;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.service.PermitSurrenderReviewDeterminationHandlerService;

@Service
@RequiredArgsConstructor
public class PermitSurrenderReviewRequestPeerReviewValidator {

    private final PeerReviewerTaskAssignmentValidator peerReviewerTaskAssignmentValidator;
    private final PermitSurrenderReviewDeterminationHandlerService permitSurrenderReviewDeterminationHandlerService;

    public void validate(RequestTask requestTask, PeerReviewRequestTaskActionPayload payload, PmrvUser pmrvUser) {
        peerReviewerTaskAssignmentValidator.validate(RequestTaskType.PERMIT_SURRENDER_APPLICATION_PEER_REVIEW, payload.getPeerReviewer(), pmrvUser);

        PermitSurrenderApplicationReviewRequestTaskPayload requestTaskPayload =
            (PermitSurrenderApplicationReviewRequestTaskPayload) requestTask.getPayload();

        permitSurrenderReviewDeterminationHandlerService.validateReview(requestTaskPayload.getReviewDecision(), requestTaskPayload.getReviewDetermination());
    }
}
