package uk.gov.pmrv.api.workflow.request.flow.common.service.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.TemplateParams;
import uk.gov.pmrv.api.notification.template.domain.enumeration.DocumentTemplateType;
import uk.gov.pmrv.api.notification.template.service.DocumentFileGeneratorService;
import uk.gov.pmrv.api.user.core.domain.dto.UserInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.service.DecisionNotificationUsersService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestAccountContactQueryService;

@Service
@RequiredArgsConstructor
public class OfficialNoticeGeneratorService {

    private final RequestAccountContactQueryService requestAccountContactQueryService;
    private final DecisionNotificationUsersService decisionNotificationUsersService;
    private final DocumentTemplateParamsProvider documentTemplateParamsProvider;
    private final DocumentFileGeneratorService documentFileGeneratorService;

    public FileInfoDTO generate(Request request, DocumentTemplateGenerationContextActionType type,
                                DocumentTemplateType documentTemplateType, DecisionNotification decisionNotification,
                                String fileNameToGenerate) {
        final UserInfoDTO accountPrimaryContact = requestAccountContactQueryService.getRequestAccountPrimaryContact(request);

        final TemplateParams templateParams = documentTemplateParamsProvider.constructTemplateParams(
            DocumentTemplateParamsSourceData.builder()
                .contextActionType(type)
                .request(request)
                .signatory(decisionNotification.getSignatory())
                .accountPrimaryContact(accountPrimaryContact)
                .toRecipientEmail(accountPrimaryContact.getEmail())
                .ccRecipientsEmails(decisionNotificationUsersService.findUserEmails(decisionNotification))
                .build()
        );

        return documentFileGeneratorService.generateFileDocument(
            documentTemplateType, templateParams, fileNameToGenerate);
    }
}
