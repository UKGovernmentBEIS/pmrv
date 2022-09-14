package uk.gov.pmrv.api.feedback.service;

import static java.util.Map.entry;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.feedback.config.UserFeedbackConfig;
import uk.gov.pmrv.api.feedback.domain.dto.UserFeedbackDto;
import uk.gov.pmrv.api.feedback.model.enumeration.FeedbackRating;
import uk.gov.pmrv.api.notification.mail.constants.EmailNotificationTemplateConstants;
import uk.gov.pmrv.api.notification.mail.domain.EmailData;
import uk.gov.pmrv.api.notification.mail.domain.EmailNotificationTemplateData;
import uk.gov.pmrv.api.notification.mail.service.NotificationEmailService;
import uk.gov.pmrv.api.notification.template.domain.enumeration.NotificationTemplateName;

@ExtendWith(MockitoExtension.class)
public class UserFeedbackServiceTest {

    @InjectMocks
    private UserFeedbackService userFeedbackService;

    @Mock
    private NotificationEmailService notificationEmailService;

    @Mock
    private UserFeedbackConfig userFeedbackConfig;

    @Spy
    private Clock fixedClock = Clock.fixed(Instant.now(), ZoneId.of("UTC"));

    @Test
    void sendFeedback() {
        String recipient = "test@test.com";
        String rootUrl = "http://test.com";
        UserFeedbackDto userFeedbackDto = createUserFeedbackDto();
        EmailNotificationTemplateData expectedTemplateData = constructEmailTemplateData(rootUrl, userFeedbackDto);
        ArgumentCaptor<EmailData> emailDataArgumentCaptor = ArgumentCaptor.forClass(EmailData.class);
        when(userFeedbackConfig.getRecipients()).thenReturn(Collections.singletonList(recipient));

        userFeedbackService.sendFeedback(rootUrl, userFeedbackDto);

        verify(userFeedbackConfig, times(1)).getRecipients();
        verify(notificationEmailService, times(1))
            .notifyRecipients(emailDataArgumentCaptor.capture(), Mockito.anyList(), Mockito.anyList());

        EmailData actualEmailData = emailDataArgumentCaptor.getValue();
        assertThat(actualEmailData.getAttachments().size()).isEqualTo(0);
        assertThat(actualEmailData.getNotificationTemplateData()).isEqualTo(expectedTemplateData);
    }

    private UserFeedbackDto createUserFeedbackDto() {
        return UserFeedbackDto.builder()
            .creatingAccountRate(FeedbackRating.DISSATISFIED)
            .creatingAccountRateReason("Optional")
            .improvementSuggestion("Very bad")
            .onBoardingRate(FeedbackRating.DISSATISFIED)
            .onBoardingRateReason("")
            .onlineGuidanceRate(FeedbackRating.SATISFIED)
            .onlineGuidanceRateReason("")
            .satisfactionRate(FeedbackRating.DISSATISFIED)
            .satisfactionRateReason("")
            .tasksRate(FeedbackRating.DISSATISFIED)
            .tasksRateReason("")
            .userRegistrationRate(FeedbackRating.DISSATISFIED)
            .userRegistrationRateReason("")
            .build();
    }

    private EmailNotificationTemplateData constructEmailTemplateData(String rootUrl, UserFeedbackDto userFeedback) {
        return EmailNotificationTemplateData.builder()
            .templateName(NotificationTemplateName.USER_FEEDBACK)
            .templateParams(
                Map.ofEntries(
                    entry(EmailNotificationTemplateConstants.DOMAIN_URL, rootUrl),
                    entry(EmailNotificationTemplateConstants.USER_FEEDBACK_TIMESTAMP,
                        ZonedDateTime.now(fixedClock).format(DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm"))),
                    entry(EmailNotificationTemplateConstants.USER_FEEDBACK_SATISFACTION_RATE, userFeedback.getSatisfactionRate().getDescription()),
                    entry(EmailNotificationTemplateConstants.USER_FEEDBACK_SATISFACTION_RATE_REASON, userFeedback.getSatisfactionRateReason()),
                    entry(EmailNotificationTemplateConstants.USER_FEEDBACK_USER_REGISTRATION_RATE, userFeedback.getUserRegistrationRate().getDescription()),
                    entry(EmailNotificationTemplateConstants.USER_FEEDBACK_USER_REGISTRATION_RATE_REASON,
                        userFeedback.getUserRegistrationRateReason()),
                    entry(EmailNotificationTemplateConstants.USER_FEEDBACK_ONLINE_GUIDANCE_RATE, userFeedback.getOnlineGuidanceRate().getDescription()),
                    entry(EmailNotificationTemplateConstants.USER_FEEDBACK_ONLINE_GUIDANCE_RATE_REASON, userFeedback.getOnlineGuidanceRateReason()),
                    entry(EmailNotificationTemplateConstants.USER_FEEDBACK_CREATING_ACCOUNT_RATE, userFeedback.getCreatingAccountRate().getDescription()),
                    entry(EmailNotificationTemplateConstants.USER_FEEDBACK_CREATING_ACCOUNT_RATE_REASON,
                        userFeedback.getCreatingAccountRateReason()),
                    entry(EmailNotificationTemplateConstants.USER_FEEDBACK_ONBOARD_RATE, userFeedback.getOnBoardingRate().getDescription()),
                    entry(EmailNotificationTemplateConstants.USER_FEEDBACK_ONBOARD_RATE_REASON, userFeedback.getOnBoardingRateReason()),
                    entry(EmailNotificationTemplateConstants.USER_FEEDBACK_TASKS_RATE, userFeedback.getTasksRate().getDescription()),
                    entry(EmailNotificationTemplateConstants.USER_FEEDBACK_TASKS_RATE_REASON, userFeedback.getTasksRateReason()),
                    entry(EmailNotificationTemplateConstants.USER_FEEDBACK_IMPROVEMENT_SUGGESTION, userFeedback.getImprovementSuggestion())
                )
            )
            .build();
    }

}
