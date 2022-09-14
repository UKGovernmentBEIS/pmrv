package uk.gov.pmrv.api.workflow.request.flow.permitsurrender.handler;

import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType.PERMIT_SURRENDER_PEER_REVIEW_REQUESTED;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderReviewOutcome;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.service.RequestPermitSurrenderReviewService;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.validation.PermitSurrenderReviewRequestPeerReviewValidator;

@Component
@RequiredArgsConstructor
public class PermitSurrenderReviewRequestPeerReviewActionHandler implements
    RequestTaskActionHandler<PeerReviewRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final PermitSurrenderReviewRequestPeerReviewValidator permitSurrenderReviewRequestPeerReviewValidator;
    private final RequestPermitSurrenderReviewService requestPermitSurrenderReviewService;
    private final RequestService requestService;
    private final WorkflowService workflowService;

    @Override
    public void process(Long requestTaskId, RequestTaskActionType requestTaskActionType, PmrvUser pmrvUser,
                        PeerReviewRequestTaskActionPayload payload) {

        RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        Request request = requestTask.getRequest();
        String selectedPeerReviewer = payload.getPeerReviewer();
        String regulatorReviewer = pmrvUser.getUserId();

        permitSurrenderReviewRequestPeerReviewValidator.validate(requestTask, payload, pmrvUser);

        requestPermitSurrenderReviewService.saveRequestPeerReviewAction(requestTask, selectedPeerReviewer, regulatorReviewer);

        requestService.addActionToRequest(request, null, PERMIT_SURRENDER_PEER_REVIEW_REQUESTED, regulatorReviewer);

        workflowService.completeTask(
            requestTask.getProcessTaskId(),
            Map.of(BpmnProcessConstants.REQUEST_ID, request.getId(),
                BpmnProcessConstants.REVIEW_OUTCOME, PermitSurrenderReviewOutcome.PEER_REVIEW_REQUIRED)
        );
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.PERMIT_SURRENDER_REQUEST_PEER_REVIEW);
    }
}
