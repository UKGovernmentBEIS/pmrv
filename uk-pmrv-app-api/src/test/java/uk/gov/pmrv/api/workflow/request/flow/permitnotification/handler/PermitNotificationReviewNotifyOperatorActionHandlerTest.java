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
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.NotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.ReviewOutcome;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.permit.DeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.FollowUp;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.service.PermitNotificationValidatorService;

import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PermitNotificationReviewNotifyOperatorActionHandlerTest {

    @InjectMocks
    private PermitNotificationReviewNotifyOperatorActionHandler handler;

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
                        .payloadType(RequestTaskActionPayloadType.PERMIT_NOTIFICATION_NOTIFY_OPERATOR_FOR_DECISION_PAYLOAD)
                        .decisionNotification(decisionNotification)
                        .build();

        final PmrvUser pmrvUser = PmrvUser.builder().userId("userId").build();
        final String processTaskId = "processTaskId";
        final PermitNotificationReviewDecision reviewDecision = PermitNotificationReviewDecision.builder()
                .type(PermitNotificationReviewDecisionType.ACCEPTED)
                .officialNotice("official notice")
                .followUp(FollowUp.builder().followUpResponseRequired(false).build())
                .notes("notes")
                .build();
        final PermitNotificationApplicationReviewRequestTaskPayload taskPayload =
                PermitNotificationApplicationReviewRequestTaskPayload.builder()
                        .reviewDecision(reviewDecision)
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
        handler.process(requestTask.getId(), RequestTaskActionType.PERMIT_NOTIFICATION_NOTIFY_OPERATOR_FOR_DECISION,
                pmrvUser, taskActionPayload);

        // Verify
        verify(requestTaskService, times(1)).findTaskById(requestTask.getId());
        verify(validator, times(1)).validateNotificationReviewDecision(reviewDecision);
        verify(validator, times(1)).validateNotifyUsers(requestTask, decisionNotification, pmrvUser);
        verify(workflowService, times(1)).completeTask(processTaskId,
                Map.of(BpmnProcessConstants.REQUEST_ID, requestId,
                        BpmnProcessConstants.REVIEW_DETERMINATION, DeterminationType.GRANTED,
                        BpmnProcessConstants.REVIEW_OUTCOME, ReviewOutcome.NOTIFY_OPERATOR));

        final PermitNotificationRequestPayload requestPayload = (PermitNotificationRequestPayload) requestTask.getRequest().getPayload();
        assertThat(requestPayload.getReviewDecision()).isEqualTo(reviewDecision);
        assertThat(requestPayload.getReviewDecisionNotification()).isEqualTo(decisionNotification);
        assertThat(requestPayload.getRegulatorReviewer()).isEqualTo(pmrvUser.getUserId());
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes()).containsExactly(RequestTaskActionType.PERMIT_NOTIFICATION_NOTIFY_OPERATOR_FOR_DECISION);
    }
}
