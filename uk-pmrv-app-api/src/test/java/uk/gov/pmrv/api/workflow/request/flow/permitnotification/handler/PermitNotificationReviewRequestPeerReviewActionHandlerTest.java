package uk.gov.pmrv.api.workflow.request.flow.permitnotification.handler;

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
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationOutcome;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.service.PermitNotificationValidatorService;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.service.RequestPermitNotificationReviewService;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PermitNotificationReviewRequestPeerReviewActionHandlerTest {

    @InjectMocks
    private PermitNotificationReviewRequestPeerReviewActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private RequestService requestService;

    @Mock
    private WorkflowService workflowService;

    @Mock
    private RequestPermitNotificationReviewService requestPermitNotificationReviewService;

    @Mock
    private PermitNotificationValidatorService permitNotificationValidatorService;

    @Test
    void process() {
        Long requestTaskId = 1L;
        PmrvUser pmrvUser = PmrvUser.builder().userId("userId").build();
        String selectedPeerReviewer = "selectedPeerReviewer";
        PermitNotificationReviewDecision decision = PermitNotificationReviewDecision.builder()
                .type(PermitNotificationReviewDecisionType.ACCEPTED)
                .notes("notes")
                .officialNotice("officialNotice")
                .build();

        PeerReviewRequestTaskActionPayload taskActionPayload = PeerReviewRequestTaskActionPayload.builder()
                .peerReviewer(selectedPeerReviewer)
                .build();
        PermitNotificationApplicationReviewRequestTaskPayload requestTaskPayload = PermitNotificationApplicationReviewRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.PERMIT_NOTIFICATION_APPLICATION_REVIEW_PAYLOAD)
                .reviewDecision(decision)
                .build();
        RequestTask requestTask = RequestTask.builder()
                .id(requestTaskId)
                .request(Request.builder().id("2").build())
                .type(RequestTaskType.PERMIT_ISSUANCE_APPLICATION_REVIEW)
                .payload(requestTaskPayload)
                .processTaskId("processTaskId")
                .build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        // Invoke
        handler.process(requestTaskId, RequestTaskActionType.PERMIT_NOTIFICATION_REQUEST_PEER_REVIEW,
                pmrvUser, taskActionPayload);

        // Verify
        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(permitNotificationValidatorService, times(1))
                .validateNotificationReviewDecision(decision);
        verify(permitNotificationValidatorService, times(1))
                .validatePeerReviewer(selectedPeerReviewer, pmrvUser);
        verify(requestPermitNotificationReviewService, times(1))
                .saveRequestPeerReviewAction(requestTask, selectedPeerReviewer, pmrvUser);
        verify(requestService, times(1))
                .addActionToRequest(requestTask.getRequest(), null,
                        RequestActionType.PERMIT_NOTIFICATION_PEER_REVIEW_REQUESTED, pmrvUser.getUserId());
        verify(workflowService, times(1)).completeTask(
                requestTask.getProcessTaskId(),
                Map.of(BpmnProcessConstants.REQUEST_ID, requestTask.getRequest().getId(),
                        BpmnProcessConstants.REVIEW_OUTCOME, PermitNotificationOutcome.PEER_REVIEW_REQUIRED));
    }

    @Test
    void getType() {
        assertThat(handler.getTypes()).isEqualTo(List.of(RequestTaskActionType.PERMIT_NOTIFICATION_REQUEST_PEER_REVIEW));
    }
}
