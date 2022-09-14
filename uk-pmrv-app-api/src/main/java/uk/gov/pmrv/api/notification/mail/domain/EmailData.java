package uk.gov.pmrv.api.notification.mail.domain;

import java.util.HashMap;
import java.util.Map;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmailData {
    
    private EmailNotificationTemplateData notificationTemplateData;
    
    @Builder.Default
    private Map<String, byte[]> attachments = new HashMap<>();
    
}
