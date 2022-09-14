package uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType.PERMIT_ISSUANCE_PEER_REVIEW_REQUESTED;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.ReviewOutcome;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.permit.DeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain.PermitIssuanceApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain.PermitIssuanceDeemedWithdrawnDetermination;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.service.PermitIssuanceReviewService;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.validation.PermitReviewRequestPeerReviewValidator;

@ExtendWith(MockitoExtension.class)
class PermitReviewRequestPeerReviewActionHandlerTest {

    @InjectMocks
    private PermitReviewRequestPeerReviewActionHandler requestPeerReviewActionHandler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private PermitIssuanceReviewService permitIssuanceReviewService;

    @Mock
    private WorkflowService workflowService;

    @Mock
    private PermitReviewRequestPeerReviewValidator permitReviewRequestPeerReviewValidator;

    @Mock
    private RequestService requestService;

    @Test
    void process() {
        Long requestTaskId = 1L;
        PmrvUser pmrvUser = PmrvUser.builder().userId("userId").build();
        String selectedPeerReviewer = "selectedPeerReviewer";
        PeerReviewRequestTaskActionPayload taskActionPayload = PeerReviewRequestTaskActionPayload.builder()
            .peerReviewer(selectedPeerReviewer)
            .build();
        PermitIssuanceApplicationReviewRequestTaskPayload requestTaskPayload = PermitIssuanceApplicationReviewRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.PERMIT_ISSUANCE_APPLICATION_REVIEW_PAYLOAD)
            .determination(PermitIssuanceDeemedWithdrawnDetermination.builder().reason("reason").type(DeterminationType.DEEMED_WITHDRAWN).build())
            .build();
        RequestTask requestTask = RequestTask.builder()
            .id(requestTaskId)
            .request(Request.builder().id("2").build())
            .type(RequestTaskType.PERMIT_ISSUANCE_APPLICATION_REVIEW)
            .payload(requestTaskPayload)
            .processTaskId("processTaskId")
            .build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        requestPeerReviewActionHandler.process(
            requestTaskId,
            RequestTaskActionType.PERMIT_ISSUANCE_REQUEST_PEER_REVIEW,
            pmrvUser,
            taskActionPayload);

        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(permitReviewRequestPeerReviewValidator, times(1))
            .validate(requestTask, taskActionPayload, pmrvUser);
        verify(permitIssuanceReviewService, times(1)).saveRequestPeerReviewAction(requestTask, selectedPeerReviewer, pmrvUser);
        verify(requestService, times(1))
            .addActionToRequest(requestTask.getRequest(), null, PERMIT_ISSUANCE_PEER_REVIEW_REQUESTED, pmrvUser.getUserId());
        verify(workflowService, times(1)).completeTask(
            requestTask.getProcessTaskId(),
            Map.of(BpmnProcessConstants.REQUEST_ID, requestTask.getRequest().getId(),
                BpmnProcessConstants.REVIEW_OUTCOME, ReviewOutcome.PEER_REVIEW_REQUIRED));
    }

    @Test
    void process_invalid_determination() {
        Long requestTaskId = 1L;
        PmrvUser pmrvUser = PmrvUser.builder().build();
        String selectedPeerReviewer = "selectedPeerReviewer";
        PeerReviewRequestTaskActionPayload taskActionPayload = PeerReviewRequestTaskActionPayload.builder()
            .peerReviewer(selectedPeerReviewer)
            .build();
        PermitIssuanceApplicationReviewRequestTaskPayload requestTaskPayload = PermitIssuanceApplicationReviewRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.PERMIT_ISSUANCE_APPLICATION_REVIEW_PAYLOAD)
            .determination(PermitIssuanceDeemedWithdrawnDetermination.builder().reason("reason").type(DeterminationType.DEEMED_WITHDRAWN).build())
            .build();
        RequestTask requestTask = RequestTask.builder()
            .id(requestTaskId)
            .request(Request.builder().id("2").build())
            .type(RequestTaskType.PERMIT_ISSUANCE_APPLICATION_REVIEW)
            .payload(requestTaskPayload)
            .processTaskId("processTaskId")
            .build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);
        doThrow(new BusinessException(ErrorCode.FORM_VALIDATION))
            .when(permitReviewRequestPeerReviewValidator)
            .validate(requestTask, taskActionPayload, pmrvUser);

        BusinessException be = assertThrows(BusinessException.class,
            () -> requestPeerReviewActionHandler.process(
                requestTaskId,
                RequestTaskActionType.PERMIT_ISSUANCE_REQUEST_PEER_REVIEW,
                pmrvUser,
                taskActionPayload));

        assertEquals(ErrorCode.FORM_VALIDATION, be.getErrorCode());

        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(permitReviewRequestPeerReviewValidator, times(1))
            .validate(requestTask, taskActionPayload, pmrvUser);
    }

    @Test
    void getTypes() {
        assertThat(requestPeerReviewActionHandler.getTypes()).containsExactly(RequestTaskActionType.PERMIT_ISSUANCE_REQUEST_PEER_REVIEW);
    }
}
