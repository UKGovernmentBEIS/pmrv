package uk.gov.pmrv.api.workflow.request.flow.common.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.account.domain.dto.AccountDTO;
import uk.gov.pmrv.api.account.service.AccountQueryService;
import uk.gov.pmrv.api.notification.mail.constants.EmailNotificationTemplateConstants;
import uk.gov.pmrv.api.notification.mail.domain.EmailData;
import uk.gov.pmrv.api.notification.mail.domain.EmailNotificationTemplateData;
import uk.gov.pmrv.api.notification.mail.service.NotificationEmailService;
import uk.gov.pmrv.api.notification.template.domain.enumeration.NotificationTemplateName;
import uk.gov.pmrv.api.permit.service.PermitQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.NotificationTemplateExpirationReminderParams;

@Service
@RequiredArgsConstructor
public class RequestExpirationReminderService {

    private final RequestService requestService;
    private final AccountQueryService accountQueryService;
    private final PermitQueryService permitQueryService;
    private final NotificationEmailService notificationEmailService;
    
    public void sendExpirationReminderNotification(String requestId, NotificationTemplateExpirationReminderParams expirationParams) {
        final Request request = requestService.findRequestById(requestId);
        final Long accountId = request.getAccountId();
        final String permitId = permitQueryService.getPermitIdByAccountId(accountId).orElse(null);
        final AccountDTO account = accountQueryService.getAccountDTOById(accountId);
        
        final Map<String, Object> templateParams = new HashMap<>();
        templateParams.put(EmailNotificationTemplateConstants.ACCOUNT_LEGAL_ENTITY_NAME, account.getLegalEntity().getName());
        templateParams.put(EmailNotificationTemplateConstants.ACCOUNT_NAME, account.getName());
        templateParams.put(EmailNotificationTemplateConstants.PERMIT_ID, permitId);
        templateParams.put(EmailNotificationTemplateConstants.WORKFLOW_ID, request.getId());
        templateParams.put(EmailNotificationTemplateConstants.WORKFLOW, request.getType().getDescription());
        templateParams.put(EmailNotificationTemplateConstants.WORKFLOW_TASK, expirationParams.getWorkflowTask());
        templateParams.put(EmailNotificationTemplateConstants.WORKFLOW_USER, expirationParams.getRecipient().getFullName());
        templateParams.put(EmailNotificationTemplateConstants.WORKFLOW_EXPIRATION_TIME, expirationParams.getExpirationTime());
        templateParams.put(EmailNotificationTemplateConstants.WORKFLOW_EXPIRATION_TIME_LONG, expirationParams.getExpirationTimeLong());
        templateParams.put(EmailNotificationTemplateConstants.WORKFLOW_DEADLINE, expirationParams.getDeadline());
        templateParams.put(EmailNotificationTemplateConstants.COMPETENT_AUTHORITY_EMAIL, request.getCompetentAuthority().getEmail());
        
        final EmailData emailData = EmailData.builder()
                .notificationTemplateData(EmailNotificationTemplateData.builder()
                        .competentAuthority(request.getCompetentAuthority())
                        .templateName(NotificationTemplateName.GENERIC_EXPIRATION_REMINDER)
                        .templateParams(templateParams)
                        .build())
                .build();
        
        notificationEmailService.notifyRecipient(emailData, expirationParams.getRecipient().getEmail());
    }
}
