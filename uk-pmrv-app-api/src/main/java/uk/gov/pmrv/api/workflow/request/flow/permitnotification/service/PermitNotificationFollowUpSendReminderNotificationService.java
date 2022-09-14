package uk.gov.pmrv.api.workflow.request.flow.permitnotification.service;

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
public class PermitNotificationFollowUpSendReminderNotificationService {
    
    private final RequestService requestService;
    private final RequestAccountContactQueryService requestAccountContactQueryService;
    private final RequestExpirationReminderService requestExpirationReminderService;

    public void sendFirstReminderNotification(final String requestId, final Date deadline) {
        this.sendReminderNotification(requestId, deadline, ExpirationReminderType.FIRST_REMINDER);
    }
    
    public void sendSecondReminderNotification(final String requestId, final Date deadline) {
        this.sendReminderNotification(requestId, deadline, ExpirationReminderType.SECOND_REMINDER);
    }
    
    private void sendReminderNotification(final String requestId, 
                                          final Date deadline, 
                                          final ExpirationReminderType expirationType) {
        
        final Request request = requestService.findRequestById(requestId);
        final UserInfoDTO accountPrimaryContact = requestAccountContactQueryService.getRequestAccountPrimaryContact(request);
        
        requestExpirationReminderService.sendExpirationReminderNotification(requestId, 
                NotificationTemplateExpirationReminderParams.builder()
                    .workflowTask(NotificationTemplateWorkflowTaskType.PERMIT_NOTIFICATION_FOLLOW_UP.getDescription())
                    .recipient(accountPrimaryContact)
                    .expirationTime(expirationType.getDescription())
                    .expirationTimeLong(expirationType.getDescriptionLong())
                    .deadline(deadline)
                    .build());
    }
}
