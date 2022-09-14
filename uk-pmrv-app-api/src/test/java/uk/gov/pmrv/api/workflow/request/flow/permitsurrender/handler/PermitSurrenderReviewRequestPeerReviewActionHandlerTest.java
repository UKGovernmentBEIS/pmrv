package uk.gov.pmrv.api.workflow.request.flow.permitsurrender.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType.PERMIT_SURRENDER_PEER_REVIEW_REQUESTED;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderReviewOutcome;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.service.RequestPermitSurrenderReviewService;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.validation.PermitSurrenderReviewRequestPeerReviewValidator;

@ExtendWith(MockitoExtension.class)
class PermitSurrenderReviewRequestPeerReviewActionHandlerTest {

    @InjectMocks
    private PermitSurrenderReviewRequestPeerReviewActionHandler requestPeerReviewActionHandler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private PermitSurrenderReviewRequestPeerReviewValidator permitSurrenderReviewRequestPeerReviewValidator;

    @Mock
    private RequestService requestService;

    @Mock
    private WorkflowService workflowService;

    @Mock
    private RequestPermitSurrenderReviewService requestPermitSurrenderReviewService;

    @Test
    void process() {
        Long requestTaskId = 1L;
        PmrvUser pmrvUser = PmrvUser.builder().userId("userId").build();
        String selectedPeerReviewer = "selectedPeerReviewer";
        PeerReviewRequestTaskActionPayload taskActionPayload = PeerReviewRequestTaskActionPayload.builder()
            .peerReviewer(selectedPeerReviewer)
            .build();
        PermitSurrenderRequestPayload requestPayload = PermitSurrenderRequestPayload.builder()
            .payloadType(RequestPayloadType.PERMIT_SURRENDER_REQUEST_PAYLOAD)
            .build();
        Request request = Request.builder()
            .id("120")
            .type(RequestType.PERMIT_SURRENDER)
            .payload(requestPayload)
            .build();
        PermitSurrenderApplicationReviewRequestTaskPayload requestTaskPayload = PermitSurrenderApplicationReviewRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.PERMIT_SURRENDER_APPLICATION_REVIEW_PAYLOAD)
            .build();
        RequestTask requestTask = RequestTask.builder()
            .id(requestTaskId)
            .request(request)
            .type(RequestTaskType.PERMIT_SURRENDER_APPLICATION_REVIEW)
            .payload(requestTaskPayload)
            .processTaskId("processTaskId")
            .build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        requestPeerReviewActionHandler.process(
            requestTaskId,
            RequestTaskActionType.PERMIT_SURRENDER_REQUEST_PEER_REVIEW,
            pmrvUser,
            taskActionPayload);

        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(permitSurrenderReviewRequestPeerReviewValidator, times(1))
            .validate(requestTask, taskActionPayload, pmrvUser);
        verify(requestPermitSurrenderReviewService, times(1))
            .saveRequestPeerReviewAction(requestTask, selectedPeerReviewer, pmrvUser.getUserId());
        verify(requestService, times(1))
            .addActionToRequest(requestTask.getRequest(), null, PERMIT_SURRENDER_PEER_REVIEW_REQUESTED, pmrvUser.getUserId());
        verify(workflowService, times(1)).completeTask(
            requestTask.getProcessTaskId(),
            Map.of(BpmnProcessConstants.REQUEST_ID, request.getId(),
                BpmnProcessConstants.REVIEW_OUTCOME, PermitSurrenderReviewOutcome.PEER_REVIEW_REQUIRED));
    }

    @Test
    void getTypes() {
        assertThat(requestPeerReviewActionHandler.getTypes()).containsExactly(RequestTaskActionType.PERMIT_SURRENDER_REQUEST_PEER_REVIEW);
    }
}
