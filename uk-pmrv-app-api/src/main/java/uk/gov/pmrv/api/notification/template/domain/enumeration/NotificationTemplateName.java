package uk.gov.pmrv.api.notification.template.domain.enumeration;

import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enumerates the various notification template names.
 */
@Getter
@AllArgsConstructor
public enum NotificationTemplateName {

    //Email Notification Template Names
    EMAIL_CONFIRMATION("EmailConfirmation"),
    USER_ACCOUNT_CREATED("UserAccountCreated"),
    USER_ACCOUNT_ACTIVATION("UserAccountActivation"),
    ACCOUNT_APPLICATION_RECEIVED("AccountApplicationReceived"),
    ACCOUNT_APPLICATION_ACCEPTED("AccountApplicationAccepted"),
    ACCOUNT_APPLICATION_REJECTED("AccountApplicationRejected"),
    INVITATION_TO_OPERATOR_ACCOUNT("InvitationToOperatorAccount"),
    INVITATION_TO_REGULATOR_ACCOUNT("InvitationToRegulatorAccount"),
    INVITATION_TO_VERIFIER_ACCOUNT("InvitationToVerifierAccount"),
    INVITATION_TO_EMITTER_CONTACT("InvitationToEmitterContact"),
    INVITEE_INVITATION_ACCEPTED("InviteeInvitationAccepted"),
    INVITER_INVITATION_ACCEPTED("InviterInvitationAccepted"),
    CHANGE_2FA("Change2FA"),
    
    GENERIC_INSTALLATION_LETTER("Generic email Template for Installation letter"),
    GENERIC_EXPIRATION_REMINDER("Generic Expiration Reminder Template"),
    
    //System Message Notification Template Names
    ACCOUNT_USERS_SETUP("AccountUsersSetup"),
    NEW_VERIFICATION_BODY_INSTALLATION("NewVerificationBodyInstallation"),
    VERIFIER_NO_LONGER_AVAILABLE("VerifierNoLongerAvailable"),

    PERMIT_NOTIFICATION_OPERATOR_RESPONSE("Permit Notification Operator Response"),

    USER_FEEDBACK("UserFeedbackForService");

    /** The name. */
    private final String name;

    /** Maps keys representing values of name property to NotificationTemplate values . */
    private static final Map<String, NotificationTemplateName> BY_NAME = new HashMap<>();

    static {
        for (NotificationTemplateName e : values()) {
            BY_NAME.put(e.name, e);
        }
    }

    /**
     * Retrieves NotificationTemplateName value from the provided name.
     * @param name name attribute
     * @return NotificationTemplateName value
     */
    public static NotificationTemplateName getEnumValueFromName(String name) {
        return BY_NAME.get(name);
    }
}
