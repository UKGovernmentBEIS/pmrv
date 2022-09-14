package uk.gov.pmrv.api.workflow.request.flow.common.service.notification;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.TemplateParams;
import uk.gov.pmrv.api.notification.template.domain.enumeration.DocumentTemplateType;
import uk.gov.pmrv.api.notification.template.service.DocumentFileGeneratorService;
import uk.gov.pmrv.api.user.core.domain.dto.UserInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.service.DecisionNotificationUsersService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestAccountContactQueryService;

@ExtendWith(MockitoExtension.class)
class OfficialNoticeGeneratorServiceTest {

    @InjectMocks
    private OfficialNoticeGeneratorService officialNoticeGeneratorService;

    @Mock
    private RequestAccountContactQueryService requestAccountContactQueryService;

    @Mock
    private DecisionNotificationUsersService decisionNotificationUsersService;

    @Mock
    private DocumentTemplateParamsProvider documentTemplateParamsProvider;

    @Mock
    private DocumentFileGeneratorService documentFileGeneratorService;

    @Test
    void generate() {
        Request request = Request.builder().build();
        DocumentTemplateGenerationContextActionType documentTemplateContextActionType =
            DocumentTemplateGenerationContextActionType.PERMIT_SURRENDER_GRANTED;
        DocumentTemplateType documentTemplateType = DocumentTemplateType.PERMIT_SURRENDERED_NOTICE;
        DecisionNotification decisionNotification = DecisionNotification.builder()
            .operators(Set.of("operator"))
            .signatory("signatory")
            .build();
        List<String> decisionNotificationUserEmails = List.of("operator@pmrv.uk");
        String filename = "permit_surrender_notice.pdf";
        UserInfoDTO accountPrimaryContactInfo = UserInfoDTO.builder().email("user@pmrv.uk").build();
        DocumentTemplateParamsSourceData documentTemplateSourceParams =
            DocumentTemplateParamsSourceData.builder()
                .contextActionType(documentTemplateContextActionType)
                .request(request)
                .signatory(decisionNotification.getSignatory())
                .accountPrimaryContact(accountPrimaryContactInfo)
                .toRecipientEmail(accountPrimaryContactInfo.getEmail())
                .ccRecipientsEmails(decisionNotificationUserEmails)
                .build();
        TemplateParams templateParams = TemplateParams.builder().build();
        FileInfoDTO officialNotice = FileInfoDTO.builder()
            .name("offDoc.pdf")
            .uuid(UUID.randomUUID().toString())
            .build();

        when(requestAccountContactQueryService.getRequestAccountPrimaryContact(request)).thenReturn(accountPrimaryContactInfo);
        when(decisionNotificationUsersService.findUserEmails(decisionNotification)).thenReturn(decisionNotificationUserEmails);
        when(documentTemplateParamsProvider.constructTemplateParams(documentTemplateSourceParams))
            .thenReturn(templateParams);
        when(documentFileGeneratorService.generateFileDocument(documentTemplateType, templateParams, filename))
            .thenReturn(officialNotice);

        FileInfoDTO generatedOfficialNotice =
            officialNoticeGeneratorService.generate(request, documentTemplateContextActionType, documentTemplateType,
                decisionNotification, filename);

        verify(requestAccountContactQueryService, times(1)).getRequestAccountPrimaryContact(request);
        verify(decisionNotificationUsersService, times(1)).findUserEmails(decisionNotification);
        verify(documentTemplateParamsProvider, times(1)).constructTemplateParams(documentTemplateSourceParams);
        verify(documentFileGeneratorService, times(1))
            .generateFileDocument(documentTemplateType, templateParams, filename);

        assertEquals(officialNotice, generatedOfficialNotice);
    }
}