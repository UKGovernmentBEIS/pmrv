package uk.gov.pmrv.api.workflow.request.flow.common.service.notification;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.user.core.domain.dto.UserInfoDTO;

@Data
@EqualsAndHashCode
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationTemplateExpirationReminderParams {

    private String workflowTask;
    
    private UserInfoDTO recipient;
    
    private String expirationTime;
    private String expirationTimeLong;
    private Date deadline;
    
}
