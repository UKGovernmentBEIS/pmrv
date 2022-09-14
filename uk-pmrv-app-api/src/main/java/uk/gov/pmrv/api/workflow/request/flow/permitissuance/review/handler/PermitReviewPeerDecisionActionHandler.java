package uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.handler;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewDecisionSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.common.mapper.PeerReviewMapper;

@Component
@RequiredArgsConstructor
public class PermitReviewPeerDecisionActionHandler
    implements RequestTaskActionHandler<PeerReviewDecisionRequestTaskActionPayload> {
    
    private static final PeerReviewMapper PEER_REVIEW_MAPPER = Mappers.getMapper(PeerReviewMapper.class);
    
    private final RequestService requestService;
    private final RequestTaskService requestTaskService;
    private final WorkflowService workflowService;

    @Override
    public void process(final Long requestTaskId,
                        final RequestTaskActionType requestTaskActionType,
                        final PmrvUser pmrvUser,
                        final PeerReviewDecisionRequestTaskActionPayload taskActionPayload) {

        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        final Request request = requestTask.getRequest();
        final String peerReviewer = pmrvUser.getUserId();

        final PeerReviewDecisionSubmittedRequestActionPayload actionPayload =
            PEER_REVIEW_MAPPER.toPeerReviewDecisionSubmittedRequestActionPayload(
                taskActionPayload,
                RequestActionPayloadType.PERMIT_ISSUANCE_PEER_REVIEW_DECISION_SUBMITTED_PAYLOAD
            );

        final RequestActionType type = actionPayload.getDecision().getType() == PeerReviewDecisionType.AGREE ?
            RequestActionType.PERMIT_ISSUANCE_APPLICATION_PEER_REVIEWER_ACCEPTED :
            RequestActionType.PERMIT_ISSUANCE_APPLICATION_PEER_REVIEWER_REJECTED;
        
        requestService.addActionToRequest(request,
            actionPayload,
            type,
            peerReviewer);

        workflowService.completeTask(requestTask.getProcessTaskId());
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.PERMIT_ISSUANCE_REVIEW_SUBMIT_PEER_REVIEW_DECISION);
    }
}
