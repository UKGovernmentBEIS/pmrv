package uk.gov.pmrv.api.workflow.request.flow.notificationsystemmessage.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.notification.template.domain.NotificationContent;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemMessageNotificationRequestParams {

    private RequestTaskType requestTaskType;

    private Long accountId;

    private CompetentAuthority competentAuthority;
    
    private Long verificationBodyId;

    private String notificationMessageRecipient;

    private NotificationContent notificationContent;
}
