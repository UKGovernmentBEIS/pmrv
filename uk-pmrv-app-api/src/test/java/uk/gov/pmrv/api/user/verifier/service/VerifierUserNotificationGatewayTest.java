package uk.gov.pmrv.api.user.verifier.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.notification.mail.constants.EmailNotificationTemplateConstants;
import uk.gov.pmrv.api.notification.mail.domain.EmailData;
import uk.gov.pmrv.api.notification.mail.service.NotificationEmailService;
import uk.gov.pmrv.api.notification.template.domain.enumeration.NotificationTemplateName;
import uk.gov.pmrv.api.user.JwtTokenActionEnum;
import uk.gov.pmrv.api.user.NavigationOutcomes;
import uk.gov.pmrv.api.user.core.config.JwtProperties;
import uk.gov.pmrv.api.user.core.domain.dto.UserInfoDTO;
import uk.gov.pmrv.api.user.core.domain.model.UserNotificationWithRedirectionLinkInfo;
import uk.gov.pmrv.api.user.core.service.UserNotificationService;
import uk.gov.pmrv.api.user.verifier.domain.VerifierUserInvitationDTO;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.pmrv.api.notification.mail.constants.EmailNotificationTemplateConstants.USER_ACCOUNT_CREATED_USER_FNAME;
import static uk.gov.pmrv.api.notification.mail.constants.EmailNotificationTemplateConstants.USER_ACCOUNT_CREATED_USER_LNAME;
import static uk.gov.pmrv.api.notification.mail.constants.EmailNotificationTemplateConstants.USER_INVITEE_FNAME;
import static uk.gov.pmrv.api.notification.mail.constants.EmailNotificationTemplateConstants.USER_INVITEE_LNAME;
import static uk.gov.pmrv.api.notification.mail.constants.EmailNotificationTemplateConstants.USER_ROLE_TYPE;

@ExtendWith(MockitoExtension.class)
class VerifierUserNotificationGatewayTest {

    @InjectMocks
    private VerifierUserNotificationGateway verifierUserNotificationGateway;

    @Mock
    private UserNotificationService userNotificationService;

    @Mock
    private NotificationEmailService notificationEmailService;

    @Mock
    private JwtProperties jwtProperties;

    @Test
    void notifyInvitedUser() {
        VerifierUserInvitationDTO verifierUserInvitation = createVerifierUserInvitationDTO();
        String authorityUuid = "uuid";
        JwtProperties.Claim claim = mock(JwtProperties.Claim.class);
        long expirationInterval = 60L;

        when(jwtProperties.getClaim()).thenReturn(claim);
        when(claim.getUserInvitationExpIntervalMinutes()).thenReturn(expirationInterval);

        // Invoke
        verifierUserNotificationGateway.notifyInvitedUser(verifierUserInvitation, authorityUuid);

        // Verify
        ArgumentCaptor<UserNotificationWithRedirectionLinkInfo> notificationInfoCaptor =
                ArgumentCaptor.forClass(UserNotificationWithRedirectionLinkInfo.class);
        verify(userNotificationService, times(1)).notifyUser(notificationInfoCaptor.capture());

        UserNotificationWithRedirectionLinkInfo notificationInfo = notificationInfoCaptor.getValue();

        assertThat(notificationInfo.getTemplateName()).isEqualTo(NotificationTemplateName.INVITATION_TO_VERIFIER_ACCOUNT);
        assertThat(notificationInfo.getUserEmail()).isEqualTo(verifierUserInvitation.getEmail());
        assertThat(notificationInfo.getLinkParamName()).isEqualTo(EmailNotificationTemplateConstants.VERIFIER_INVITATION_CONFIRMATION_LINK);
        assertThat(notificationInfo.getLinkPath()).isEqualTo(NavigationOutcomes.VERIFIER_REGISTRATION_INVITATION_ACCEPTED_URL);
        assertThat(notificationInfo.getNotificationParams()).containsExactlyInAnyOrderEntriesOf(
                Map.of(
                        EmailNotificationTemplateConstants.APPLICANT_FNAME, verifierUserInvitation.getFirstName(),
                        EmailNotificationTemplateConstants.APPLICANT_LNAME, verifierUserInvitation.getLastName(),
                        EmailNotificationTemplateConstants.EXPIRATION_MINUTES, 60L
                ));
        assertThat(notificationInfo.getTokenParams()).isEqualTo(expectedInvitationLinkTokenParams(authorityUuid, expirationInterval));
    }

    @Test
    void notifyInviteeAcceptedInvitation() {
        UserInfoDTO invitee = UserInfoDTO.builder()
                .firstName("firstName")
                .lastName("lastName")
                .email("email")
                .build();

        // Invoke
        verifierUserNotificationGateway.notifyInviteeAcceptedInvitation(invitee);

        // Verify
        ArgumentCaptor<EmailData> emailInfoCaptor = ArgumentCaptor.forClass(EmailData.class);
        verify(notificationEmailService, times(1)).notifyRecipient(emailInfoCaptor.capture(), Mockito.eq(invitee.getEmail()));

        EmailData emailInfo = emailInfoCaptor.getValue();
        assertThat(emailInfo.getNotificationTemplateData().getTemplateName()).isEqualTo(NotificationTemplateName.INVITEE_INVITATION_ACCEPTED);
        assertThat(emailInfo.getNotificationTemplateData().getTemplateParams()).containsExactlyInAnyOrderEntriesOf(
                Map.of(USER_ROLE_TYPE, "Verifier")
        );
    }

    @Test
    void notifyInviterAcceptedInvitation() {
        UserInfoDTO invitee = UserInfoDTO.builder()
                .firstName("inviteeName")
                .lastName("inviteeLastName")
                .email("inviteeEmail")
                .build();
        UserInfoDTO inviter =  UserInfoDTO.builder()
                .firstName("inviterName")
                .lastName("inviterLastName")
                .email("inviterEmail")
                .build();

        // Invoke
        verifierUserNotificationGateway.notifyInviterAcceptedInvitation(invitee, inviter);

        // Verify
        ArgumentCaptor<EmailData> emailInfoCaptor = ArgumentCaptor.forClass(EmailData.class);
        verify(notificationEmailService, times(1)).notifyRecipient(emailInfoCaptor.capture(), Mockito.eq(inviter.getEmail()));

        EmailData emailInfo = emailInfoCaptor.getValue();
        assertThat(emailInfo.getNotificationTemplateData().getTemplateName()).isEqualTo(NotificationTemplateName.INVITER_INVITATION_ACCEPTED);
        assertThat(emailInfo.getNotificationTemplateData().getTemplateParams()).containsExactlyInAnyOrderEntriesOf(Map.of(
                USER_ACCOUNT_CREATED_USER_FNAME, inviter.getFirstName(),
                USER_ACCOUNT_CREATED_USER_LNAME, inviter.getLastName(),
                USER_INVITEE_FNAME, invitee.getFirstName(),
                USER_INVITEE_LNAME, invitee.getLastName()
        ));
    }

    @Test
    void activatedVerifiers() {
        String userId1 = "verifier1";
        String userId2 = "verifier2";
        List<String> notifications = List.of(userId1, userId2);

        // Invoke
        verifierUserNotificationGateway.notifyUsersUpdateStatus(notifications);

        // Verify
        verify(userNotificationService, times(1))
                .notifyUserAccountActivation(userId1, "Verifier");
        verify(userNotificationService, times(1))
                .notifyUserAccountActivation(userId2, "Verifier");
        verifyNoMoreInteractions(userNotificationService);
    }

    @Test
    void activatedVerifiers_with_exception() {
        String userId1 = "verifier1";
        String userId2 = "verifier2";
        List<String> notifications = List.of(userId1, userId2);

        doThrow(new RuntimeException())
                .when(userNotificationService)
                .notifyUserAccountActivation(userId1, "Verifier");

        // Invoke
        verifierUserNotificationGateway.notifyUsersUpdateStatus(notifications);

        // Verify
        verify(userNotificationService, times(1))
                .notifyUserAccountActivation(userId1, "Verifier");
        verify(userNotificationService, times(1))
                .notifyUserAccountActivation(userId2, "Verifier");
        verifyNoMoreInteractions(userNotificationService);
    }

    private VerifierUserInvitationDTO createVerifierUserInvitationDTO() {
        return VerifierUserInvitationDTO.builder()
                .roleCode("roleCode")
                .firstName("firstName")
                .lastName("lastName")
                .email("email")
                .phoneNumber("69999999999")
                .build();
    }

    private UserNotificationWithRedirectionLinkInfo.TokenParams expectedInvitationLinkTokenParams(String authUuid,
                                                                                                  long expirationInterval) {
        return UserNotificationWithRedirectionLinkInfo.TokenParams.builder()
                .jwtTokenAction(JwtTokenActionEnum.VERIFIER_INVITATION)
                .claimValue(authUuid)
                .expirationInterval(expirationInterval)
                .build();
    }

}