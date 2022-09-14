package uk.gov.pmrv.api.user.core.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.pmrv.api.notification.mail.constants.EmailNotificationTemplateConstants.APPLICANT_FNAME;
import static uk.gov.pmrv.api.notification.mail.constants.EmailNotificationTemplateConstants.APPLICANT_LNAME;
import static uk.gov.pmrv.api.notification.mail.constants.EmailNotificationTemplateConstants.OPERATOR_INVITATION_CONFIRMATION_LINK;
import static uk.gov.pmrv.api.notification.mail.constants.EmailNotificationTemplateConstants.USER_ROLE_TYPE;
import static uk.gov.pmrv.api.notification.template.domain.enumeration.NotificationTemplateName.INVITATION_TO_EMITTER_CONTACT;
import static uk.gov.pmrv.api.notification.template.domain.enumeration.NotificationTemplateName.USER_ACCOUNT_ACTIVATION;
import static uk.gov.pmrv.api.user.NavigationOutcomes.OPERATOR_REGISTRATION_INVITATION_ACCEPTED_URL;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.pmrv.api.common.config.property.AppProperties;
import uk.gov.pmrv.api.notification.mail.constants.EmailNotificationTemplateConstants;
import uk.gov.pmrv.api.notification.mail.domain.EmailNotificationTemplateData;
import uk.gov.pmrv.api.notification.mail.domain.EmailData;
import uk.gov.pmrv.api.notification.mail.service.NotificationEmailService;
import uk.gov.pmrv.api.notification.template.domain.enumeration.NotificationTemplateName;
import uk.gov.pmrv.api.user.JwtTokenActionEnum;
import uk.gov.pmrv.api.user.NavigationParams;
import uk.gov.pmrv.api.user.core.domain.dto.UserInfoDTO;
import uk.gov.pmrv.api.user.core.domain.model.UserNotificationWithRedirectionLinkInfo;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;

@ExtendWith(MockitoExtension.class)
class UserNotificationServiceTest {

    @InjectMocks
    private UserNotificationService userNotificationService;

    @Mock
    private UserAuthService userAuthService;

    @Mock
    private NotificationEmailService notificationEmailService;

    @Mock
    private AppProperties appProperties;

    @Test
    void notifyInvitedUser() {
        String token = "token";
        UserNotificationWithRedirectionLinkInfo notificationInfo = createNotificationInfo();
        UserNotificationWithRedirectionLinkInfo.TokenParams tokenParams = notificationInfo.getTokenParams();
        AppProperties.Web web = createAppWeb();

        when(appProperties.getWeb()).thenReturn(web);
        when(userAuthService
            .generateToken(tokenParams.getJwtTokenAction(), tokenParams.getClaimValue(), tokenParams.getExpirationInterval()))
            .thenReturn(token);

        userNotificationService.notifyUser(notificationInfo);

        verify(userAuthService, times(1))
            .generateToken(tokenParams.getJwtTokenAction(), tokenParams.getClaimValue(), tokenParams.getExpirationInterval());
        verify(appProperties, times(1)).getWeb();

        ArgumentCaptor<EmailData> emailInfoCaptor =
            ArgumentCaptor.forClass(EmailData.class);
        verify(notificationEmailService, times(1)).notifyRecipient(emailInfoCaptor.capture(), Mockito.eq(notificationInfo.getUserEmail()));

        EmailData emailInfo = emailInfoCaptor.getValue();

        assertThat(emailInfo.getNotificationTemplateData().getTemplateName()).isEqualTo(notificationInfo.getTemplateName());
        assertThat(emailInfo.getNotificationTemplateData().getTemplateParams()).containsExactlyInAnyOrderEntriesOf(
            Map.of(
                EmailNotificationTemplateConstants.APPLICANT_FNAME, "firstName",
                OPERATOR_INVITATION_CONFIRMATION_LINK, expectedNotificationLink(token, web)
            ));
        assertThat(emailInfo.getAttachments()).isEmpty();
    }

    @Test
    void notifyUserAccountActivation() {
        String userId = "userId";
        String roleName = "roleName";
        String email = "email";
        UserInfoDTO userInfo = UserInfoDTO.builder().email(email).build();
        EmailData emailInfo = EmailData.builder()
                .notificationTemplateData(EmailNotificationTemplateData.builder()
                        .templateName(USER_ACCOUNT_ACTIVATION)
                        .templateParams(Map.of(USER_ROLE_TYPE, roleName))
                        .build())
                .build();

        when(userAuthService.getUserByUserId(userId)).thenReturn(userInfo);

        userNotificationService.notifyUserAccountActivation(userId, roleName);

        verify(userAuthService, times(1)).getUserByUserId(userId);
        verify(notificationEmailService, times(1)).notifyRecipient(emailInfo, email);
    }

    @Test
    void notifyNewEmitterContact() {
        String userId = "userId";
        String installationName = "installationName";
        UserInfoDTO userInfo = UserInfoDTO.builder()
                .firstName("firstName")
                .lastName("lastName")
                .email("email").build();
        EmailData emailInfo = EmailData.builder()
                .notificationTemplateData(EmailNotificationTemplateData.builder()
                        .templateName(INVITATION_TO_EMITTER_CONTACT)
                        .templateParams(Map.of(APPLICANT_FNAME, userInfo.getFirstName(),
                                APPLICANT_LNAME, userInfo.getLastName(),
                                EmailNotificationTemplateConstants.ACCOUNT_NAME, installationName))
                        .build())
                .build();

        when(userAuthService.getUserByUserId(userId)).thenReturn(userInfo);

        userNotificationService.notifyEmitterContactAccountActivation(userId, installationName);

        verify(userAuthService, times(1)).getUserByUserId(userId);
        verify(notificationEmailService, times(1)).notifyRecipient(emailInfo, userInfo.getEmail());
    }

    private UserNotificationWithRedirectionLinkInfo createNotificationInfo() {
        return UserNotificationWithRedirectionLinkInfo.builder()
            .templateName(NotificationTemplateName.INVITATION_TO_OPERATOR_ACCOUNT)
            .userEmail("email")
            .linkParamName(OPERATOR_INVITATION_CONFIRMATION_LINK)
            .linkPath(OPERATOR_REGISTRATION_INVITATION_ACCEPTED_URL)
            .notificationParams(new HashMap<>(
                Map.of(EmailNotificationTemplateConstants.APPLICANT_FNAME, "firstName")))
            .tokenParams(UserNotificationWithRedirectionLinkInfo.TokenParams.builder()
                .jwtTokenAction(JwtTokenActionEnum.OPERATOR_INVITATION)
                .claimValue("claimValue")
                .expirationInterval(5L)
                .build()
            )
            .build();
    }

    private String expectedNotificationLink(String token, AppProperties.Web web) {
        return UriComponentsBuilder
            .fromHttpUrl(web.getUrl())
            .path("/")
            .path(OPERATOR_REGISTRATION_INVITATION_ACCEPTED_URL)
            .queryParam(NavigationParams.TOKEN, token)
            .build()
            .toUriString();
    }

    private AppProperties.Web createAppWeb() {
        AppProperties.Web web = new AppProperties.Web();
        web.setUrl("http://www.pmrv.org.uk");
        return web;
    }
}