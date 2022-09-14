package uk.gov.pmrv.api.workflow.request.flow.common.service.notification;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.account.domain.dto.AccountDTO;
import uk.gov.pmrv.api.account.domain.enumeration.AccountContactType;
import uk.gov.pmrv.api.account.service.AccountQueryService;
import uk.gov.pmrv.api.common.config.property.AppProperties;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.common.utils.ResourceFileUtil;
import uk.gov.pmrv.api.files.common.domain.dto.FileDTO;
import uk.gov.pmrv.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.AccountTemplateParams;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.CompetentAuthorityTemplateParams;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.EmailTemplateParams;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.SignatoryTemplateParams;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.TemplateParams;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.WorkflowTemplateParams;
import uk.gov.pmrv.api.permit.service.PermitQueryService;
import uk.gov.pmrv.api.user.core.domain.dto.UserInfoDTO;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;
import uk.gov.pmrv.api.user.regulator.domain.RegulatorUserDTO;
import uk.gov.pmrv.api.user.regulator.service.RegulatorUserAuthService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestAccountContactQueryService;

@Service
@RequiredArgsConstructor
public class DocumentTemplateParamsProvider {

    private final List<DocumentTemplateWorkflowParamsProvider> workflowParamsProviders;
    private final AccountQueryService accountQueryService;
    private final PermitQueryService permitQueryService;
    private final RequestAccountContactQueryService requestAccountContactQueryService;
    private final DocumentTemplateLocationInfoResolver documentTemplateLocationInfoResolver;
    private final RegulatorUserAuthService regulatorUserAuthService;
    private final UserAuthService userAuthService;
    private final AppProperties appProperties;
    
    public TemplateParams constructTemplateParams(DocumentTemplateParamsSourceData templateSourceParams) {
        final AccountDTO account = accountQueryService.getAccountDTOById(templateSourceParams.getRequest().getAccountId());
        final String permitId = permitQueryService.getPermitIdByAccountId(templateSourceParams.getRequest().getAccountId()).orElse(null);
        
        // Email params
        List<String> ccRecipientsEmailsFinal = new ArrayList<>(templateSourceParams.getCcRecipientsEmails());
        ccRecipientsEmailsFinal.removeIf(email -> email.equals(templateSourceParams.getAccountPrimaryContact().getEmail()));
        EmailTemplateParams emailParams = EmailTemplateParams.builder()
                .toRecipient(templateSourceParams.getToRecipientEmail())
                .ccRecipients(ccRecipientsEmailsFinal)
                .build();
        
        // CA params
        CompetentAuthority competentAuthority = account.getCompetentAuthority();
        CompetentAuthorityTemplateParams competentAuthorityParams = CompetentAuthorityTemplateParams.builder()
                .competentAuthority(competentAuthority)
                .logo(ResourceFileUtil.getCompetentAuthorityLogo(competentAuthority))
                .build();
        
        // account params
        String accountServiceContactFullName = requestAccountContactQueryService
                .getRequestAccountContact(templateSourceParams.getRequest(), AccountContactType.SERVICE)
                .map(UserInfoDTO::getFullName)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
        AccountTemplateParams accountParams = AccountTemplateParams.builder()
                .name(account.getName())
                .emitterType(account.getEmitterType() != null ? account.getEmitterType().name() : null)
                .siteName(account.getSiteName())
                .location(documentTemplateLocationInfoResolver.constructLocationInfo(account.getLocation()))
                .legalEntityName(account.getLegalEntity().getName())
                .legalEntityLocation(documentTemplateLocationInfoResolver.constructAddressInfo(account.getLegalEntity().getAddress()))
                .primaryContact(templateSourceParams.getAccountPrimaryContact().getFullName())
                .serviceContact(accountServiceContactFullName)
                .build();
        
        // Signatory params
        RegulatorUserDTO signatoryUser = regulatorUserAuthService.getRegulatorUserById(templateSourceParams.getSignatory());
        FileInfoDTO signatureInfo = signatoryUser.getSignature();
        if (signatureInfo == null) {
            throw new BusinessException(ErrorCode.USER_SIGNATURE_NOT_EXIST, templateSourceParams.getSignatory());
        }
        FileDTO signatorySignature = userAuthService.getUserSignature(signatureInfo.getUuid())
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, signatureInfo.getUuid()));
        SignatoryTemplateParams signatoryParams = SignatoryTemplateParams.builder()
                .fullName(signatoryUser.getFullName())
                .jobTitle(signatoryUser.getJobTitle())
                .signature(signatorySignature.getFileContent())
                .build();
        
        // Workflow params
        WorkflowTemplateParams workflowParams = WorkflowTemplateParams.builder()
                .requestId(templateSourceParams.getRequest().getId())
                .requestSubmissionDate(Date.from(templateSourceParams.getRequest().getSubmissionDate().atZone(ZoneId.systemDefault()).toInstant()))
                .requestTypeInfo(RequestTypeDocumentTemplateInfoMapper.getTemplateInfo(templateSourceParams.getRequest().getType()).toLowerCase())
                .build();
        workflowParamsProviders.stream()
            .filter(provider -> provider.getContextActionType() == templateSourceParams.getContextActionType())
            .findFirst()
            .ifPresent((workflowParamsProvider) -> workflowParams.getParams().putAll(workflowParamsProvider.constructParams(templateSourceParams.getRequest().getPayload())));
        
        return TemplateParams.builder()
                .emailParams(emailParams)
                .competentAuthorityParams(competentAuthorityParams)
                .competentAuthorityCentralInfo(appProperties.getCompetentAuthorityCentralInfo())
                .signatoryParams(signatoryParams)
                .accountParams(accountParams)
                .workflowParams(workflowParams)
                .permitId(permitId)
                .build();
    }
}
