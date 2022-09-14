package uk.gov.pmrv.api.workflow.request.flow.permitrevocation.handler;

import java.util.List;
import java.util.Map;
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
import uk.gov.pmrv.api.workflow.request.flow.permitrevocation.domain.PermitRevocationApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitrevocation.domain.PermitRevocationOutcome;
import uk.gov.pmrv.api.workflow.request.flow.permitrevocation.service.RequestPermitRevocationService;
import uk.gov.pmrv.api.workflow.request.flow.permitrevocation.validation.PermitRevocationValidator;

@Component
@RequiredArgsConstructor
public class PermitRevocationRequestPeerReviewActionHandler implements
    RequestTaskActionHandler<PeerReviewRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final PermitRevocationValidator validator;
    private final RequestPermitRevocationService requestPermitRevocationService;
    private final RequestService requestService;
    private final WorkflowService workflowService;

    @Override
    public void process(final Long requestTaskId,
                        final RequestTaskActionType requestTaskActionType,
                        final PmrvUser pmrvUser,
                        final PeerReviewRequestTaskActionPayload actionPayload) {

        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        final Request request = requestTask.getRequest();
        final PermitRevocationApplicationSubmitRequestTaskPayload payload =
            (PermitRevocationApplicationSubmitRequestTaskPayload) requestTask.getPayload();
        final String peerReviewer = actionPayload.getPeerReviewer();
        
        validator.validateSubmitRequestTaskPayload(payload);
        validator.validatePeerReviewer(peerReviewer, pmrvUser);

        final String regulatorReviewer = pmrvUser.getUserId();
        requestPermitRevocationService.requestPeerReview(requestTask, peerReviewer, regulatorReviewer);

        requestService.addActionToRequest(request,
            null,
            RequestActionType.PERMIT_REVOCATION_PEER_REVIEW_REQUESTED,
            regulatorReviewer);

        workflowService.completeTask(requestTask.getProcessTaskId(),
            Map.of(BpmnProcessConstants.REVOCATION_OUTCOME, PermitRevocationOutcome.PEER_REVIEW_REQUIRED)
        );
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.PERMIT_REVOCATION_REQUEST_PEER_REVIEW);
    }
}
