package uk.gov.pmrv.api.feedback.service;

import static java.util.Map.entry;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.feedback.config.UserFeedbackConfig;
import uk.gov.pmrv.api.feedback.domain.dto.UserFeedbackDto;
import uk.gov.pmrv.api.notification.mail.constants.EmailNotificationTemplateConstants;
import uk.gov.pmrv.api.notification.mail.domain.EmailData;
import uk.gov.pmrv.api.notification.mail.domain.EmailNotificationTemplateData;
import uk.gov.pmrv.api.notification.mail.service.NotificationEmailService;
import uk.gov.pmrv.api.notification.template.domain.enumeration.NotificationTemplateName;

@Service
@RequiredArgsConstructor
public class UserFeedbackService {

    private final NotificationEmailService notificationEmailService;
    private final UserFeedbackConfig userFeedbackConfig;
    private final Clock clock;

    public void sendFeedback(String domainUrl, UserFeedbackDto userFeedbackDto) {
        notificationEmailService.notifyRecipients(
            EmailData.builder()
                .notificationTemplateData(constructEmailTemplateData(domainUrl, userFeedbackDto))
                .attachments(Collections.emptyMap())
                .build(),
            userFeedbackConfig.getRecipients(),
            Collections.emptyList()
        );
    }

    private EmailNotificationTemplateData constructEmailTemplateData(String domainUrl, UserFeedbackDto userFeedback) {
        return EmailNotificationTemplateData.builder()
            .templateName(NotificationTemplateName.USER_FEEDBACK)
            .templateParams(
                Map.ofEntries(
                    entry(EmailNotificationTemplateConstants.DOMAIN_URL, domainUrl),
                    entry(EmailNotificationTemplateConstants.USER_FEEDBACK_TIMESTAMP,
                        LocalDateTime.now(clock).format(DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm"))),
                    entry(EmailNotificationTemplateConstants.USER_FEEDBACK_SATISFACTION_RATE, userFeedback.getSatisfactionRate().getDescription()),
                    entry(EmailNotificationTemplateConstants.USER_FEEDBACK_SATISFACTION_RATE_REASON,
                        Optional.ofNullable(userFeedback.getSatisfactionRateReason()).orElse("Not provided")),
                    entry(EmailNotificationTemplateConstants.USER_FEEDBACK_USER_REGISTRATION_RATE, userFeedback.getUserRegistrationRate().getDescription()),
                    entry(EmailNotificationTemplateConstants.USER_FEEDBACK_USER_REGISTRATION_RATE_REASON,
                        Optional.ofNullable(userFeedback.getUserRegistrationRateReason()).orElse("Not provided")),
                    entry(EmailNotificationTemplateConstants.USER_FEEDBACK_ONLINE_GUIDANCE_RATE, userFeedback.getOnlineGuidanceRate().getDescription()),
                    entry(EmailNotificationTemplateConstants.USER_FEEDBACK_ONLINE_GUIDANCE_RATE_REASON,
                        Optional.ofNullable(userFeedback.getOnlineGuidanceRateReason()).orElse("Not provided")),
                    entry(EmailNotificationTemplateConstants.USER_FEEDBACK_CREATING_ACCOUNT_RATE, userFeedback.getCreatingAccountRate().getDescription()),
                    entry(EmailNotificationTemplateConstants.USER_FEEDBACK_CREATING_ACCOUNT_RATE_REASON,
                        Optional.ofNullable(userFeedback.getCreatingAccountRateReason()).orElse("Not provided")),
                    entry(EmailNotificationTemplateConstants.USER_FEEDBACK_ONBOARD_RATE, userFeedback.getOnBoardingRate().getDescription()),
                    entry(EmailNotificationTemplateConstants.USER_FEEDBACK_ONBOARD_RATE_REASON,
                        Optional.ofNullable(userFeedback.getOnBoardingRateReason()).orElse("Not provided")),
                    entry(EmailNotificationTemplateConstants.USER_FEEDBACK_TASKS_RATE, userFeedback.getTasksRate().getDescription()),
                    entry(EmailNotificationTemplateConstants.USER_FEEDBACK_TASKS_RATE_REASON,
                        Optional.ofNullable(userFeedback.getTasksRateReason()).orElse("Not provided")),
                    entry(EmailNotificationTemplateConstants.USER_FEEDBACK_IMPROVEMENT_SUGGESTION, 
                    		Optional.ofNullable(userFeedback.getImprovementSuggestion()).orElse("Not provided"))
                )
            )
            .build();
    }

}
