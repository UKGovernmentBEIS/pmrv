package uk.gov.pmrv.api.notification.message.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.notification.message.domain.SystemMessageNotificationInfo;
import uk.gov.pmrv.api.notification.template.domain.NotificationContent;
import uk.gov.pmrv.api.notification.template.domain.enumeration.NotificationTemplateName;
import uk.gov.pmrv.api.notification.template.service.NotificationTemplateProcessService;

@Log4j2
@RequiredArgsConstructor
@Service
public class SystemMessageNotificationService {
    private final SystemMessageNotificationSendService systemMessageNotificationSendService;
    private final NotificationTemplateProcessService notificationTemplateProcessService;

    @Transactional
    public void generateAndSendNotificationSystemMessage(SystemMessageNotificationInfo msgInfo) {
        NotificationContent notificationContent = createNotificationSystemMessageContent(msgInfo);
        systemMessageNotificationSendService.sendNotificationSystemMessage(msgInfo, notificationContent);
    }

    private NotificationContent createNotificationSystemMessageContent(SystemMessageNotificationInfo msgInfo) {
        NotificationTemplateName templateName = msgInfo.getMessageType().getNotificationTemplateName();
        log.debug("Generating system message using template {}", () -> templateName);

        return notificationTemplateProcessService
                .processMessageNotificationTemplate(templateName, msgInfo.getMessageParameters());
    }
}
