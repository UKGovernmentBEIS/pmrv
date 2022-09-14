package uk.gov.pmrv.api.notification.mail.domain;

import java.util.HashMap;
import java.util.Map;

import lombok.Builder;
import lombok.Data;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.notification.template.domain.enumeration.NotificationTemplateName;

@Data
@Builder
public class EmailNotificationTemplateData {

    private CompetentAuthority competentAuthority;
    
    private NotificationTemplateName templateName;
    
    @Builder.Default
    private Map<String, Object> templateParams = new HashMap<>();
    
}
