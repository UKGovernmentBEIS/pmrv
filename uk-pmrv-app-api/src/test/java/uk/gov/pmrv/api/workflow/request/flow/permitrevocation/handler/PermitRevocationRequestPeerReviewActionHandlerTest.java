package uk.gov.pmrv.api.workflow.request.flow.permitrevocation.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitrevocation.domain.PermitRevocationApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitrevocation.domain.PermitRevocationOutcome;
import uk.gov.pmrv.api.workflow.request.flow.permitrevocation.domain.PermitRevocationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitrevocation.service.RequestPermitRevocationService;
import uk.gov.pmrv.api.workflow.request.flow.permitrevocation.validation.PermitRevocationValidator;

@ExtendWith(MockitoExtension.class)
class PermitRevocationRequestPeerReviewActionHandlerTest {

    @InjectMocks
    private PermitRevocationRequestPeerReviewActionHandler requestPeerReviewActionHandler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private PermitRevocationValidator peerReviewValidator;

    @Mock
    private RequestPermitRevocationService requestPermitRevocationService;

    @Mock
    private RequestService requestService;

    @Mock
    private WorkflowService workflowService;

    @Test
    void process() {

        final Long requestTaskId = 1L;
        final PmrvUser pmrvUser = PmrvUser.builder().userId("userId").build();
        final String selectedPeerReviewer = "selectedPeerReviewer";
        final PeerReviewRequestTaskActionPayload taskActionPayload = PeerReviewRequestTaskActionPayload.builder()
            .peerReviewer(selectedPeerReviewer)
            .build();
        final PermitRevocationRequestPayload requestPayload = PermitRevocationRequestPayload.builder()
            .payloadType(RequestPayloadType.PERMIT_REVOCATION_REQUEST_PAYLOAD)
            .build();
        final Request request = Request.builder()
            .id("120")
            .type(RequestType.PERMIT_REVOCATION)
            .payload(requestPayload)
            .build();
        final PermitRevocationApplicationSubmitRequestTaskPayload requestTaskPayload =
            PermitRevocationApplicationSubmitRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.PERMIT_REVOCATION_APPLICATION_SUBMIT_PAYLOAD)
                .build();
        final RequestTask requestTask = RequestTask.builder()
            .id(requestTaskId)
            .request(request)
            .type(RequestTaskType.PERMIT_REVOCATION_APPLICATION_SUBMIT)
            .payload(requestTaskPayload)
            .processTaskId("processTaskId")
            .build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        requestPeerReviewActionHandler.process(
            requestTaskId,
            RequestTaskActionType.PERMIT_REVOCATION_REQUEST_PEER_REVIEW,
            pmrvUser,
            taskActionPayload);

        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(peerReviewValidator, times(1))
            .validatePeerReviewer( "selectedPeerReviewer", pmrvUser);
        verify(requestPermitRevocationService, times(1))
            .requestPeerReview(requestTask, selectedPeerReviewer, pmrvUser.getUserId());
        verify(requestService, times(1)).addActionToRequest(requestTask.getRequest(),
            null,
            RequestActionType.PERMIT_REVOCATION_PEER_REVIEW_REQUESTED,
            pmrvUser.getUserId());
        verify(workflowService, times(1)).completeTask(
            requestTask.getProcessTaskId(),
            Map.of(BpmnProcessConstants.REVOCATION_OUTCOME, PermitRevocationOutcome.PEER_REVIEW_REQUIRED));
    }

    @Test
    void getTypes() {
        assertThat(requestPeerReviewActionHandler.getTypes()).containsExactly(
            RequestTaskActionType.PERMIT_REVOCATION_REQUEST_PEER_REVIEW);
    }
}
