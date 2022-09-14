package uk.gov.pmrv.api.user.core.service;

import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.pmrv.api.common.config.property.AppProperties;
import uk.gov.pmrv.api.notification.mail.domain.EmailNotificationTemplateData;
import uk.gov.pmrv.api.notification.mail.constants.EmailNotificationTemplateConstants;
import uk.gov.pmrv.api.notification.mail.domain.EmailData;
import uk.gov.pmrv.api.notification.mail.service.NotificationEmailService;
import uk.gov.pmrv.api.user.NavigationParams;
import uk.gov.pmrv.api.user.core.domain.dto.UserInfoDTO;
import uk.gov.pmrv.api.user.core.domain.model.UserNotificationWithRedirectionLinkInfo;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;

import static uk.gov.pmrv.api.notification.mail.constants.EmailNotificationTemplateConstants.APPLICANT_FNAME;
import static uk.gov.pmrv.api.notification.mail.constants.EmailNotificationTemplateConstants.APPLICANT_LNAME;
import static uk.gov.pmrv.api.notification.mail.constants.EmailNotificationTemplateConstants.USER_ROLE_TYPE;
import static uk.gov.pmrv.api.notification.template.domain.enumeration.NotificationTemplateName.INVITATION_TO_EMITTER_CONTACT;
import static uk.gov.pmrv.api.notification.template.domain.enumeration.NotificationTemplateName.USER_ACCOUNT_ACTIVATION;

@Service
@RequiredArgsConstructor
public class UserNotificationService {

    private final UserAuthService userAuthService;
    private final NotificationEmailService notificationEmailService;
    private final AppProperties appProperties;

    /**
     * Sends email notification containing a redirection link to user.
     * @param notificationInfo {@link UserNotificationWithRedirectionLinkInfo}
     */
    public void notifyUser(UserNotificationWithRedirectionLinkInfo notificationInfo) {
        String redirectionLink = constructRedirectionLink(notificationInfo.getLinkPath(), notificationInfo.getTokenParams());

        Map<String, Object> notificationParameters = !ObjectUtils.isEmpty(notificationInfo.getNotificationParams()) ?
            notificationInfo.getNotificationParams() :
            new HashMap<>();

        notificationParameters.put(notificationInfo.getLinkParamName(), redirectionLink);

        EmailData emailInfo = EmailData.builder()
            .notificationTemplateData(EmailNotificationTemplateData.builder()
                    .templateName(notificationInfo.getTemplateName())
                    .templateParams(notificationParameters)
                    .build())
            .build();

        notificationEmailService.notifyRecipient(emailInfo, notificationInfo.getUserEmail());
    }

    public void notifyUserAccountActivation(String userId, String roleName) {
        UserInfoDTO user = userAuthService.getUserByUserId(userId);

        EmailData emailInfo = EmailData.builder()
                .notificationTemplateData(EmailNotificationTemplateData.builder()
                        .templateName(USER_ACCOUNT_ACTIVATION)
                        .templateParams(Map.of(USER_ROLE_TYPE, roleName))
                        .build())
                .build();

        notificationEmailService.notifyRecipient(emailInfo, user.getEmail());
    }

    public void notifyEmitterContactAccountActivation(String userId, String installationName) {
        UserInfoDTO user = userAuthService.getUserByUserId(userId);

        EmailData emailInfo = EmailData.builder()
                .notificationTemplateData(EmailNotificationTemplateData.builder()
                        .templateName(INVITATION_TO_EMITTER_CONTACT)
                        .templateParams(Map.of(APPLICANT_FNAME, user.getFirstName(),
                                APPLICANT_LNAME, user.getLastName(),
                                EmailNotificationTemplateConstants.ACCOUNT_NAME, installationName))
                        .build())
                .build();

        notificationEmailService.notifyRecipient(emailInfo, user.getEmail());
    }

    private String constructRedirectionLink(String path, UserNotificationWithRedirectionLinkInfo.TokenParams tokenParams) {
        String token = userAuthService
            .generateToken(tokenParams.getJwtTokenAction(), tokenParams.getClaimValue(), tokenParams.getExpirationInterval());

        return UriComponentsBuilder
            .fromHttpUrl(appProperties.getWeb().getUrl())
            .path("/")
            .path(path)
            .queryParam(NavigationParams.TOKEN, token)
            .build()
            .toUriString();
    }
}
