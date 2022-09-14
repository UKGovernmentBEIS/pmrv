package uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.handler;

import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType.PERMIT_ISSUANCE_PEER_REVIEW_REQUESTED;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.ReviewOutcome;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.service.PermitIssuanceReviewService;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.validation.PermitReviewRequestPeerReviewValidator;

@Component
@RequiredArgsConstructor
public class PermitReviewRequestPeerReviewActionHandler
    implements RequestTaskActionHandler<PeerReviewRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final PermitIssuanceReviewService permitIssuanceReviewService;
    private final RequestService requestService;
    private final WorkflowService workflowService;
    private final PermitReviewRequestPeerReviewValidator permitReviewRequestPeerReviewValidator;


    @Override
    public void process(Long requestTaskId, RequestTaskActionType requestTaskActionType, PmrvUser pmrvUser,
                        PeerReviewRequestTaskActionPayload payload) {

        RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);

        permitReviewRequestPeerReviewValidator.validate(requestTask, payload, pmrvUser);

        permitIssuanceReviewService.saveRequestPeerReviewAction(requestTask, payload.getPeerReviewer(), pmrvUser);

        requestService.addActionToRequest(requestTask.getRequest(), null, PERMIT_ISSUANCE_PEER_REVIEW_REQUESTED, pmrvUser.getUserId());

        workflowService.completeTask(
            requestTask.getProcessTaskId(),
            Map.of(BpmnProcessConstants.REQUEST_ID, requestTask.getRequest().getId(),
                BpmnProcessConstants.REVIEW_OUTCOME, ReviewOutcome.PEER_REVIEW_REQUIRED)
        );

    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.PERMIT_ISSUANCE_REQUEST_PEER_REVIEW);
    }
}
