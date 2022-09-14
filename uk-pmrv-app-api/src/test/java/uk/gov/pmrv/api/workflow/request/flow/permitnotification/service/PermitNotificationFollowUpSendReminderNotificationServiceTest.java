package uk.gov.pmrv.api.workflow.request.flow.permitnotification.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.user.core.domain.dto.UserInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.ExpirationReminderType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestAccountContactQueryService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestExpirationReminderService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.NotificationTemplateExpirationReminderParams;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.NotificationTemplateWorkflowTaskType;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderRequestPayload;

@ExtendWith(MockitoExtension.class)
class PermitNotificationFollowUpSendReminderNotificationServiceTest {

    @InjectMocks
    private PermitNotificationFollowUpSendReminderNotificationService service;

    @Mock
    private RequestService requestService;

    @Mock
    private RequestAccountContactQueryService requestAccountContactQueryService;

    @Mock
    private RequestExpirationReminderService requestExpirationReminderService;

    @Test
    void sendFirstReminderNotification() {

        final String requestId = "1";
        final Date deadline = new Date();
        final RequestType requestType = RequestType.PERMIT_NOTIFICATION;
        final Request request = Request.builder().id(requestId)
            .type(requestType)
            .payload(PermitSurrenderRequestPayload.builder().build())
            .build();
        final UserInfoDTO primaryContact =
            UserInfoDTO.builder().firstName("fn").lastName("ln").email("email@email").build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(requestAccountContactQueryService.getRequestAccountPrimaryContact(request))
            .thenReturn(primaryContact);

        service.sendFirstReminderNotification(requestId, deadline);

        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestAccountContactQueryService, times(1))
            .getRequestAccountPrimaryContact(request);
        verify(requestExpirationReminderService, times(1)).sendExpirationReminderNotification(requestId,
            NotificationTemplateExpirationReminderParams
                .builder()
                .workflowTask(NotificationTemplateWorkflowTaskType.PERMIT_NOTIFICATION_FOLLOW_UP.getDescription())
                .recipient(primaryContact)
                .expirationTime(ExpirationReminderType.FIRST_REMINDER.getDescription())
                .expirationTimeLong(ExpirationReminderType.FIRST_REMINDER.getDescriptionLong())
                .deadline(deadline)
                .build()
        );
    }

    @Test
    void sendSecondReminderNotification() {

        final String requestId = "1";
        final Date deadline = new Date();
        final RequestType requestType = RequestType.PERMIT_NOTIFICATION;
        final Request request = Request.builder().id(requestId)
            .type(requestType)
            .payload(PermitSurrenderRequestPayload.builder().build())
            .build();
        final UserInfoDTO primaryContact =
            UserInfoDTO.builder().firstName("fn").lastName("ln").email("email@email").build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(requestAccountContactQueryService.getRequestAccountPrimaryContact(request))
            .thenReturn(primaryContact);

        service.sendSecondReminderNotification(requestId, deadline);

        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestAccountContactQueryService, times(1))
            .getRequestAccountPrimaryContact(request);
        verify(requestExpirationReminderService, times(1)).sendExpirationReminderNotification(requestId,
            NotificationTemplateExpirationReminderParams
                .builder()
                .workflowTask(NotificationTemplateWorkflowTaskType.PERMIT_NOTIFICATION_FOLLOW_UP.getDescription())
                .recipient(primaryContact)
                .expirationTime(ExpirationReminderType.SECOND_REMINDER.getDescription())
                .expirationTimeLong(ExpirationReminderType.SECOND_REMINDER.getDescriptionLong())
                .deadline(deadline)
                .build()
        );
    }
}
