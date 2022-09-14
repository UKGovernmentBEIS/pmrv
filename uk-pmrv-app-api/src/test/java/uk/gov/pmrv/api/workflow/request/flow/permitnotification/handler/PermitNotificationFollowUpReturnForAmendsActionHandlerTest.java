package uk.gov.pmrv.api.workflow.request.flow.permitnotification.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.SubRequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestTaskActionEmptyPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.ReviewOutcome;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestExpirationVarsBuilder;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationFollowUpApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationFollowUpReturnedForAmendsRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationFollowUpReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationFollowUpReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.service.PermitNotificationValidatorService;

@ExtendWith(MockitoExtension.class)
class PermitNotificationFollowUpReturnForAmendsActionHandlerTest {

    @InjectMocks
    private PermitNotificationFollowUpReturnForAmendsActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private RequestService requestService;

    @Mock
    private PermitNotificationValidatorService validator;

    @Mock
    private WorkflowService workflowService;
    
    @Mock
    private RequestExpirationVarsBuilder varsBuilder;

    @Test
    void process_whenDueDateExists_thenUseIt() {

        final PmrvUser pmrvUser = PmrvUser.builder().userId("userId").build();
        final String processTaskId = "processTaskId";
        final UUID file = UUID.randomUUID();
        final PermitNotificationFollowUpReviewDecision reviewDecision = PermitNotificationFollowUpReviewDecision.builder()
                .type(PermitNotificationFollowUpReviewDecisionType.AMENDS_NEEDED)
                .notes("notes")
                .changesRequired("changes required")
                .files(Set.of(file))
                .dueDate(LocalDate.of(2023, 3, 3))
                .build();
        final PermitNotificationFollowUpApplicationReviewRequestTaskPayload taskPayload =
            PermitNotificationFollowUpApplicationReviewRequestTaskPayload.builder()
                        .reviewDecision(reviewDecision)
                        .followUpResponseExpirationDate(LocalDate.of(2023, 2, 2))
                        .submissionDate(LocalDate.of(2023, 1, 1))
                        .followUpAttachments(Map.of(file, "filename"))
                        .reviewSectionsCompleted(Map.of("section", true))
                        .build();
        final String requestId = "requestId";
        final Request request = Request.builder().id(requestId).payload(PermitNotificationRequestPayload.builder().build()).build();
        final RequestTask requestTask = RequestTask.builder()
                .id(1L)
                .processTaskId(processTaskId)
                .payload(taskPayload)
                .request(request)
                .build();
        final Date dueDate = Date.from(LocalDate.of(2023, 3, 3)
            .atTime(LocalTime.MIN)
            .atZone(ZoneId.systemDefault())
            .toInstant());
        final Date firstReminderDate = Date.from(LocalDate.of(2023, 1, 3)
            .atTime(LocalTime.MIN)
            .atZone(ZoneId.systemDefault())
            .toInstant());
        final Date secondReminderDate = Date.from(LocalDate.of(2023, 2, 3)
            .atTime(LocalTime.MIN)
            .atZone(ZoneId.systemDefault())
            .toInstant());

        when(requestTaskService.findTaskById(1L)).thenReturn(requestTask);
        when(varsBuilder.buildExpirationVars(SubRequestType.FOLLOW_UP_RESPONSE, dueDate)).thenReturn(Map.of(
            BpmnProcessConstants.FOLLOW_UP_RESPONSE_EXPIRATION_DATE, dueDate,
            BpmnProcessConstants.FOLLOW_UP_RESPONSE_FIRST_REMINDER_DATE, firstReminderDate,
            BpmnProcessConstants.FOLLOW_UP_RESPONSE_SECOND_REMINDER_DATE, secondReminderDate
        ));

        // Invoke
        handler.process(requestTask.getId(), 
            RequestTaskActionType.PERMIT_NOTIFICATION_FOLLOW_UP_RETURN_FOR_AMENDS,
            pmrvUser,
            RequestTaskActionEmptyPayload.builder().build());

        // Verify
        verify(requestTaskService, times(1)).findTaskById(requestTask.getId());
        verify(validator, times(1)).validateReturnForAmends(reviewDecision);
        verify(requestService, times(1)).addActionToRequest(request,
            PermitNotificationFollowUpReturnedForAmendsRequestActionPayload.builder()
                .payloadType(RequestActionPayloadType.PERMIT_NOTIFICATION_FOLLOW_UP_RETURNED_FOR_AMENDS_PAYLOAD)
                .notes(reviewDecision.getNotes())
                .changesRequired(reviewDecision.getChangesRequired())
                .dueDate(reviewDecision.getDueDate())
                .amendFiles(Set.of(file))
                .amendAttachments(Map.of(file, "filename"))
                .build(),
            RequestActionType.PERMIT_NOTIFICATION_FOLLOW_UP_RETURNED_FOR_AMENDS,
            "userId");
        verify(workflowService, times(1)).completeTask(processTaskId,
                Map.of(BpmnProcessConstants.REVIEW_OUTCOME, ReviewOutcome.AMENDS_NEEDED,
                       BpmnProcessConstants.APPLICATION_REVIEW_EXPIRATION_DATE, dueDate,
                       BpmnProcessConstants.FOLLOW_UP_RESPONSE_EXPIRATION_DATE, dueDate,
                       BpmnProcessConstants.FOLLOW_UP_RESPONSE_FIRST_REMINDER_DATE, firstReminderDate,
                       BpmnProcessConstants.FOLLOW_UP_RESPONSE_SECOND_REMINDER_DATE, secondReminderDate));

        final PermitNotificationRequestPayload requestPayload = (PermitNotificationRequestPayload) requestTask.getRequest().getPayload();
        assertThat(requestPayload.getFollowUpReviewDecision()).isEqualTo(reviewDecision);
        assertThat(requestPayload.getFollowUpResponseAttachments()).isEqualTo(Map.of(file, "filename"));
        assertThat(requestPayload.getFollowUpReviewSectionsCompleted()).isEqualTo(Map.of("section", true));
    }

    @Test
    void process_whenDueDateNotExists_thenUseExpirationDate() {

        final PmrvUser pmrvUser = PmrvUser.builder().userId("userId").build();
        final String processTaskId = "processTaskId";
        final UUID file = UUID.randomUUID();
        final PermitNotificationFollowUpReviewDecision reviewDecision = PermitNotificationFollowUpReviewDecision.builder()
            .type(PermitNotificationFollowUpReviewDecisionType.AMENDS_NEEDED)
            .notes("notes")
            .changesRequired("changes required")
            .files(Set.of(file))
            .build();
        final PermitNotificationFollowUpApplicationReviewRequestTaskPayload taskPayload =
            PermitNotificationFollowUpApplicationReviewRequestTaskPayload.builder()
                .reviewDecision(reviewDecision)
                .followUpResponseExpirationDate(LocalDate.of(2023, 2, 2))
                .submissionDate(LocalDate.of(2023, 1, 1))
                .followUpAttachments(Map.of(file, "filename"))
                .reviewSectionsCompleted(Map.of("section", true))
                .build();
        final String requestId = "requestId";
        final Request request = Request.builder().id(requestId).payload(PermitNotificationRequestPayload.builder().build()).build();
        final RequestTask requestTask = RequestTask.builder()
            .id(1L)
            .processTaskId(processTaskId)
            .payload(taskPayload)
            .request(request)
            .build();
        final Date dueDate = Date.from(LocalDate.of(2023, 2, 2)
            .atTime(LocalTime.MIN)
            .atZone(ZoneId.systemDefault())
            .toInstant());
        final Date firstReminderDate = Date.from(LocalDate.of(2022, 12, 12)
            .atTime(LocalTime.MIN)
            .atZone(ZoneId.systemDefault())
            .toInstant());
        final Date secondReminderDate = Date.from(LocalDate.of(2023, 1, 3)
            .atTime(LocalTime.MIN)
            .atZone(ZoneId.systemDefault())
            .toInstant());

        when(requestTaskService.findTaskById(1L)).thenReturn(requestTask);
        when(varsBuilder.buildExpirationVars(SubRequestType.FOLLOW_UP_RESPONSE, dueDate)).thenReturn(Map.of(
            BpmnProcessConstants.FOLLOW_UP_RESPONSE_EXPIRATION_DATE, dueDate,
            BpmnProcessConstants.FOLLOW_UP_RESPONSE_FIRST_REMINDER_DATE, firstReminderDate,
            BpmnProcessConstants.FOLLOW_UP_RESPONSE_SECOND_REMINDER_DATE, secondReminderDate
        ));

        // Invoke
        handler.process(requestTask.getId(),
            RequestTaskActionType.PERMIT_NOTIFICATION_FOLLOW_UP_RETURN_FOR_AMENDS,
            pmrvUser,
            RequestTaskActionEmptyPayload.builder().build());

        // Verify
        verify(requestTaskService, times(1)).findTaskById(requestTask.getId());
        verify(validator, times(1)).validateReturnForAmends(reviewDecision);
        verify(requestService, times(1)).addActionToRequest(request,
            PermitNotificationFollowUpReturnedForAmendsRequestActionPayload.builder()
                .payloadType(RequestActionPayloadType.PERMIT_NOTIFICATION_FOLLOW_UP_RETURNED_FOR_AMENDS_PAYLOAD)
                .notes(reviewDecision.getNotes())
                .changesRequired(reviewDecision.getChangesRequired())
                .dueDate(taskPayload.getFollowUpResponseExpirationDate())
                .amendFiles(Set.of(file))
                .amendAttachments(Map.of(file, "filename"))
                .build(),
            RequestActionType.PERMIT_NOTIFICATION_FOLLOW_UP_RETURNED_FOR_AMENDS,
            "userId");
        verify(workflowService, times(1)).completeTask(processTaskId,
            Map.of(BpmnProcessConstants.REVIEW_OUTCOME, ReviewOutcome.AMENDS_NEEDED,
                BpmnProcessConstants.APPLICATION_REVIEW_EXPIRATION_DATE, dueDate,
                BpmnProcessConstants.FOLLOW_UP_RESPONSE_EXPIRATION_DATE, dueDate,
                BpmnProcessConstants.FOLLOW_UP_RESPONSE_FIRST_REMINDER_DATE, firstReminderDate,
                BpmnProcessConstants.FOLLOW_UP_RESPONSE_SECOND_REMINDER_DATE, secondReminderDate));

        final PermitNotificationRequestPayload requestPayload = (PermitNotificationRequestPayload) requestTask.getRequest().getPayload();
        assertThat(requestPayload.getFollowUpReviewDecision()).isEqualTo(reviewDecision);
        assertThat(requestPayload.getFollowUpResponseAttachments()).isEqualTo(Map.of(file, "filename"));
        assertThat(requestPayload.getFollowUpReviewSectionsCompleted()).isEqualTo(Map.of("section", true));
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes()).containsExactly(RequestTaskActionType.PERMIT_NOTIFICATION_FOLLOW_UP_RETURN_FOR_AMENDS);
    }
}
