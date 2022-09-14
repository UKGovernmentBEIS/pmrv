package uk.gov.pmrv.api.notification.message.domain.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import uk.gov.pmrv.api.notification.template.domain.enumeration.NotificationTemplateName;

@Getter
@AllArgsConstructor
public enum SystemMessageNotificationType {
    ACCOUNT_USERS_SETUP(NotificationTemplateName.ACCOUNT_USERS_SETUP),
    NEW_VERIFICATION_BODY_INSTALLATION(NotificationTemplateName.NEW_VERIFICATION_BODY_INSTALLATION),
    VERIFIER_NO_LONGER_AVAILABLE(NotificationTemplateName.VERIFIER_NO_LONGER_AVAILABLE);

    private final NotificationTemplateName notificationTemplateName;
}
