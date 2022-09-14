package uk.gov.pmrv.api.workflow.request.flow.rfi.service;

import java.util.Date;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.user.core.domain.dto.UserInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.ExpirationReminderType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestAccountContactQueryService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestExpirationReminderService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.NotificationTemplateExpirationReminderParams;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.NotificationTemplateWorkflowTaskType;

@Service
@RequiredArgsConstructor
public class RfiSendReminderNotificationService {

    private final RequestService requestService;
    private final RequestAccountContactQueryService requestAccountContactQueryService;
    private final RequestExpirationReminderService requestExpirationReminderService;

    public void sendFirstReminderNotification(String requestId, Date expirationDate) {
        sendReminderNotification(requestId, expirationDate, ExpirationReminderType.FIRST_REMINDER);
    }

    public void sendSecondReminderNotification(String requestId, Date expirationDate) {
        sendReminderNotification(requestId, expirationDate, ExpirationReminderType.SECOND_REMINDER);
    }
    
    private void sendReminderNotification(String requestId, Date expirationDate, ExpirationReminderType expirationReminderType) {
        final Request request = requestService.findRequestById(requestId);
        
        final UserInfoDTO accountPrimaryContact = requestAccountContactQueryService.getRequestAccountPrimaryContact(request);        
        requestExpirationReminderService.sendExpirationReminderNotification(requestId, 
                NotificationTemplateExpirationReminderParams.builder()
                    .workflowTask(NotificationTemplateWorkflowTaskType.RFI.getDescription())
                    .recipient(accountPrimaryContact)
                    .expirationTime(expirationReminderType.getDescription())
                    .expirationTimeLong(expirationReminderType.getDescriptionLong())
                    .deadline(expirationDate)
                    .build());
    }
}
