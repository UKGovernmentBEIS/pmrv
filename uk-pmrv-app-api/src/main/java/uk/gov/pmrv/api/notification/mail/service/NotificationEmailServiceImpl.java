package uk.gov.pmrv.api.notification.mail.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.notification.mail.config.property.NotificationProperties;
import uk.gov.pmrv.api.notification.mail.domain.Email;
import uk.gov.pmrv.api.notification.mail.domain.EmailData;
import uk.gov.pmrv.api.notification.template.domain.NotificationContent;
import uk.gov.pmrv.api.notification.template.service.NotificationTemplateProcessService;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Service implementation for generating and sending email notifications
 */
@Log4j2
@Service
@ConditionalOnProperty(name = "env.isProd", havingValue = "true")
public class NotificationEmailServiceImpl implements NotificationEmailService {

    private final SendEmailService sendEmailService;
    private final NotificationTemplateProcessService notificationTemplateProcessService;
    private final NotificationProperties notificationProperties;

    public NotificationEmailServiceImpl(SendEmailService sendEmailService,
                                        NotificationTemplateProcessService notificationTemplateProcessService,
                                        NotificationProperties notificationProperties) {
        this.sendEmailService = sendEmailService;
        this.notificationTemplateProcessService = notificationTemplateProcessService;
        this.notificationProperties = notificationProperties;
    }
    
    @Override
    public void notifyRecipient(EmailData emailData, String recipientEmail) {
        notifyRecipients(emailData, List.of(recipientEmail), Collections.emptyList());
    }
    
    @Override
    public void notifyRecipients(EmailData emailData, List<String> recipientsEmails, List<String> ccRecipientsEmails) {
        final NotificationContent emailNotificationContent;
            emailNotificationContent = notificationTemplateProcessService.processEmailNotificationTemplate(
                    emailData.getNotificationTemplateData().getTemplateName(),
                    emailData.getNotificationTemplateData().getCompetentAuthority(),
                    emailData.getNotificationTemplateData().getTemplateParams());

        Email email = Email.builder()
            .sendFrom(notificationProperties.getEmail().getAutoSender())
            .sendTo(recipientsEmails)
            .sendCc(ccRecipientsEmails)
            .subject(emailNotificationContent.getSubject())
            .text(createEmailText(emailNotificationContent))
            .attachments(emailData.getAttachments())
            .build();
        
        //send the email
        CompletableFuture.runAsync(() -> sendEmailService.sendMail(email));
    }
    
    protected String createEmailText(NotificationContent notificationContent) {
        return notificationContent.getText();
    }

}
