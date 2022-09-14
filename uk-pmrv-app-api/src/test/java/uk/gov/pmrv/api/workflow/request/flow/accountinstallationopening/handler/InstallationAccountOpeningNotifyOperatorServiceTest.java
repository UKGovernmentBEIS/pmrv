package uk.gov.pmrv.api.workflow.request.flow.accountinstallationopening.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.notification.mail.constants.EmailNotificationTemplateConstants;
import uk.gov.pmrv.api.notification.mail.domain.EmailData;
import uk.gov.pmrv.api.notification.mail.service.NotificationEmailService;
import uk.gov.pmrv.api.notification.template.domain.enumeration.NotificationTemplateName;
import uk.gov.pmrv.api.user.application.UserService;
import uk.gov.pmrv.api.user.core.domain.dto.ApplicationUserDTO;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.Decision;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.accountinstallationopening.domain.AccountOpeningDecisionPayload;
import uk.gov.pmrv.api.workflow.request.flow.accountinstallationopening.domain.InstallationAccountOpeningRequestPayload;

@ExtendWith(MockitoExtension.class)
class InstallationAccountOpeningNotifyOperatorServiceTest {

	@InjectMocks
	private InstallationAccountOpeningNotifyOperatorService installationAccountOpeningNotifyOperatorService;
	
	@Mock
	private NotificationEmailService notificationEmailService;
	
	@Mock
	private UserService userService;
	
	@Mock
	private RequestService requestService;
	
	@Test
	void execute_application_accepted() {
		//prepare data
		final String requestId = "1";
		final CompetentAuthority ca = CompetentAuthority.ENGLAND;
        final String user = "user";
        final String email = "email@email.gr";
        final String firstName = "fn";
        final String lastName = "ln";
		final Request request = Request.builder()
            .id(requestId)
            .competentAuthority(ca)
            .payload(InstallationAccountOpeningRequestPayload.builder()
                .payloadType(RequestPayloadType.INSTALLATION_ACCOUNT_OPENING_REQUEST_PAYLOAD)
                .accountOpeningDecisionPayload(AccountOpeningDecisionPayload.builder()
                    .decision(Decision.ACCEPTED)
                    .reason("reason")
                    .build())
                .operatorAssignee(user)
                .build())
            .build();
		final ApplicationUserDTO userDTO = OperatorUserDTO.builder().email(email).firstName(firstName).lastName(lastName).build();
		
		when(requestService.findRequestById(requestId)).thenReturn(request);
		when(userService.getUserById(user)).thenReturn(userDTO);
		
		//invoke
		installationAccountOpeningNotifyOperatorService.execute(requestId);
		
		//verify
		verify(requestService, times(1)).findRequestById(requestId);
		verify(userService, times(1)).getUserById(user);
		
		final ArgumentCaptor<EmailData> recipientEmailCaptor = ArgumentCaptor.forClass(EmailData.class);
		verify(notificationEmailService, times(1)).notifyRecipient(recipientEmailCaptor.capture(), Mockito.eq(email));
		//assert email argument
		EmailData emailData = recipientEmailCaptor.getValue();
		assertThat(emailData.getNotificationTemplateData().getTemplateName()).isEqualTo(NotificationTemplateName.ACCOUNT_APPLICATION_ACCEPTED);
		assertThat(emailData.getNotificationTemplateData().getTemplateParams())
					.containsExactlyInAnyOrderEntriesOf(
							Map.of(
									EmailNotificationTemplateConstants.COMPETENT_AUTHORITY_NAME, ca.getName(),
									EmailNotificationTemplateConstants.COMPETENT_AUTHORITY_EMAIL, ca.getEmail(),
									EmailNotificationTemplateConstants.COMPETENT_AUTHORITY_PHONE, ca.getPhone(),
									EmailNotificationTemplateConstants.APPLICANT_FNAME, userDTO.getFirstName(),
									EmailNotificationTemplateConstants.APPLICANT_LNAME, userDTO.getLastName()
									));
	}
	
	@Test
	void execute_application_rejected() {
		//prepare data
		final String requestId = "1";
		final CompetentAuthority ca = CompetentAuthority.ENGLAND;
        final String user = "user";
        final String email = "email@email.gr";
        final String firstName = "fn";
        final String lastName = "ln";
        final String rejectionReason = "reason";
		final Request request = Request.builder()
            .id(requestId)
            .competentAuthority(ca)
            .payload(InstallationAccountOpeningRequestPayload.builder()
                .payloadType(RequestPayloadType.INSTALLATION_ACCOUNT_OPENING_REQUEST_PAYLOAD)
                .accountOpeningDecisionPayload(AccountOpeningDecisionPayload.builder()
                    .decision(Decision.REJECTED)
                    .reason(rejectionReason)
                    .build())
                .operatorAssignee(user)
                .build())
            .build();
		final ApplicationUserDTO userDTO = OperatorUserDTO.builder().email(email).firstName(firstName).lastName(lastName).build();
		
		when(requestService.findRequestById(requestId)).thenReturn(request);
		when(userService.getUserById(user)).thenReturn(userDTO);
		
		//invoke
		installationAccountOpeningNotifyOperatorService.execute(requestId);
		
		//verify
		verify(requestService, times(1)).findRequestById(requestId);
		verify(userService, times(1)).getUserById(user);
		
		final ArgumentCaptor<EmailData> recipientEmailCaptor = ArgumentCaptor.forClass(EmailData.class);
		verify(notificationEmailService, times(1)).notifyRecipient(recipientEmailCaptor.capture(), Mockito.eq(email));
		//assert email argument
		EmailData emailData = recipientEmailCaptor.getValue();
		assertThat(emailData.getNotificationTemplateData().getTemplateName()).isEqualTo(NotificationTemplateName.ACCOUNT_APPLICATION_REJECTED);
		assertThat(emailData.getNotificationTemplateData().getTemplateParams())
					.containsExactlyInAnyOrderEntriesOf(
							Map.of(
									EmailNotificationTemplateConstants.COMPETENT_AUTHORITY_NAME, ca.getName(),
									EmailNotificationTemplateConstants.COMPETENT_AUTHORITY_EMAIL, ca.getEmail(),
									EmailNotificationTemplateConstants.COMPETENT_AUTHORITY_PHONE, ca.getPhone(),
									EmailNotificationTemplateConstants.APPLICANT_FNAME, userDTO.getFirstName(),
									EmailNotificationTemplateConstants.APPLICANT_LNAME, userDTO.getLastName(),
									EmailNotificationTemplateConstants.ACCOUNT_APPLICATION_REJECTED_REASON, rejectionReason
									));
	}
	
}
