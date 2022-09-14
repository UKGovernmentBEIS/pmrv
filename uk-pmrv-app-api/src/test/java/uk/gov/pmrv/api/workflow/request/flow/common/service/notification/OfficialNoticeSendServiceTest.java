package uk.gov.pmrv.api.workflow.request.flow.common.service.notification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.files.common.domain.dto.FileDTO;
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

@ExtendWith(MockitoExtension.class)
class OfficialNoticeSendServiceTest {

	@InjectMocks
    private OfficialNoticeSendService service;
	
	@Mock
    private RequestAccountContactQueryService requestAccountContactQueryService;
	
	@Mock
    private NotificationEmailService notificationEmailService;
    
    @Mock
    private FileDocumentService fileDocumentService;
    
    @Test
    void sendOfficialNotice() {
    	FileInfoDTO officialDocFileInfoDTO = FileInfoDTO.builder()
                .name("offDoc.pdf")
                .uuid(UUID.randomUUID().toString())
                .build();
    	
    	Request request = Request.builder()
        		.id("1")
                .competentAuthority(CompetentAuthority.ENGLAND)
                .build();
    	
    	UserInfoDTO accountPrimaryContact = UserInfoDTO.builder()
                .firstName("fn").lastName("ln").email("primary@email").userId("primaryUserId")
                .build();
    	
    	FileDTO officialDocFileDTO = FileDTO.builder().fileContent("content".getBytes()).build();
    	
    	List<String> ccRecipientsEmails = List.of("cc1@email", "cc2@email");
    	
    	when(requestAccountContactQueryService.getRequestAccountPrimaryContact(request))
    		.thenReturn(accountPrimaryContact);
    	when(fileDocumentService.getFileDTO(officialDocFileInfoDTO.getUuid()))
    		.thenReturn(officialDocFileDTO);
    	
    	service.sendOfficialNotice(officialDocFileInfoDTO, request, ccRecipientsEmails);
    	
    	verify(requestAccountContactQueryService, times(1)).getRequestAccountPrimaryContact(request);
    	verify(fileDocumentService, times(1)).getFileDTO(officialDocFileInfoDTO.getUuid());
    	
		ArgumentCaptor<EmailData> emailDataCaptor = ArgumentCaptor.forClass(EmailData.class);
		verify(notificationEmailService, times(1)).notifyRecipients(emailDataCaptor.capture(),
				Mockito.eq(List.of(accountPrimaryContact.getEmail())), Mockito.eq(ccRecipientsEmails));
		EmailData emailDataCaptured = emailDataCaptor.getValue();
		assertThat(emailDataCaptured).isEqualTo(EmailData.builder()
				.notificationTemplateData(EmailNotificationTemplateData.builder()
						.templateName(NotificationTemplateName.GENERIC_INSTALLATION_LETTER)
						.competentAuthority(CompetentAuthority.ENGLAND)
						.templateParams(Map.of(EmailNotificationTemplateConstants.ACCOUNT_PRIMARY_CONTACT,
								accountPrimaryContact.getFullName()))
						.build())
				.attachments(Map.of(officialDocFileInfoDTO.getName(), officialDocFileDTO.getFileContent())).build());
    	
    	
    }
}
