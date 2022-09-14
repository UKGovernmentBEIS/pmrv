package uk.gov.pmrv.api.workflow.request.flow.permitsurrender.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrender;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderReviewDeterminationGrant;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderReviewDeterminationReject;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderReviewDeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderSaveReviewDeterminationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderSaveReviewGroupDecisionRequestTaskActionPayload;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class RequestPermitSurrenderReviewServiceTest {

    @InjectMocks
    private RequestPermitSurrenderReviewService service;
    
    @Mock
    private RequestTaskService requestTaskService;
    
    @Mock
    private PermitSurrenderReviewDeterminationHandlerService determinationHandlerService;

    @Mock
    private RequestService requestService;
    
    @Test
    void saveReviewDecision() {
        PermitSurrenderSaveReviewGroupDecisionRequestTaskActionPayload actionPayload = PermitSurrenderSaveReviewGroupDecisionRequestTaskActionPayload.builder()
                .payloadType(RequestTaskActionPayloadType.PERMIT_SURRENDER_SAVE_REVIEW_GROUP_DECISION_PAYLOAD)
                .reviewDecision(PermitSurrenderReviewDecision.builder().type(PermitSurrenderReviewDecisionType.REJECTED).notes("notes_reject").build())
                .reviewDeterminationCompleted(Boolean.FALSE)
                .build();
        
        PermitSurrenderApplicationReviewRequestTaskPayload taskPayload = PermitSurrenderApplicationReviewRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.PERMIT_SURRENDER_APPLICATION_REVIEW_PAYLOAD)
                .permitSurrender(PermitSurrender.builder().build())
                .reviewDecision(PermitSurrenderReviewDecision.builder().type(PermitSurrenderReviewDecisionType.ACCEPTED).notes("notes").build())
                .reviewDetermination(PermitSurrenderReviewDeterminationGrant.builder().type(PermitSurrenderReviewDeterminationType.GRANTED).stopDate(LocalDate.now().minusDays(1)).build())
                .reviewDeterminationCompleted(Boolean.TRUE)
                .build();
        
        RequestTask requestTask = RequestTask.builder().id(1L).payload(taskPayload).type(RequestTaskType.PERMIT_SURRENDER_APPLICATION_REVIEW)
                .build();
        
        service.saveReviewDecision(actionPayload, requestTask);
        
        verify(determinationHandlerService, times(1)).handleDeterminationUponDecision(PermitSurrenderReviewDeterminationType.GRANTED, taskPayload, actionPayload.getReviewDecision());
        
        ArgumentCaptor<RequestTask> requestTaskCaptor = ArgumentCaptor.forClass(RequestTask.class);
        verify(requestTaskService, times(1)).saveRequestTask(requestTaskCaptor.capture());
        RequestTask requestTaskCaptured = requestTaskCaptor.getValue();
        assertThat(((PermitSurrenderApplicationReviewRequestTaskPayload)requestTaskCaptured.getPayload()).getReviewDecision()).isEqualTo(actionPayload.getReviewDecision());
        assertThat(((PermitSurrenderApplicationReviewRequestTaskPayload)requestTaskCaptured.getPayload()).getReviewDeterminationCompleted()).isEqualTo(actionPayload.getReviewDeterminationCompleted());
    }
    
    @Test
    void saveReviewDecision_no_determination_exists() {
        PermitSurrenderSaveReviewGroupDecisionRequestTaskActionPayload actionPayload = PermitSurrenderSaveReviewGroupDecisionRequestTaskActionPayload.builder()
                .payloadType(RequestTaskActionPayloadType.PERMIT_SURRENDER_SAVE_REVIEW_GROUP_DECISION_PAYLOAD)
                .reviewDecision(PermitSurrenderReviewDecision.builder().type(PermitSurrenderReviewDecisionType.REJECTED).notes("notes_reject").build())
                .build();
        
        PermitSurrenderApplicationReviewRequestTaskPayload taskPayload = PermitSurrenderApplicationReviewRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.PERMIT_SURRENDER_APPLICATION_REVIEW_PAYLOAD)
                .permitSurrender(PermitSurrender.builder().build())
                .reviewDecision(PermitSurrenderReviewDecision.builder().type(PermitSurrenderReviewDecisionType.ACCEPTED).notes("notes").build())
                .build();
        
        RequestTask requestTask = RequestTask.builder().id(1L).payload(taskPayload).type(RequestTaskType.PERMIT_SURRENDER_APPLICATION_REVIEW)
                .build();
        
        service.saveReviewDecision(actionPayload, requestTask);
        
        verifyNoInteractions(determinationHandlerService);
        
        ArgumentCaptor<RequestTask> requestTaskCaptor = ArgumentCaptor.forClass(RequestTask.class);
        verify(requestTaskService, times(1)).saveRequestTask(requestTaskCaptor.capture());
        RequestTask requestTaskCaptured = requestTaskCaptor.getValue();
        assertThat(((PermitSurrenderApplicationReviewRequestTaskPayload)requestTaskCaptured.getPayload()).getReviewDecision()).isEqualTo(actionPayload.getReviewDecision());
    }
    
    @Test
    void saveReviewDetermination() {
        PermitSurrenderSaveReviewDeterminationRequestTaskActionPayload actionPayload = PermitSurrenderSaveReviewDeterminationRequestTaskActionPayload.builder()
                .payloadType(RequestTaskActionPayloadType.PERMIT_SURRENDER_SAVE_REVIEW_DETERMINATION_PAYLOAD)
                .reviewDetermination(PermitSurrenderReviewDeterminationGrant.builder()
                        .stopDate(LocalDate.now().minusDays(1))
                        .build())
                .reviewDeterminationCompleted(Boolean.FALSE)
                .build();
        
        PermitSurrenderApplicationReviewRequestTaskPayload taskPayload = 
                PermitSurrenderApplicationReviewRequestTaskPayload.builder()
                    .payloadType(RequestTaskPayloadType.PERMIT_SURRENDER_APPLICATION_REVIEW_PAYLOAD)
                    .permitSurrender(PermitSurrender.builder().build())
                    .reviewDecision(PermitSurrenderReviewDecision.builder().type(PermitSurrenderReviewDecisionType.ACCEPTED).notes("notes").build())
                    .build();
        
        RequestTask requestTask = RequestTask.builder().id(1L).payload(taskPayload).build();
        
        service.saveReviewDetermination(actionPayload, requestTask);
        
        verify(determinationHandlerService, times(1)).validateDecisionUponDetermination(taskPayload, actionPayload.getReviewDetermination());
        
        ArgumentCaptor<RequestTask> requestTaskCaptor = ArgumentCaptor.forClass(RequestTask.class);
        verify(requestTaskService, times(1)).saveRequestTask(requestTaskCaptor.capture());
        RequestTask requestTaskCaptured = requestTaskCaptor.getValue();
        assertThat(((PermitSurrenderApplicationReviewRequestTaskPayload)requestTaskCaptured.getPayload()).getReviewDetermination()).isEqualTo(actionPayload.getReviewDetermination());
    }

    @Test
    void saveRequestPeerReviewAction() {
        String selectedPeerReviewer = "selectedPeerReviewer";
        String reviewer = "reviewer";
        PermitSurrenderRequestPayload requestPayload = PermitSurrenderRequestPayload.builder()
            .payloadType(RequestPayloadType.PERMIT_SURRENDER_REQUEST_PAYLOAD)
            .permitSurrender(PermitSurrender.builder()
                .stopDate(LocalDate.now())
                .justification("justification")
                .documentsExist(false)
                .build())
            .build();
        Request request = Request.builder()
            .type(RequestType.PERMIT_SURRENDER)
            .payload(requestPayload)
            .build();
        PermitSurrenderApplicationReviewRequestTaskPayload requestTaskPayload = PermitSurrenderApplicationReviewRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.PERMIT_SURRENDER_APPLICATION_REVIEW_PAYLOAD)
            .reviewDecision(PermitSurrenderReviewDecision.builder().type(PermitSurrenderReviewDecisionType.REJECTED).notes("rejected").build())
            .reviewDetermination(PermitSurrenderReviewDeterminationReject.builder()
                    .type(PermitSurrenderReviewDeterminationType.REJECTED)
                    .officialRefusalLetter("official refusal letter")
                    .shouldFeeBeRefundedToOperator(false)
                    .build())
            .reviewDeterminationCompleted(true)
            .build();
        RequestTask requestTask = RequestTask.builder()
            .type(RequestTaskType.PERMIT_SURRENDER_APPLICATION_REVIEW)
            .request(request)
            .payload(requestTaskPayload)
            .build();

        service.saveRequestPeerReviewAction(requestTask, selectedPeerReviewer, reviewer);

        ArgumentCaptor<Request> requestCaptor = ArgumentCaptor.forClass(Request.class);
        verify(requestService, times(1)).saveRequest(requestCaptor.capture());

        final Request updatedRequest = requestCaptor.getValue();
        assertThat(updatedRequest.getPayload()).isInstanceOf(PermitSurrenderRequestPayload.class);

        final PermitSurrenderRequestPayload updatedPayload = (PermitSurrenderRequestPayload) updatedRequest.getPayload();

        assertEquals(reviewer, updatedPayload.getRegulatorReviewer());
        assertEquals(selectedPeerReviewer, updatedPayload.getRegulatorPeerReviewer());
        assertThat(updatedPayload.getPermitSurrender()).isEqualTo(requestPayload.getPermitSurrender());
        assertThat(updatedPayload.getPermitSurrenderAttachments()).isEmpty();
        assertThat(updatedPayload.getReviewDecision()).isEqualTo(requestTaskPayload.getReviewDecision());
        assertThat(updatedPayload.getReviewDetermination()).isEqualTo(requestTaskPayload.getReviewDetermination());
        assertThat(updatedPayload.getReviewDeterminationCompleted()).isEqualTo(requestTaskPayload.getReviewDeterminationCompleted());
    }

    @Test
    void saveReviewDecisionNotification() {
        final PmrvUser pmrvUser = PmrvUser.builder().userId("user").build();
        
        PermitSurrenderApplicationReviewRequestTaskPayload taskPayload = 
                PermitSurrenderApplicationReviewRequestTaskPayload.builder()
                    .payloadType(RequestTaskPayloadType.PERMIT_SURRENDER_APPLICATION_REVIEW_PAYLOAD)
                    .reviewDecision(PermitSurrenderReviewDecision.builder()
                            .type(PermitSurrenderReviewDecisionType.ACCEPTED)
                            .notes("notes")
                            .build())
                    .reviewDetermination(PermitSurrenderReviewDeterminationGrant.builder()
                            .type(PermitSurrenderReviewDeterminationType.GRANTED)
                            .stopDate(LocalDate.now().minusDays(1))
                            .build())
                    .build();
        PermitSurrenderRequestPayload requestPayload = PermitSurrenderRequestPayload.builder()
                .payloadType(RequestPayloadType.PERMIT_SURRENDER_REQUEST_PAYLOAD)
                .build();
        Request request = Request.builder().id("1").payload(requestPayload).build();
        RequestTask requestTask = RequestTask.builder().id(1L).request(request).payload(taskPayload).build();
        
        DecisionNotification decisionNotification = DecisionNotification.builder()
                .operators(Set.of("operator1", "operator2"))
                .signatory("regulator")
                .build();
        
        service.saveReviewDecisionNotification(requestTask, decisionNotification, pmrvUser);
        
        ArgumentCaptor<Request> requestCaptor = ArgumentCaptor.forClass(Request.class);
        verify(requestService, times(1)).saveRequest(requestCaptor.capture());
        Request requestCaptured = requestCaptor.getValue();
        assertThat(((PermitSurrenderRequestPayload)requestCaptured.getPayload()).getReviewDecision()).isEqualTo(taskPayload.getReviewDecision());
        assertThat(((PermitSurrenderRequestPayload)requestCaptured.getPayload()).getReviewDetermination()).isEqualTo(taskPayload.getReviewDetermination());
        assertThat(((PermitSurrenderRequestPayload)requestCaptured.getPayload()).getReviewDecisionNotification()).isEqualTo(decisionNotification);
        assertThat(((PermitSurrenderRequestPayload)requestCaptured.getPayload()).getRegulatorReviewer()).isEqualTo(pmrvUser.getUserId());
        assertThat(((PermitSurrenderRequestPayload)requestCaptured.getPayload()).getReviewDeterminationCompletedDate()).isNotNull();
    }
}
