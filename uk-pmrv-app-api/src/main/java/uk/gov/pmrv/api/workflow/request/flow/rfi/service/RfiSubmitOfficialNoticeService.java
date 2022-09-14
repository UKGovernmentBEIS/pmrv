package uk.gov.pmrv.api.workflow.request.flow.rfi.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.TemplateParams;
import uk.gov.pmrv.api.notification.template.domain.enumeration.DocumentTemplateType;
import uk.gov.pmrv.api.notification.template.service.DocumentFileGeneratorService;
import uk.gov.pmrv.api.user.core.domain.dto.UserInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateParamsProvider;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateParamsSourceData;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.OfficialNoticeSendService;

@Service
@RequiredArgsConstructor
public class RfiSubmitOfficialNoticeService {
    
    private final DocumentTemplateParamsProvider documentTemplateParamsProvider;
    private final DocumentFileGeneratorService documentFileGeneratorService;
    private final OfficialNoticeSendService officialNoticeSendService;
    
    @Transactional
    public FileInfoDTO generateOfficialNotice(Request request, String signatory,
            UserInfoDTO accountPrimaryContact, List<String> ccRecipientsEmails) {
        final TemplateParams templateParams = documentTemplateParamsProvider.constructTemplateParams(
                DocumentTemplateParamsSourceData.builder()
                    .contextActionType(DocumentTemplateGenerationContextActionType.RFI_SUBMIT)
                    .request(request)
                    .signatory(signatory)
                    .accountPrimaryContact(accountPrimaryContact)
                    .toRecipientEmail(accountPrimaryContact.getEmail())
                    .ccRecipientsEmails(ccRecipientsEmails)
                    .build()
                );

        return documentFileGeneratorService.generateFileDocument(DocumentTemplateType.IN_RFI, templateParams,
                "Request for Further Information.pdf");
    }
    
    public void sendOfficialNotice(FileInfoDTO officialNotice, 
            Request request,
            List<String> ccRecipientsEmails) {
    	officialNoticeSendService.sendOfficialNotice(officialNotice, request, ccRecipientsEmails);
    }

}
