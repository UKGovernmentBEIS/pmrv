package uk.gov.pmrv.api.workflow.request.flow.common.service;

import java.util.Date;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.user.core.domain.dto.UserInfoDTO;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.SubRequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.ExpirationReminderType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.NotificationTemplateExpirationReminderParams;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.NotificationTemplateWorkflowTaskType;

@Service
@RequiredArgsConstructor
public class ApplicationReviewSendReminderNotificationService {
    
    private final RequestService requestService;
    private final UserAuthService userAuthService;
    private final RequestExpirationReminderService requestExpirationReminderService;

    public void sendFirstReminderNotification(String requestId, Date deadline) {
        sendReminderNotification(requestId, deadline, ExpirationReminderType.FIRST_REMINDER);
    }
    
    public void sendSecondReminderNotification(String requestId, Date deadline) {
        sendReminderNotification(requestId, deadline, ExpirationReminderType.SECOND_REMINDER);
    }
    
    private void sendReminderNotification(String requestId, Date deadline, ExpirationReminderType expirationType) {
        final Request request = requestService.findRequestById(requestId);
        final String regulatorAssignee = request.getPayload().getRegulatorAssignee();
        if(regulatorAssignee == null) {
            return;
        }
        
        UserInfoDTO regulatorAssigneeUser = userAuthService.getUserByUserId(regulatorAssignee);
        
        requestExpirationReminderService.sendExpirationReminderNotification(requestId, 
                NotificationTemplateExpirationReminderParams.builder()
                        .workflowTask(NotificationTemplateWorkflowTaskType
                                .fromString(request.getType().name() + "_" + SubRequestType.APPLICATION_REVIEW.name())
                                .getDescription())
                    .recipient(regulatorAssigneeUser)
                    .expirationTime(expirationType.getDescription())
                    .expirationTimeLong(expirationType.getDescriptionLong())
                    .deadline(deadline)
                    .build());
    }
}
