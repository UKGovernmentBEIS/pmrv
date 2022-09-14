package uk.gov.pmrv.api.workflow.request.flow.permitnotification.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestTaskTimeManagementService;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.FollowUp;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationFollowUpApplicationAmendsSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationFollowUpRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationFollowUpReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationFollowUpWaitForAmendsRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationWaitForFollowUpRequestTaskPayload;

@ExtendWith(MockitoExtension.class)
class ExtendFollowUpExpirationTimerServiceTest {

    @InjectMocks
    private ExtendFollowUpExpirationTimerService service;

    @Mock
    private RequestService requestService;

    @Mock
    private RequestTaskTimeManagementService requestTaskTimeManagementService;

    @Test
    void extendTimer_duringWaitForFollowUp() {

        final String requestId = "1";
        final String assignee = "regulator";
        final LocalDate previousDueDate = LocalDate.of(2023, 1, 1);
        final PermitNotificationRequestPayload payload = PermitNotificationRequestPayload.builder()
            .reviewDecision(PermitNotificationReviewDecision.builder()
                .followUp(FollowUp.builder().followUpResponseExpirationDate(previousDueDate).build()).build())
            .regulatorAssignee(assignee)
            .build();
        final Request request = Request.builder()
            .id(requestId)
            .type(RequestType.PERMIT_NOTIFICATION)
            .payload(payload)
            .build();
        final RequestTask followUpTask = RequestTask.builder()
            .payload(PermitNotificationFollowUpRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.PERMIT_NOTIFICATION_FOLLOW_UP_PAYLOAD)
                .followUpResponseExpirationDate(previousDueDate)
                .build())
            .build();
        final RequestTask waitTask = RequestTask.builder()
            .payload(PermitNotificationWaitForFollowUpRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.PERMIT_NOTIFICATION_WAIT_FOR_FOLLOW_UP_PAYLOAD)
                .followUpResponseExpirationDate(previousDueDate)
                .build())
            .build();

        final Date expirationDate = new GregorianCalendar(2023, Calendar.FEBRUARY, 1).getTime();
        final LocalDate dueDate = LocalDate.of(2023, 2, 1);

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(requestTaskTimeManagementService.setDueDateToTasks("1",
            BpmnProcessConstants.FOLLOW_UP_RESPONSE_EXPIRATION_DATE,
            dueDate)).thenReturn(List.of(followUpTask, waitTask));

        // Invoke
        service.extendTimer(requestId, expirationDate, BpmnProcessConstants.FOLLOW_UP_RESPONSE_EXPIRATION_DATE);

        // Verify
        assertThat(payload.getReviewDecision().getFollowUp().getFollowUpResponseExpirationDate()).isEqualTo(dueDate);
        assertThat(((PermitNotificationFollowUpRequestTaskPayload) followUpTask.getPayload()).getFollowUpResponseExpirationDate()).isEqualTo(dueDate);
        assertThat(((PermitNotificationWaitForFollowUpRequestTaskPayload) waitTask.getPayload()).getFollowUpResponseExpirationDate()).isEqualTo(dueDate);

        verify(requestService, times(1)).addActionToRequest(
            request,
            null,
            RequestActionType.PERMIT_NOTIFICATION_FOLLOW_UP_DATE_EXTENDED,
            "regulator");
    }

    @Test
    void extendTimer_duringWaitForAmend() {

        final String requestId = "1";
        final String assignee = "regulator";
        final LocalDate previousDueDate = LocalDate.of(2023, 1, 1);
        final PermitNotificationRequestPayload payload = PermitNotificationRequestPayload.builder()
            .followUpReviewDecision(PermitNotificationFollowUpReviewDecision.builder().dueDate(previousDueDate).build())
            .regulatorAssignee(assignee)
            .build();
        final Request request = Request.builder()
            .id(requestId)
            .type(RequestType.PERMIT_NOTIFICATION)
            .payload(payload)
            .build();
        final RequestTask followUpTask = RequestTask.builder()
            .payload(PermitNotificationFollowUpApplicationAmendsSubmitRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.PERMIT_NOTIFICATION_FOLLOW_UP_APPLICATION_AMENDS_SUBMIT_PAYLOAD)
                .reviewDecision(PermitNotificationFollowUpReviewDecision.builder().dueDate(previousDueDate).build())
                .build())
            .build();
        final RequestTask waitTask = RequestTask.builder()
            .payload(PermitNotificationFollowUpWaitForAmendsRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.PERMIT_NOTIFICATION_FOLLOW_UP_WAIT_FOR_AMENDS_PAYLOAD)
                .reviewDecision(PermitNotificationFollowUpReviewDecision.builder().dueDate(previousDueDate).build())
                .build())
            .build();

        final Date expirationDate = new GregorianCalendar(2023, Calendar.FEBRUARY, 1).getTime();
        final LocalDate dueDate = LocalDate.of(2023, 2, 1);

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(requestTaskTimeManagementService.setDueDateToTasks("1",
            BpmnProcessConstants.APPLICATION_REVIEW_EXPIRATION_DATE,
            dueDate)).thenReturn(List.of(followUpTask, waitTask));

        // Invoke
        service.extendTimer(requestId, expirationDate, BpmnProcessConstants.APPLICATION_REVIEW_EXPIRATION_DATE);

        // Verify
        assertThat(payload.getFollowUpReviewDecision().getDueDate()).isEqualTo(dueDate);
        assertThat(((PermitNotificationFollowUpApplicationAmendsSubmitRequestTaskPayload) followUpTask.getPayload()).getReviewDecision().getDueDate()).isEqualTo(dueDate);
        assertThat(((PermitNotificationFollowUpWaitForAmendsRequestTaskPayload) waitTask.getPayload()).getReviewDecision().getDueDate()).isEqualTo(dueDate);

        verify(requestService, times(1)).addActionToRequest(
            request,
            null,
            RequestActionType.PERMIT_NOTIFICATION_FOLLOW_UP_DATE_EXTENDED,
            "regulator");
    }
}
