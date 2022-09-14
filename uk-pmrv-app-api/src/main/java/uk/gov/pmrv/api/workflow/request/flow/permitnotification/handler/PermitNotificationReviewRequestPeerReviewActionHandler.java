package uk.gov.pmrv.api.workflow.request.flow.permitnotification.handler;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationOutcome;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.service.PermitNotificationValidatorService;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.service.RequestPermitNotificationReviewService;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class PermitNotificationReviewRequestPeerReviewActionHandler implements
        RequestTaskActionHandler<PeerReviewRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final RequestService requestService;
    private final WorkflowService workflowService;
    private final RequestPermitNotificationReviewService requestPermitNotificationReviewService;
    private final PermitNotificationValidatorService permitNotificationValidatorService;

    @Override
    public void process(Long requestTaskId, RequestTaskActionType requestTaskActionType, PmrvUser pmrvUser,
                        PeerReviewRequestTaskActionPayload actionPayload) {
        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        final Request request = requestTask.getRequest();
        final PermitNotificationApplicationReviewRequestTaskPayload payload =
                (PermitNotificationApplicationReviewRequestTaskPayload) requestTask.getPayload();
        final String peerReviewer = actionPayload.getPeerReviewer();

        // Validate
        permitNotificationValidatorService.validateNotificationReviewDecision(payload.getReviewDecision());
        permitNotificationValidatorService.validatePeerReviewer(peerReviewer, pmrvUser);

        // Save as Peer Review
        requestPermitNotificationReviewService.saveRequestPeerReviewAction(requestTask, peerReviewer, pmrvUser);
        requestService.addActionToRequest(request, null,
                RequestActionType.PERMIT_NOTIFICATION_PEER_REVIEW_REQUESTED, pmrvUser.getUserId());

        // Complete task
        workflowService.completeTask(requestTask.getProcessTaskId(),
                Map.of(BpmnProcessConstants.REQUEST_ID, requestTask.getRequest().getId(),
                        BpmnProcessConstants.REVIEW_OUTCOME, PermitNotificationOutcome.PEER_REVIEW_REQUIRED)
        );
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.PERMIT_NOTIFICATION_REQUEST_PEER_REVIEW);
    }
}
