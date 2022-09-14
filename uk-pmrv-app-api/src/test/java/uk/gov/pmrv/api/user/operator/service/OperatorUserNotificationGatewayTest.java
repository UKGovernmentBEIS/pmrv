package uk.gov.pmrv.api.user.operator.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.pmrv.api.notification.mail.constants.EmailNotificationTemplateConstants.EMAIL_CONFIRMATION_LINK;
import static uk.gov.pmrv.api.notification.mail.constants.EmailNotificationTemplateConstants.USER_ROLE_TYPE;
import static uk.gov.pmrv.api.notification.template.domain.enumeration.NotificationTemplateName.EMAIL_CONFIRMATION;
import static uk.gov.pmrv.api.notification.template.domain.enumeration.NotificationTemplateName.INVITATION_TO_OPERATOR_ACCOUNT;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.service.AccountQueryService;
import uk.gov.pmrv.api.authorization.AuthorityConstants;
import uk.gov.pmrv.api.authorization.core.domain.dto.RoleDTO;
import uk.gov.pmrv.api.authorization.core.service.RoleService;
import uk.gov.pmrv.api.authorization.operator.domain.NewUserActivated;
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
import uk.gov.pmrv.api.user.operator.domain.OperatorUserAcceptInvitationDTO;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserDTO;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserInvitationDTO;

@ExtendWith(MockitoExtension.class)
class OperatorUserNotificationGatewayTest {

    @InjectMocks
    private OperatorUserNotificationGateway operatorUserNotificationGateway;

    @Mock
    private RoleService roleService;

    @Mock
    private AccountQueryService accountQueryService;

    @Mock
    private NotificationEmailService notificationEmailService;

    @Mock
    private UserNotificationService userNotificationService;

    @Mock
    private JwtProperties jwtProperties;

    @Test
    void notifyInvitedUser() {
        String receiverEmail = "receiverEmail";
        String roleCode = "roleCode";
        String accountName = "accountName";
        String authorityUuid = "authorityUuid";
        String roleName = "roleName";
        RoleDTO roleDTO = RoleDTO.builder().code(roleCode).name(roleName).build();

        OperatorUserInvitationDTO operatorUserInvitationDTO =
            OperatorUserInvitationDTO
                .builder()
                .email(receiverEmail)
                .roleCode(roleCode)
                .build();

        JwtProperties.Claim claim = mock(JwtProperties.Claim.class);
        long expirationInterval = 60L;

        when(roleService.getRoleByCode(roleCode)).thenReturn(roleDTO);
        when(jwtProperties.getClaim()).thenReturn(claim);
        when(claim.getUserInvitationExpIntervalMinutes()).thenReturn(expirationInterval);

        operatorUserNotificationGateway.notifyInvitedUser(operatorUserInvitationDTO, accountName, authorityUuid);

        verify(roleService , times(1)).getRoleByCode(roleCode);

        ArgumentCaptor<UserNotificationWithRedirectionLinkInfo> notificationInfoCaptor =
            ArgumentCaptor.forClass(UserNotificationWithRedirectionLinkInfo.class);
        verify(userNotificationService, times(1)).notifyUser(notificationInfoCaptor.capture());

        UserNotificationWithRedirectionLinkInfo notificationInfo = notificationInfoCaptor.getValue();

        assertThat(notificationInfo.getTemplateName()).isEqualTo(INVITATION_TO_OPERATOR_ACCOUNT);
        assertThat(notificationInfo.getUserEmail()).isEqualTo(operatorUserInvitationDTO.getEmail());
        assertThat(notificationInfo.getLinkParamName()).isEqualTo(EmailNotificationTemplateConstants.OPERATOR_INVITATION_CONFIRMATION_LINK);
        assertThat(notificationInfo.getLinkPath()).isEqualTo(NavigationOutcomes.OPERATOR_REGISTRATION_INVITATION_ACCEPTED_URL);
        assertThat(notificationInfo.getNotificationParams()).containsExactlyInAnyOrderEntriesOf(
            Map.of(
                    USER_ROLE_TYPE, roleDTO.getName(),
                    EmailNotificationTemplateConstants.ACCOUNT_NAME, accountName,
                    EmailNotificationTemplateConstants.EXPIRATION_MINUTES, 60L
            ));
        assertThat(notificationInfo.getTokenParams())
            .isEqualTo(expectedInvitationLinkTokenParams(JwtTokenActionEnum.OPERATOR_INVITATION, authorityUuid, expirationInterval));

    }

    @Test
    void notifyRegisteredUser() {
        OperatorUserDTO operatorUserDTO =
            OperatorUserDTO.builder().firstName("fn").lastName("ln").email("email").build();

        operatorUserNotificationGateway.notifyRegisteredUser(operatorUserDTO);

        //verify
        ArgumentCaptor<EmailData> emailInfoCaptor = ArgumentCaptor.forClass(EmailData.class);
        verify(notificationEmailService, times(1)).notifyRecipient(emailInfoCaptor.capture(), Mockito.eq(operatorUserDTO.getEmail()));

        EmailData emailInfo = emailInfoCaptor.getValue();
        assertThat(emailInfo.getNotificationTemplateData().getTemplateName()).isEqualTo(NotificationTemplateName.USER_ACCOUNT_CREATED);

        assertThat(emailInfo.getNotificationTemplateData().getTemplateParams()).containsExactlyInAnyOrderEntriesOf(
            Map.of(
                EmailNotificationTemplateConstants.USER_ACCOUNT_CREATED_USER_EMAIL, operatorUserDTO.getEmail()
            ));
    }

    @Test
    void notifyEmailVerification() {
        String email = "email";
        JwtProperties.Claim claim = mock(JwtProperties.Claim.class);
        long expirationInterval = 10L;

        when(jwtProperties.getClaim()).thenReturn(claim);
        when(claim.getUserInvitationExpIntervalMinutes()).thenReturn(expirationInterval);

        operatorUserNotificationGateway.notifyEmailVerification("email");

        //verify
        ArgumentCaptor<UserNotificationWithRedirectionLinkInfo> notificationInfoCaptor =
            ArgumentCaptor.forClass(UserNotificationWithRedirectionLinkInfo.class);
        verify(userNotificationService, times(1)).notifyUser(notificationInfoCaptor.capture());

        UserNotificationWithRedirectionLinkInfo notificationInfo = notificationInfoCaptor.getValue();

        assertThat(notificationInfo.getTemplateName()).isEqualTo(EMAIL_CONFIRMATION);
        assertThat(notificationInfo.getUserEmail()).isEqualTo(email);
        assertThat(notificationInfo.getLinkParamName()).isEqualTo(EMAIL_CONFIRMATION_LINK);
        assertThat(notificationInfo.getLinkPath()).isEqualTo(NavigationOutcomes.REGISTRATION_EMAIL_VERIFY_CONFIRMATION_URL);
        assertThat(notificationInfo.getNotificationParams()).isNull();
        assertThat(notificationInfo.getTokenParams())
            .isEqualTo(expectedInvitationLinkTokenParams(JwtTokenActionEnum.USER_REGISTRATION, email, expirationInterval));
    }

    @Test
    void notifyInviteeAcceptedInvitation() {
        OperatorUserAcceptInvitationDTO operatorUserAcceptInvitation = OperatorUserAcceptInvitationDTO.builder()
            .firstName("firstName")
            .lastName("lastName")
            .email("email")
            .accountInstallationName("accountInstallationName")
            .build();

        operatorUserNotificationGateway.notifyInviteeAcceptedInvitation(operatorUserAcceptInvitation);

        //verify
        ArgumentCaptor<EmailData> emailInfoCaptor = ArgumentCaptor.forClass(EmailData.class);
        verify(notificationEmailService, times(1)).notifyRecipient(emailInfoCaptor.capture(), Mockito.eq(operatorUserAcceptInvitation.getEmail()));

        EmailData emailInfo = emailInfoCaptor.getValue();
        assertThat(emailInfo.getNotificationTemplateData().getTemplateName()).isEqualTo(NotificationTemplateName.INVITEE_INVITATION_ACCEPTED);
        assertThat(emailInfo.getNotificationTemplateData().getTemplateParams()).containsExactlyInAnyOrderEntriesOf(
            Map.of(USER_ROLE_TYPE, "Operator")
        );
    }

    @Test
    void notifyInviterAcceptedInvitation() {
        OperatorUserAcceptInvitationDTO invitee = OperatorUserAcceptInvitationDTO.builder()
                .firstName("inviteeName")
                .lastName("inviteeLastName")
                .email("inviteeEmail")
                .accountInstallationName("accountInstallationName")
                .build();
        UserInfoDTO inviter =  UserInfoDTO.builder()
                .firstName("inviterName")
                .lastName("inviterLastName")
                .email("inviterEmail")
                .build();

        operatorUserNotificationGateway.notifyInviterAcceptedInvitation(invitee, inviter);

        //verify
        ArgumentCaptor<EmailData> emailInfoCaptor = ArgumentCaptor.forClass(EmailData.class);
        verify(notificationEmailService, times(1)).notifyRecipient(emailInfoCaptor.capture(), Mockito.eq(inviter.getEmail()));

        EmailData emailInfo = emailInfoCaptor.getValue();
        assertThat(emailInfo.getNotificationTemplateData().getTemplateName()).isEqualTo(NotificationTemplateName.INVITER_INVITATION_ACCEPTED);
        assertThat(emailInfo.getNotificationTemplateData().getTemplateParams()).containsExactlyInAnyOrderEntriesOf(Map.of(
                EmailNotificationTemplateConstants.USER_ACCOUNT_CREATED_USER_FNAME, inviter.getFirstName(),
                EmailNotificationTemplateConstants.USER_ACCOUNT_CREATED_USER_LNAME, inviter.getLastName(),
                EmailNotificationTemplateConstants.USER_INVITEE_FNAME, invitee.getFirstName(),
                EmailNotificationTemplateConstants.USER_INVITEE_LNAME, invitee.getLastName()
        ));
    }

    @Test
    void notifyUsersUpdateStatus() {
        Long accountId = 1L;
        String installationName = "installationName";

        NewUserActivated operator1 = NewUserActivated.builder().userId("operator1").roleCode(AuthorityConstants.OPERATOR_ROLE_CODE).build();
        NewUserActivated operator2 = NewUserActivated.builder().userId("operator2").roleCode(AuthorityConstants.OPERATOR_ROLE_CODE).build();
        NewUserActivated emitter1 = NewUserActivated.builder().userId("emitter1").accountId(accountId)
                .roleCode(AuthorityConstants.EMITTER_CONTACT).build();
        NewUserActivated emitter2 = NewUserActivated.builder().userId("emitter2").accountId(accountId)
                .roleCode(AuthorityConstants.EMITTER_CONTACT).build();

        List<NewUserActivated> activatedOperators = List.of(operator1, operator2, emitter1, emitter2);

        when(accountQueryService.getAccountInstallationName(accountId)).thenReturn(installationName);

        // Invoke
        operatorUserNotificationGateway.notifyUsersUpdateStatus(activatedOperators);

        // Verify
        verify(userNotificationService, times(1))
                .notifyEmitterContactAccountActivation(emitter1.getUserId(), installationName);
        verify(userNotificationService, times(1))
                .notifyEmitterContactAccountActivation(emitter2.getUserId(), installationName);
        verify(userNotificationService, times(1))
                .notifyUserAccountActivation(operator1.getUserId(), "Operator");
        verify(userNotificationService, times(1))
                .notifyUserAccountActivation(operator2.getUserId(), "Operator");
        verify(accountQueryService, times(2))
                .getAccountInstallationName(accountId);
        verifyNoMoreInteractions(userNotificationService, accountQueryService);
    }

    @Test
    void notifyUsersUpdateStatus_with_exception() {
        Long accountId = 1L;
        String installationName = "installationName";

        NewUserActivated operator1 = NewUserActivated.builder().userId("operator1").roleCode(AuthorityConstants.OPERATOR_ROLE_CODE).build();
        NewUserActivated operator2 = NewUserActivated.builder().userId("operator2").roleCode(AuthorityConstants.OPERATOR_ROLE_CODE).build();
        NewUserActivated emitter1 = NewUserActivated.builder().userId("emitter1").accountId(accountId)
                .roleCode(AuthorityConstants.EMITTER_CONTACT).build();

        List<NewUserActivated> activatedOperators = List.of(emitter1, operator1, operator2);

        when(accountQueryService.getAccountInstallationName(accountId))
                .thenThrow(NullPointerException.class);

        // Invoke
        operatorUserNotificationGateway.notifyUsersUpdateStatus(activatedOperators);

        // Verify
        verify(userNotificationService, never())
                .notifyEmitterContactAccountActivation(emitter1.getUserId(), installationName);
        verify(userNotificationService, times(1))
                .notifyUserAccountActivation(operator1.getUserId(), "Operator");
        verify(userNotificationService, times(1))
                .notifyUserAccountActivation(operator2.getUserId(), "Operator");
        verify(accountQueryService, times(1))
                .getAccountInstallationName(accountId);
        verifyNoMoreInteractions(userNotificationService, accountQueryService);
    }

    private UserNotificationWithRedirectionLinkInfo.TokenParams expectedInvitationLinkTokenParams(JwtTokenActionEnum jwtTokenAction,
                                                                                                  String claimValue, long expirationInterval) {
        return UserNotificationWithRedirectionLinkInfo.TokenParams.builder()
            .jwtTokenAction(jwtTokenAction)
            .claimValue(claimValue)
            .expirationInterval(expirationInterval)
            .build();
    }
}