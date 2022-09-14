package uk.gov.pmrv.api.notification.template.service;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.common.exception.BusinessCheckedException;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.notification.template.domain.NotificationContent;
import uk.gov.pmrv.api.notification.template.domain.NotificationTemplate;
import uk.gov.pmrv.api.notification.template.domain.enumeration.NotificationTemplateName;
import uk.gov.pmrv.api.notification.template.repository.NotificationTemplateRepository;
import uk.gov.pmrv.api.notification.template.utils.MarkdownUtils;

import java.io.IOException;
import java.io.StringReader;
import java.util.Map;

import static uk.gov.pmrv.api.common.exception.ErrorCode.EMAIL_TEMPLATE_NOT_FOUND;
import static uk.gov.pmrv.api.common.exception.ErrorCode.EMAIL_TEMPLATE_PROCESSING_FAILED;

/**
 * Service for processing notification templates using FreeMarker Template Engine.
 */
@Log4j2
@RequiredArgsConstructor
@Service
public class NotificationTemplateProcessService {

    private final Configuration freemarkerConfig;
    private final NotificationTemplateRepository notificationTemplateRepository;
    
    /**
     * Process the provided template with the given parameters, using the FreeMarker Template Engine.
     * @param templateName the {@link NotificationTemplateName}
     * @param competentAuthority
     * @param parameters {@link Map} that contains parameter names as keys and parameter objects as values
     * @return {@link NotificationContent} that encapsulates the processing result
     */
    @Transactional(readOnly = true)
    public NotificationContent processEmailNotificationTemplate(NotificationTemplateName templateName,
            CompetentAuthority competentAuthority, Map<String, Object> parameters) {
        return processNotificationTemplate(templateName, competentAuthority, parameters, true);
    }

    /**
     * Process the provided template with the given parameters, using the FreeMarker Template Engine.
     * @param templateName the {@link NotificationTemplateName}
     * @param parameters {@link Map} that contains parameter names as keys and parameter objects as values
     * @return {@link NotificationContent} that encapsulates the processing result
     * @throws BusinessCheckedException the {@link BusinessCheckedException}
     */
    @Transactional(readOnly = true)
    public NotificationContent processMessageNotificationTemplate(NotificationTemplateName templateName, 
            Map<String, Object> parameters) {
        return processNotificationTemplate(templateName, null, parameters, false);
    }

    private NotificationContent processNotificationTemplate(NotificationTemplateName templateName,
            CompetentAuthority competentAuthority, Map<String, Object> parameters, boolean parseToHtml) {

        NotificationTemplate notificationTemplate =  notificationTemplateRepository.findByNameAndCompetentAuthority(templateName, competentAuthority)
                .orElseThrow(() -> new BusinessException(EMAIL_TEMPLATE_NOT_FOUND, "Email Template Not Found: " + templateName.getName()));

        String processedSubject = processTemplateIntoString(templateName, notificationTemplate.getSubject(), parameters);
        String processedText = processTemplateIntoString(templateName,
            parseToHtml ? MarkdownUtils.parseToHtml(notificationTemplate.getText()) : notificationTemplate.getText(),
            parameters);

        return NotificationContent.builder().subject(processedSubject).text(processedText).build();
    }

    private String processTemplateIntoString(NotificationTemplateName templateName, String text,
                                            Map<String, Object> model) {
        String result;
        try {
            Template template = new Template(templateName.getName(), new StringReader(text), freemarkerConfig);
            result = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
        } catch (IOException | TemplateException e) {
            log.error("Error during template processing, {}", e::getMessage);
            throw new BusinessException(EMAIL_TEMPLATE_PROCESSING_FAILED, templateName.getName());
        }
        return result;
    }

}
