package uk.gov.pmrv.api.workflow.request.flow.permitnotification.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.NotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.ReviewOutcome;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationFollowUpApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationFollowUpReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationFollowUpReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.service.PermitNotificationValidatorService;

@ExtendWith(MockitoExtension.class)
class PermitNotificationFollowUpReviewNotifyOperatorActionHandlerTest {

    @InjectMocks
    private PermitNotificationFollowUpReviewNotifyOperatorActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private PermitNotificationValidatorService validator;

    @Mock
    private WorkflowService workflowService;

    @Test
    void process() {
        
        final DecisionNotification decisionNotification = DecisionNotification.builder()
                .operators(Set.of("operator"))
                .signatory("regulator")
                .build();
        final NotifyOperatorForDecisionRequestTaskActionPayload taskActionPayload =
                NotifyOperatorForDecisionRequestTaskActionPayload.builder()
                        .payloadType(RequestTaskActionPayloadType.PERMIT_NOTIFICATION_FOLLOW_UP_NOTIFY_OPERATOR_FOR_DECISION_PAYLOAD)
                        .decisionNotification(decisionNotification)
                        .build();
        final PmrvUser pmrvUser = PmrvUser.builder().userId("userId").build();
        final String processTaskId = "processTaskId";
        final PermitNotificationFollowUpReviewDecision reviewDecision = PermitNotificationFollowUpReviewDecision.builder()
                .type(PermitNotificationFollowUpReviewDecisionType.ACCEPTED)
                .notes("notes")
                .build();
        final PermitNotificationFollowUpApplicationReviewRequestTaskPayload taskPayload =
            PermitNotificationFollowUpApplicationReviewRequestTaskPayload.builder()
                        .reviewDecision(reviewDecision)
                        .submissionDate(LocalDate.of(2023, 1, 1))
                        .build();
        final String requestId = "requestId";
        final RequestTask requestTask = RequestTask.builder()
                .id(1L)
                .processTaskId(processTaskId)
                .payload(taskPayload)
                .request(Request.builder().id(requestId).payload(PermitNotificationRequestPayload.builder().build()).build())
                .build();

        when(requestTaskService.findTaskById(1L)).thenReturn(requestTask);

        // Invoke
        handler.process(requestTask.getId(), 
            RequestTaskActionType.PERMIT_NOTIFICATION_FOLLOW_UP_NOTIFY_OPERATOR_FOR_DECISION,
            pmrvUser, 
            taskActionPayload);

        // Verify
        verify(requestTaskService, times(1)).findTaskById(requestTask.getId());
        verify(validator, times(1)).validateNotificationFollowUpReviewDecision(reviewDecision);
        verify(validator, times(1)).validateNotifyUsers(requestTask, decisionNotification, pmrvUser);
        verify(workflowService, times(1)).completeTask(processTaskId,
                Map.of(BpmnProcessConstants.REVIEW_OUTCOME, ReviewOutcome.NOTIFY_OPERATOR));

        final PermitNotificationRequestPayload requestPayload = (PermitNotificationRequestPayload) requestTask.getRequest().getPayload();
        assertThat(requestPayload.getFollowUpResponseSubmissionDate()).isEqualTo(LocalDate.of(2023, 1, 1));
        assertThat(requestPayload.getFollowUpReviewDecision()).isEqualTo(reviewDecision);
        assertThat(requestPayload.getFollowUpReviewDecisionNotification()).isEqualTo(decisionNotification);
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes()).containsExactly(RequestTaskActionType.PERMIT_NOTIFICATION_FOLLOW_UP_NOTIFY_OPERATOR_FOR_DECISION);
    }
}
