package uk.gov.pmrv.api.user.core.service;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.notification.mail.config.property.NotificationProperties;
import uk.gov.pmrv.api.notification.mail.constants.EmailNotificationTemplateConstants;
import uk.gov.pmrv.api.notification.template.domain.enumeration.NotificationTemplateName;
import uk.gov.pmrv.api.user.core.config.JwtProperties;
import uk.gov.pmrv.api.user.JwtTokenActionEnum;
import uk.gov.pmrv.api.user.NavigationOutcomes;
import uk.gov.pmrv.api.user.core.domain.dto.TokenDTO;
import uk.gov.pmrv.api.user.core.domain.model.UserNotificationWithRedirectionLinkInfo;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserSecuritySetupService {

    private final UserNotificationService userNotificationService;
    private final UserAuthService userAuthService;
    private final JwtProperties jwtProperties;
    private final NotificationProperties notificationProperties;

    public void requestTwoFactorAuthChange(PmrvUser currentUser, String accessToken, String otp) {
        // Validate otp
        userAuthService.validateAuthenticatedUserOtp(otp, accessToken);
        long expirationInMinutes = jwtProperties.getClaim().getChange2faExpIntervalMinutes();

        Map<String, Object> notificationParams = new HashMap<>(Map.of(
                EmailNotificationTemplateConstants.OFFICIAL_PMRV_CONTACT, notificationProperties.getEmail().getOfficialContact(),
                EmailNotificationTemplateConstants.EXPIRATION_MINUTES, expirationInMinutes
        ));

        // Send email with token
        userNotificationService.notifyUser(
            UserNotificationWithRedirectionLinkInfo.builder()
                .templateName(NotificationTemplateName.CHANGE_2FA)
                .userEmail(currentUser.getEmail())
                .notificationParams(notificationParams)
                .linkParamName(EmailNotificationTemplateConstants.CHANGE_2FA_LINK)
                .linkPath(NavigationOutcomes.CHANGE_2FA_URL)
                .tokenParams(UserNotificationWithRedirectionLinkInfo.TokenParams.builder()
                    .jwtTokenAction(JwtTokenActionEnum.CHANGE_2FA)
                    .claimValue(currentUser.getEmail())
                    .expirationInterval(expirationInMinutes)
                    .build()
                )
                .build()
        );
    }

    public void deleteOtpCredentials(TokenDTO tokenDTO) {
        // Validate token and get email
        String userEmail = userAuthService.resolveTokenActionClaim(tokenDTO.getToken(), JwtTokenActionEnum.CHANGE_2FA);

        // Delete otp credentials
        userAuthService.deleteOtpCredentials(userEmail);
    }
}
