package uk.gov.pmrv.api.workflow.request.flow.common.service.notification;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.files.documents.service.FileDocumentService;
import uk.gov.pmrv.api.notification.mail.constants.EmailNotificationTemplateConstants;
import uk.gov.pmrv.api.notification.mail.domain.EmailData;
import uk.gov.pmrv.api.notification.mail.domain.EmailNotificationTemplateData;
import uk.gov.pmrv.api.notification.mail.service.NotificationEmailService;
import uk.gov.pmrv.api.notification.template.domain.enumeration.NotificationTemplateName;
import uk.gov.pmrv.api.user.core.domain.dto.UserInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestAccountContactQueryService;

@Service
@RequiredArgsConstructor
public class OfficialNoticeSendService {
	
	private final RequestAccountContactQueryService requestAccountContactQueryService;
	private final NotificationEmailService notificationEmailService;
	private final FileDocumentService fileDocumentService;

	public void sendOfficialNotice(FileInfoDTO officialNotice, Request request, List<String> ccRecipientsEmails) {
        final UserInfoDTO accountPrimaryContact = requestAccountContactQueryService.getRequestAccountPrimaryContact(request);
        
        //remove from cc if is the to recipient
        List<String> ccRecipientsEmailsFinal = new ArrayList<>(ccRecipientsEmails);
        ccRecipientsEmailsFinal.removeIf(email -> email.equals(accountPrimaryContact.getEmail()));
        
        //notify 
        notificationEmailService.notifyRecipients(
                EmailData.builder()
                        .notificationTemplateData(EmailNotificationTemplateData.builder()
                                .templateName(NotificationTemplateName.GENERIC_INSTALLATION_LETTER)
                                .competentAuthority(request.getCompetentAuthority())
                                .templateParams(Map.of(EmailNotificationTemplateConstants.ACCOUNT_PRIMARY_CONTACT,
                                        accountPrimaryContact.getFullName()))
                                .build())
                        .attachments(Map.of(officialNotice.getName(),
                                fileDocumentService.getFileDTO(officialNotice.getUuid()).getFileContent()))
                        .build(),
                List.of(accountPrimaryContact.getEmail()), 
                ccRecipientsEmailsFinal);
    }
	
}
