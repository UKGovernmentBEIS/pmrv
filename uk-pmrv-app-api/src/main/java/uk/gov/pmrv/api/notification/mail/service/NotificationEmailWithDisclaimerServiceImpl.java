package uk.gov.pmrv.api.notification.mail.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.notification.mail.config.property.NotificationProperties;
import uk.gov.pmrv.api.notification.template.domain.NotificationContent;
import uk.gov.pmrv.api.notification.template.service.NotificationTemplateProcessService;

/**
 * Service implementation for generating mail objects using FreeMarker Template Engine.
 * To be used in non-production environments.
 */
@Log4j2
@Service
@ConditionalOnProperty(name = "env.isProd", havingValue = "false")
public class NotificationEmailWithDisclaimerServiceImpl extends NotificationEmailServiceImpl {

    public static final String MAIL_DISCLAIMER = "***THIS EMAIL HAS BEEN SENT FROM A TEST SYSTEM. " +
            "IF YOU ARE NOT CURRENTLY PERFORMING TESTING, PLEASE DISREGARD THIS EMAIL.***";

    public NotificationEmailWithDisclaimerServiceImpl(SendEmailService sendEmailService,
                                                      NotificationTemplateProcessService notificationTemplateProcessService,
                                                      NotificationProperties notificationProperties) {
        super(sendEmailService, notificationTemplateProcessService, notificationProperties);
    }

    @Override
    protected String createEmailText(NotificationContent notificationContent) {
        StringBuilder sb = new StringBuilder(MAIL_DISCLAIMER)
                .append(System.lineSeparator())
                .append(super.createEmailText(notificationContent));
        return sb.toString();
    }
}
