package uk.gov.pmrv.api.workflow.request.flow.permitsurrender.handler;

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
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.NotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.ReviewOutcome;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.DecisionNotificationUsersValidator;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderReviewDeterminationGrant;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderReviewDeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.service.PermitSurrenderReviewDeterminationHandlerService;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.service.RequestPermitSurrenderReviewService;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PermitSurrenderReviewNotifyOperatorActionHandlerTest {

    @InjectMocks
    private PermitSurrenderReviewNotifyOperatorActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;
    
    @Mock
    private PermitSurrenderReviewDeterminationHandlerService reviewDeterminationHandlerService;
    
    @Mock
    private RequestPermitSurrenderReviewService requestPermitSurrenderReviewService;
    
    @Mock
    private WorkflowService workflowService;
    
    @Mock
    private DecisionNotificationUsersValidator decisionNotificationUsersValidator;
    
    @Test
    void process() {
        final Long requestTaskId = 1L;
        final RequestTaskActionType requestTaskActionType = RequestTaskActionType.PERMIT_SURRENDER_NOTIFY_OPERATOR_FOR_DECISION;
        final PmrvUser pmrvUser = PmrvUser.builder().userId("user").build();
        
        final NotifyOperatorForDecisionRequestTaskActionPayload notifyPayload = NotifyOperatorForDecisionRequestTaskActionPayload.builder()
                .payloadType(RequestTaskActionPayloadType.PERMIT_SURRENDER_NOTIFY_OPERATOR_FOR_DECISION_PAYLOAD)
                .decisionNotification(DecisionNotification.builder()
                        .operators(Set.of("operator1", "operator2"))
                        .signatory("regulator")
                        .build())
                .build();
        
        final PermitSurrenderApplicationReviewRequestTaskPayload taskPayload = 
                PermitSurrenderApplicationReviewRequestTaskPayload.builder()
        .payloadType(RequestTaskPayloadType.PERMIT_SURRENDER_APPLICATION_REVIEW_PAYLOAD)
        .reviewDetermination(PermitSurrenderReviewDeterminationGrant.builder()
                .type(PermitSurrenderReviewDeterminationType.GRANTED)
                .stopDate(LocalDate.now())
                .build())
        .reviewDecision(PermitSurrenderReviewDecision.builder()
                .type(PermitSurrenderReviewDecisionType.ACCEPTED)
                .notes("notes")
                .build())
        .build();
        
        final String processTaskId = "processTaskId";
        RequestTask requestTask = RequestTask.builder()
                .id(requestTaskId)
                .request(Request.builder().id("1").build())
                .processTaskId(processTaskId)
                .payload(taskPayload)
                .build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);
        when(decisionNotificationUsersValidator.areUsersValid(requestTask, notifyPayload.getDecisionNotification(), pmrvUser)).thenReturn(true);
        
        //invoke
        handler.process(requestTaskId, requestTaskActionType, pmrvUser, notifyPayload);

        verify(requestTaskService, times(1)).findTaskById(requestTask.getId());
        verify(reviewDeterminationHandlerService, times(1)).validateReview(taskPayload.getReviewDecision(), taskPayload.getReviewDetermination());
        verify(decisionNotificationUsersValidator, times(1)).areUsersValid(requestTask, notifyPayload.getDecisionNotification(), pmrvUser);
        verify(requestPermitSurrenderReviewService, times(1)).saveReviewDecisionNotification(requestTask, notifyPayload.getDecisionNotification(), pmrvUser);
        verify(workflowService, times(1)).completeTask(processTaskId, Map.of(
                BpmnProcessConstants.REQUEST_ID, requestTask.getRequest().getId(),
                BpmnProcessConstants.REVIEW_DETERMINATION, taskPayload.getReviewDetermination().getType(),
                BpmnProcessConstants.REVIEW_OUTCOME, ReviewOutcome.NOTIFY_OPERATOR
                ));
    }
    
    @Test
    void process_users_invalid() {
        final Long requestTaskId = 1L;
        final RequestTaskActionType requestTaskActionType = RequestTaskActionType.PERMIT_SURRENDER_NOTIFY_OPERATOR_FOR_DECISION;
        final PmrvUser pmrvUser = PmrvUser.builder().userId("user").build();
        
        final NotifyOperatorForDecisionRequestTaskActionPayload notifyPayload = NotifyOperatorForDecisionRequestTaskActionPayload.builder()
                .payloadType(RequestTaskActionPayloadType.PERMIT_SURRENDER_NOTIFY_OPERATOR_FOR_DECISION_PAYLOAD)
                .decisionNotification(DecisionNotification.builder()
                        .operators(Set.of("operator1", "operator2"))
                        .signatory("regulator")
                        .build())
                .build();
        
        final PermitSurrenderApplicationReviewRequestTaskPayload taskPayload = 
                PermitSurrenderApplicationReviewRequestTaskPayload.builder()
        .payloadType(RequestTaskPayloadType.PERMIT_SURRENDER_APPLICATION_REVIEW_PAYLOAD)
        .reviewDetermination(PermitSurrenderReviewDeterminationGrant.builder()
                .type(PermitSurrenderReviewDeterminationType.GRANTED)
                .stopDate(LocalDate.now())
                .build())
        .reviewDecision(PermitSurrenderReviewDecision.builder()
                .type(PermitSurrenderReviewDecisionType.ACCEPTED)
                .notes("notes")
                .build())
        .build();
        
        final String processTaskId = "processTaskId";
        RequestTask requestTask = RequestTask.builder()
                .id(requestTaskId)
                .request(Request.builder().id("1").build())
                .processTaskId(processTaskId)
                .payload(taskPayload)
                .build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);
        when(decisionNotificationUsersValidator.areUsersValid(requestTask, notifyPayload.getDecisionNotification(), pmrvUser)).thenReturn(false);
        
        //invoke
        BusinessException be = assertThrows(BusinessException.class, () -> handler.process(requestTaskId, requestTaskActionType, pmrvUser, notifyPayload));
        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.FORM_VALIDATION);

        verify(requestTaskService, times(1)).findTaskById(requestTask.getId());
        verify(reviewDeterminationHandlerService, times(1)).validateReview(taskPayload.getReviewDecision(), taskPayload.getReviewDetermination());
        verify(decisionNotificationUsersValidator, times(1)).areUsersValid(requestTask, notifyPayload.getDecisionNotification(), pmrvUser);
        verifyNoInteractions(requestPermitSurrenderReviewService, workflowService);
    }
    
    @Test
    void getTypes() {
        assertThat(handler.getTypes()).containsExactly(RequestTaskActionType.PERMIT_SURRENDER_NOTIFY_OPERATOR_FOR_DECISION);
    }
}
