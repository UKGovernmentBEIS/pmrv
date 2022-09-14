package uk.gov.pmrv.api.notification.message.service;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.notification.message.domain.SystemMessageNotificationInfo;
import uk.gov.pmrv.api.notification.template.domain.NotificationContent;

@Service
public interface SystemMessageNotificationSendService {
    void sendNotificationSystemMessage(SystemMessageNotificationInfo msgInfo, NotificationContent notificationContent);
}
