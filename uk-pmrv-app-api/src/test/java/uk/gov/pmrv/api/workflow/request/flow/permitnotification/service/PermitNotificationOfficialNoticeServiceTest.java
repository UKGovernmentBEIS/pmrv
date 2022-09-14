package uk.gov.pmrv.api.workflow.request.flow.permitnotification.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.notification.mail.constants.EmailNotificationTemplateConstants;
import uk.gov.pmrv.api.notification.mail.domain.EmailData;
import uk.gov.pmrv.api.notification.mail.domain.EmailNotificationTemplateData;
import uk.gov.pmrv.api.notification.mail.service.NotificationEmailService;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.TemplateParams;
import uk.gov.pmrv.api.notification.template.domain.enumeration.DocumentTemplateType;
import uk.gov.pmrv.api.notification.template.domain.enumeration.NotificationTemplateName;
import uk.gov.pmrv.api.notification.template.service.DocumentFileGeneratorService;
import uk.gov.pmrv.api.user.core.domain.dto.UserInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.service.DecisionNotificationUsersService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestAccountContactQueryService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateParamsProvider;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateParamsSourceData;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationRequestPayload;

@ExtendWith(MockitoExtension.class)
class PermitNotificationOfficialNoticeServiceTest {

    @InjectMocks
    private PermitNotificationOfficialNoticeService service;

    @Mock
    private RequestAccountContactQueryService requestAccountContactQueryService;

    @Mock
    private DecisionNotificationUsersService decisionNotificationUsersService;

    @Mock
    private NotificationEmailService notificationEmailService;
    
    @Mock
    private RequestService requestService;

    @Mock
    private DocumentTemplateParamsProvider documentTemplateParamsProvider;

    @Mock
    private DocumentFileGeneratorService documentFileGeneratorService;

    @Test
    void sendFollowUpOfficialNotice() {

        final PermitNotificationRequestPayload requestPayload = PermitNotificationRequestPayload.builder()
            .followUpReviewDecisionNotification(DecisionNotification.builder()
                .operators(Set.of("operator1"))
                .signatory("signatory")
                .build())
            .build();
        final Request request = Request.builder()
            .id("requestId")
            .competentAuthority(CompetentAuthority.ENGLAND)
            .payload(requestPayload)
            .build();
        final UserInfoDTO accountPrimaryContact = UserInfoDTO.builder()
            .firstName("fn").lastName("ln").email("primary@email").userId("primaryUserId").build();
        final List<String> ccRecipientsEmails = List.of("operator1@email");

        when(requestAccountContactQueryService.getRequestAccountPrimaryContact(request))
            .thenReturn(accountPrimaryContact);
        when(decisionNotificationUsersService.findUserEmails(requestPayload.getFollowUpReviewDecisionNotification()))
            .thenReturn(ccRecipientsEmails);

        service.sendFollowUpOfficialNotice(request);

        verify(requestAccountContactQueryService, times(1))
            .getRequestAccountPrimaryContact(request);
        verify(decisionNotificationUsersService, times(1))
            .findUserEmails(requestPayload.getFollowUpReviewDecisionNotification());

        final ArgumentCaptor<EmailData> emailDataCaptor = ArgumentCaptor.forClass(EmailData.class);
        verify(notificationEmailService, times(1)).notifyRecipients(emailDataCaptor.capture(),
            Mockito.eq(List.of(accountPrimaryContact.getEmail())), Mockito.eq(List.of("operator1@email")));
        final EmailData emailDataCaptured = emailDataCaptor.getValue();
        assertThat(emailDataCaptured).isEqualTo(EmailData.builder()
            .notificationTemplateData(EmailNotificationTemplateData.builder()
                .templateName(NotificationTemplateName.PERMIT_NOTIFICATION_OPERATOR_RESPONSE)
                .competentAuthority(CompetentAuthority.ENGLAND)
                .templateParams(Map.of(
                    EmailNotificationTemplateConstants.WORKFLOW_ID, "requestId",
                    EmailNotificationTemplateConstants.ACCOUNT_PRIMARY_CONTACT,accountPrimaryContact.getFullName()))
                .build()).build());
    }

    @Test
    void generateAndSaveGrantedOfficialNotice() {

        final String requestId = "1";
        final PermitNotificationRequestPayload requestPayload = PermitNotificationRequestPayload.builder()
            .reviewDecisionNotification(DecisionNotification.builder()
                .operators(Set.of("operator1"))
                .signatory("signatory")
                .build())
            .build();
        final Request request = Request.builder()
            .competentAuthority(CompetentAuthority.ENGLAND)
            .payload(requestPayload)
            .build();
        final UserInfoDTO accountPrimaryContact = UserInfoDTO.builder()
            .firstName("fn").lastName("ln").email("primary@email").userId("primaryUserId")
            .build();
        final List<String> ccRecipientsEmails = List.of("operator1@email");
        final DocumentTemplateParamsSourceData
            templateSourceParams = DocumentTemplateParamsSourceData.builder()
            .contextActionType(DocumentTemplateGenerationContextActionType.PERMIT_NOTIFICATION_GRANTED)
            .request(request)
            .signatory("signatory")
            .accountPrimaryContact(accountPrimaryContact)
            .toRecipientEmail(accountPrimaryContact.getEmail())
            .ccRecipientsEmails(List.of("operator1@email"))
            .build();
        final FileInfoDTO officialDocFileInfoDTO = FileInfoDTO.builder()
            .name("offDoc.pdf")
            .uuid(UUID.randomUUID().toString())
            .build();
        final TemplateParams templateParams = TemplateParams.builder().build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(requestAccountContactQueryService.getRequestAccountPrimaryContact(request)).thenReturn(accountPrimaryContact);
        when(decisionNotificationUsersService.findUserEmails(requestPayload.getReviewDecisionNotification()))
        	.thenReturn(ccRecipientsEmails);
        when(documentTemplateParamsProvider.constructTemplateParams(templateSourceParams))
            .thenReturn(templateParams);
        when(documentFileGeneratorService.generateFileDocument(DocumentTemplateType.PERMIT_NOTIFICATION_ACCEPTED, templateParams, "Permit Notification Acknowledgement Letter.pdf"))
            .thenReturn(officialDocFileInfoDTO);

        service.generateAndSaveGrantedOfficialNotice(requestId);

        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestAccountContactQueryService, times(1)).getRequestAccountPrimaryContact(request);
        verify(decisionNotificationUsersService, times(1))
        .findUserEmails(requestPayload.getReviewDecisionNotification());
        verify(documentTemplateParamsProvider, times(1)).constructTemplateParams(templateSourceParams);
        verify(documentFileGeneratorService, times(1)).generateFileDocument(DocumentTemplateType.PERMIT_NOTIFICATION_ACCEPTED, templateParams, "Permit Notification Acknowledgement Letter.pdf");

        assertThat(requestPayload.getOfficialNotice()).isEqualTo(officialDocFileInfoDTO);
    }
    
    @Test
    void generateAndSaveRejectedOfficialNotice() {

        final String requestId = "1";
        final PermitNotificationRequestPayload requestPayload = PermitNotificationRequestPayload.builder()
            .reviewDecisionNotification(DecisionNotification.builder()
                .operators(Set.of("operator1"))
                .signatory("signatory")
                .build())
            .build();
        final Request request = Request.builder()
            .competentAuthority(CompetentAuthority.ENGLAND)
            .payload(requestPayload)
            .build();
        final UserInfoDTO accountPrimaryContact = UserInfoDTO.builder()
            .firstName("fn").lastName("ln").email("primary@email").userId("primaryUserId")
            .build();
        final List<String> ccRecipientsEmails = List.of("operator1@email");
        final DocumentTemplateParamsSourceData
            templateSourceParams = DocumentTemplateParamsSourceData.builder()
            .contextActionType(DocumentTemplateGenerationContextActionType.PERMIT_NOTIFICATION_REJECTED)
            .request(request)
            .signatory("signatory")
            .accountPrimaryContact(accountPrimaryContact)
            .toRecipientEmail(accountPrimaryContact.getEmail())
            .ccRecipientsEmails(List.of("operator1@email"))
            .build();
        final FileInfoDTO officialDocFileInfoDTO = FileInfoDTO.builder()
            .name("offDoc.pdf")
            .uuid(UUID.randomUUID().toString())
            .build();
        final TemplateParams templateParams = TemplateParams.builder().build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(requestAccountContactQueryService.getRequestAccountPrimaryContact(request)).thenReturn(accountPrimaryContact);
        when(decisionNotificationUsersService.findUserEmails(requestPayload.getReviewDecisionNotification()))
        	.thenReturn(ccRecipientsEmails);
        when(documentTemplateParamsProvider.constructTemplateParams(templateSourceParams))
            .thenReturn(templateParams);
        when(documentFileGeneratorService.generateFileDocument(DocumentTemplateType.PERMIT_NOTIFICATION_REFUSED, templateParams, "Permit Notification Refusal Letter.pdf"))
            .thenReturn(officialDocFileInfoDTO);

        service.generateAndSaveRejectedOfficialNotice(requestId);

        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestAccountContactQueryService, times(1)).getRequestAccountPrimaryContact(request);
        verify(decisionNotificationUsersService, times(1))
        .findUserEmails(requestPayload.getReviewDecisionNotification());
        verify(documentTemplateParamsProvider, times(1)).constructTemplateParams(templateSourceParams);
        verify(documentFileGeneratorService, times(1)).generateFileDocument(DocumentTemplateType.PERMIT_NOTIFICATION_REFUSED, templateParams, "Permit Notification Refusal Letter.pdf");

        assertThat(requestPayload.getOfficialNotice()).isEqualTo(officialDocFileInfoDTO);
    }
}
