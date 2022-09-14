package uk.gov.pmrv.api.notification.message.domain;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.notification.message.domain.enumeration.SystemMessageNotificationType;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SystemMessageNotificationInfo {

    private SystemMessageNotificationType messageType;
    private Map<String, Object> messageParameters;
    private Long accountId;
    private CompetentAuthority competentAuthority;
    private Long verificationBodyId;
    private String receiver;
}
