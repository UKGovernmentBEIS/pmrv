package uk.gov.pmrv.api.user.regulator.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.notification.mail.constants.EmailNotificationTemplateConstants;
import uk.gov.pmrv.api.notification.mail.domain.EmailNotificationTemplateData;
import uk.gov.pmrv.api.notification.mail.domain.EmailData;
import uk.gov.pmrv.api.notification.mail.service.NotificationEmailService;
import uk.gov.pmrv.api.notification.template.domain.enumeration.NotificationTemplateName;
import uk.gov.pmrv.api.user.JwtTokenActionEnum;
import uk.gov.pmrv.api.user.NavigationOutcomes;
import uk.gov.pmrv.api.user.core.config.JwtProperties;
import uk.gov.pmrv.api.user.core.domain.dto.UserInfoDTO;
import uk.gov.pmrv.api.user.core.domain.model.UserNotificationWithRedirectionLinkInfo;
import uk.gov.pmrv.api.user.core.service.UserNotificationService;
import uk.gov.pmrv.api.user.regulator.domain.RegulatorInvitedUserDetailsDTO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static uk.gov.pmrv.api.notification.mail.constants.EmailNotificationTemplateConstants.USER_ACCOUNT_CREATED_USER_FNAME;
import static uk.gov.pmrv.api.notification.mail.constants.EmailNotificationTemplateConstants.USER_ACCOUNT_CREATED_USER_LNAME;
import static uk.gov.pmrv.api.notification.mail.constants.EmailNotificationTemplateConstants.USER_INVITEE_FNAME;
import static uk.gov.pmrv.api.notification.mail.constants.EmailNotificationTemplateConstants.USER_INVITEE_LNAME;
import static uk.gov.pmrv.api.notification.mail.constants.EmailNotificationTemplateConstants.USER_ROLE_TYPE;
import static uk.gov.pmrv.api.notification.template.domain.enumeration.NotificationTemplateName.INVITEE_INVITATION_ACCEPTED;
import static uk.gov.pmrv.api.notification.template.domain.enumeration.NotificationTemplateName.INVITER_INVITATION_ACCEPTED;

@Log4j2
@Service
@RequiredArgsConstructor
public class RegulatorUserNotificationGateway {

    private final UserNotificationService userNotificationService;
    private final NotificationEmailService notificationEmailService;
    private final JwtProperties jwtProperties;

    public void notifyInvitedUser(RegulatorInvitedUserDetailsDTO invitedUserDetails, String authorityUuid) {
        long expirationInMinutes = jwtProperties.getClaim().getUserInvitationExpIntervalMinutes();

        Map<String, Object> notificationParams = new HashMap<>(Map.of(
                EmailNotificationTemplateConstants.APPLICANT_FNAME, invitedUserDetails.getFirstName(),
                EmailNotificationTemplateConstants.APPLICANT_LNAME, invitedUserDetails.getLastName(),
                EmailNotificationTemplateConstants.EXPIRATION_MINUTES, expirationInMinutes
        ));

        userNotificationService.notifyUser(
                UserNotificationWithRedirectionLinkInfo.builder()
                        .templateName(NotificationTemplateName.INVITATION_TO_REGULATOR_ACCOUNT)
                        .userEmail(invitedUserDetails.getEmail())
                        .notificationParams(notificationParams)
                        .linkParamName(EmailNotificationTemplateConstants.REGULATOR_INVITATION_CONFIRMATION_LINK)
                        .linkPath(NavigationOutcomes.REGULATOR_REGISTRATION_INVITATION_ACCEPTED_URL)
                        .tokenParams(UserNotificationWithRedirectionLinkInfo.TokenParams.builder()
                                .jwtTokenAction(JwtTokenActionEnum.REGULATOR_INVITATION)
                                .claimValue(authorityUuid)
                                .expirationInterval(expirationInMinutes)
                                .build()
                        )
                        .build()
        );
    }

    public void notifyInviteeAcceptedInvitation(final String email) {

        final EmailData inviteeInfo = EmailData.builder()
            .notificationTemplateData(EmailNotificationTemplateData.builder()
                    .templateName(INVITEE_INVITATION_ACCEPTED)
                    .templateParams(Map.of(USER_ROLE_TYPE, "Regulator"))
                    .build())
            .build();

        notificationEmailService.notifyRecipient(inviteeInfo, email);
    }

    public void notifyInviterAcceptedInvitation(UserInfoDTO invitee, UserInfoDTO inviter) {

        final EmailData inviteeInfo = EmailData.builder()
            .notificationTemplateData(EmailNotificationTemplateData.builder()
                    .templateName(INVITER_INVITATION_ACCEPTED)
                    .templateParams(Map.of(USER_ACCOUNT_CREATED_USER_FNAME, inviter.getFirstName(),
                            USER_ACCOUNT_CREATED_USER_LNAME, inviter.getLastName(),
                            USER_INVITEE_FNAME, invitee.getFirstName(),
                            USER_INVITEE_LNAME, invitee.getLastName()))
                    .build())
            .build();

        notificationEmailService.notifyRecipient(inviteeInfo, inviter.getEmail());
    }

    public void sendUpdateNotifications(final List<String> activatedRegulators) {

        // send notifications for accounts that have been activated
        activatedRegulators
            .forEach(userId -> {
                try {
                    userNotificationService.notifyUserAccountActivation(userId, "Regulator");
                } catch (Exception ex) {
                    log.error("Exception during sending email for regulator activation:", ex);
                }
            });
    }
}
