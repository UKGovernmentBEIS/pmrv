package uk.gov.pmrv.api.user.regulator.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.notification.mail.constants.EmailNotificationTemplateConstants;
import uk.gov.pmrv.api.notification.template.domain.enumeration.NotificationTemplateName;
import uk.gov.pmrv.api.user.JwtTokenActionEnum;
import uk.gov.pmrv.api.user.NavigationOutcomes;
import uk.gov.pmrv.api.user.core.config.JwtProperties;
import uk.gov.pmrv.api.user.core.domain.model.UserNotificationWithRedirectionLinkInfo;
import uk.gov.pmrv.api.user.core.domain.model.UserNotificationWithRedirectionLinkInfo.TokenParams;
import uk.gov.pmrv.api.user.core.service.UserNotificationService;
import uk.gov.pmrv.api.user.regulator.domain.RegulatorInvitedUserDTO;
import uk.gov.pmrv.api.user.regulator.domain.RegulatorInvitedUserDetailsDTO;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.pmrv.api.authorization.regulator.domain.RegulatorPermissionGroup.MANAGE_USERS_AND_CONTACTS;
import static uk.gov.pmrv.api.authorization.regulator.domain.RegulatorPermissionLevel.NONE;

@ExtendWith(MockitoExtension.class)
class RegulatorUserNotificationGatewayTest {

    @InjectMocks
    private RegulatorUserNotificationGateway regulatorUserNotificationGateway;

    @Mock
    private UserNotificationService userNotificationService;

    @Mock
    private JwtProperties jwtProperties;

    @Test
    void notifyInvitedUser() {
        RegulatorInvitedUserDTO regulatorInvitedUser = createInvitedUser();
        RegulatorInvitedUserDetailsDTO userDetails = regulatorInvitedUser.getUserDetails();
        String authorityUuid = "uuid";
        JwtProperties.Claim claim = mock(JwtProperties.Claim.class);
        long expirationInterval = 60L;

        when(jwtProperties.getClaim()).thenReturn(claim);
        when(claim.getUserInvitationExpIntervalMinutes()).thenReturn(expirationInterval);

        //invoke
        regulatorUserNotificationGateway.notifyInvitedUser(regulatorInvitedUser.getUserDetails(), authorityUuid);

        ArgumentCaptor<UserNotificationWithRedirectionLinkInfo> notificationInfoCaptor =
                ArgumentCaptor.forClass(UserNotificationWithRedirectionLinkInfo.class);
        verify(userNotificationService, times(1)).notifyUser(notificationInfoCaptor.capture());

        UserNotificationWithRedirectionLinkInfo notificationInfo = notificationInfoCaptor.getValue();

        assertThat(notificationInfo.getTemplateName()).isEqualTo(NotificationTemplateName.INVITATION_TO_REGULATOR_ACCOUNT);
        assertThat(notificationInfo.getUserEmail()).isEqualTo(userDetails.getEmail());
        assertThat(notificationInfo.getLinkParamName()).isEqualTo(EmailNotificationTemplateConstants.REGULATOR_INVITATION_CONFIRMATION_LINK);
        assertThat(notificationInfo.getLinkPath()).isEqualTo(NavigationOutcomes.REGULATOR_REGISTRATION_INVITATION_ACCEPTED_URL);
        assertThat(notificationInfo.getNotificationParams()).containsExactlyInAnyOrderEntriesOf(
                Map.of(
                        EmailNotificationTemplateConstants.APPLICANT_FNAME, userDetails.getFirstName(),
                        EmailNotificationTemplateConstants.APPLICANT_LNAME, userDetails.getLastName(),
                        EmailNotificationTemplateConstants.EXPIRATION_MINUTES, 60L
                ));
        assertThat(notificationInfo.getTokenParams()).isEqualTo(expectedInvitationLinkTokenParams(authorityUuid, expirationInterval));
    }

    @Test
    void sendActivationNotifications_whenActivations_thenNotifications() {
        regulatorUserNotificationGateway.sendUpdateNotifications(List.of("user2"));

        verify(userNotificationService, times(1)).notifyUserAccountActivation("user2", "Regulator");
    }

    private RegulatorInvitedUserDTO createInvitedUser() {
        return RegulatorInvitedUserDTO.builder()
                .userDetails(RegulatorInvitedUserDetailsDTO.builder()
                        .firstName("fn")
                        .lastName("ln")
                        .email("em@em.gr")
                        .jobTitle("title")
                        .phoneNumber("210000")
                        .build()
                )
                .permissions(Map.of(MANAGE_USERS_AND_CONTACTS, NONE))
                .build();
    }

    private TokenParams expectedInvitationLinkTokenParams(String authUuid, long expirationInterval) {
        return TokenParams.builder()
                .jwtTokenAction(JwtTokenActionEnum.REGULATOR_INVITATION)
                .claimValue(authUuid)
                .expirationInterval(expirationInterval)
                .build();
    }
}
