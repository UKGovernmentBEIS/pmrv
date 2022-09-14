package uk.gov.pmrv.api.workflow.request.flow.common.service.notification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.account.domain.dto.AccountDTO;
import uk.gov.pmrv.api.account.domain.dto.LegalEntityDTO;
import uk.gov.pmrv.api.account.domain.dto.LocationOnShoreDTO;
import uk.gov.pmrv.api.account.domain.enumeration.AccountContactType;
import uk.gov.pmrv.api.account.domain.enumeration.EmitterType;
import uk.gov.pmrv.api.account.domain.enumeration.LocationType;
import uk.gov.pmrv.api.account.service.AccountQueryService;
import uk.gov.pmrv.api.common.config.property.AppProperties;
import uk.gov.pmrv.api.common.domain.dto.AddressDTO;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
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
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestAccountContactQueryService;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.common.domain.PermitIssuanceRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.rfi.service.RfiSubmitDocumentTemplateWorkflowParamsProvider;

@ExtendWith(MockitoExtension.class)
class DocumentTemplateParamsProviderTest {

    @InjectMocks
    private DocumentTemplateParamsProvider provider;
    
    @Mock
    private RfiSubmitDocumentTemplateWorkflowParamsProvider rfiSubmitDocumentTemplateWorkflowParamsProvider;

    @Spy
    private ArrayList<DocumentTemplateWorkflowParamsProvider> workflowParamsProviders;
    
    @Mock
    private AccountQueryService accountQueryService;

    @Mock
    private PermitQueryService permitQueryService;
    
    @Mock
    private RequestAccountContactQueryService requestAccountContactQueryService;
    
    @Mock
    private DocumentTemplateLocationInfoResolver documentTemplateLocationInfoResolver;
    
    @Mock
    private RegulatorUserAuthService regulatorUserAuthService;
    
    @Mock
    private UserAuthService userAuthService;
    
    @Mock
    private AppProperties appProperties;
    
    @BeforeEach
    public void setUp() {
        workflowParamsProviders.add(rfiSubmitDocumentTemplateWorkflowParamsProvider);
    }

    @Test
    void constructTemplateParams() throws IOException {
        LocalDateTime submissionDate = LocalDateTime.now();
        Long accountId = 1L;
        PermitIssuanceRequestPayload requestPayload = PermitIssuanceRequestPayload.builder()
                .build();
        Request request = Request.builder().id("1").accountId(accountId)
                .type(RequestType.PERMIT_ISSUANCE).submissionDate(submissionDate)
                .payload(requestPayload)
                .build();
        AccountDTO account = AccountDTO.builder()
                .id(accountId)
                .name("accountname")
                .emitterType(EmitterType.GHGE)
                .competentAuthority(CompetentAuthority.ENGLAND)
                .siteName("accountsitename")
                .location(LocationOnShoreDTO.builder()
                        .type(LocationType.ONSHORE)
                        .gridReference("gridRef")
                        .address(AddressDTO.builder()
                                .line1("line1")
                                .line2("line2")
                                .city("city")
                                .country("GR")
                                .postcode("15125")
                                .build())
                        .build())
                .legalEntity(LegalEntityDTO.builder()
                        .name("lename")
                        .address(AddressDTO.builder()
                                .line1("le_line1")
                                .line2("le_line2")
                                .city("le_city")
                                .country("GR")
                                .postcode("15125")
                                .build())
                        .build())
                .build();
        String signatory = "signatoryUserId";
        UserInfoDTO accountPrimaryContact = UserInfoDTO.builder()
                .firstName("fn").lastName("ln").email("email@email")
                .build();
        String permitId = "permitId";
        
        List<String> ccRecipientsEmails = List.of("cc1@email", "cc2@email");
        
        DocumentTemplateParamsSourceData sourceParams = DocumentTemplateParamsSourceData.builder()
                .contextActionType(DocumentTemplateGenerationContextActionType.RFI_SUBMIT)
                .accountPrimaryContact(accountPrimaryContact)
                .request(request)
                .signatory(signatory)
                .toRecipientEmail(accountPrimaryContact.getEmail())
                .ccRecipientsEmails(ccRecipientsEmails)
                .build();
        
        String caCentralInfo = "ca central info";
        
        when(accountQueryService.getAccountDTOById(request.getAccountId())).thenReturn(account);
        when(permitQueryService.getPermitIdByAccountId(request.getAccountId())).thenReturn(Optional.of(permitId));
        when(requestAccountContactQueryService.getRequestAccountContact(request, AccountContactType.SERVICE))
            .thenReturn(Optional.of(UserInfoDTO.builder()
                    .firstName("service fn")
                    .lastName("service ln")
                    .userId("serviceContact")
                    .build()));
        when(documentTemplateLocationInfoResolver.constructLocationInfo(account.getLocation()))
            .thenReturn("accountLocation");
        when(documentTemplateLocationInfoResolver.constructAddressInfo(account.getLegalEntity().getAddress()))
            .thenReturn("leLocation");
        
        RegulatorUserDTO signatoryUser = RegulatorUserDTO.builder()
                .firstName("signtoryFn").lastName("signatoryLn").jobTitle("signatoryJobTitle")
                .signature(FileInfoDTO.builder().name("signature.pdf").uuid(UUID.randomUUID().toString()).build())
                .build();
        when(regulatorUserAuthService.getRegulatorUserById(signatory)).thenReturn(signatoryUser);
        when(appProperties.getCompetentAuthorityCentralInfo()).thenReturn(caCentralInfo);
        
        FileDTO signatorySignature = FileDTO.builder()
                .fileContent("content".getBytes())
                .fileName("signature")
                .fileSize("content".length())
                .fileType("type")
                .build();
        when(userAuthService.getUserSignature(signatoryUser.getSignature().getUuid()))
                .thenReturn(Optional.of(signatorySignature));
        
        when(rfiSubmitDocumentTemplateWorkflowParamsProvider.getContextActionType()).thenReturn(DocumentTemplateGenerationContextActionType.RFI_SUBMIT);
        when(rfiSubmitDocumentTemplateWorkflowParamsProvider.constructParams(requestPayload))
            .thenReturn(Map.of("questions", List.of("quest1", "quest2")));
        
        //invoke
        TemplateParams result = provider.constructTemplateParams(sourceParams);
        
        assertThat(result).isEqualTo(TemplateParams.builder()
                .emailParams(EmailTemplateParams.builder()
                    .toRecipient(accountPrimaryContact.getEmail())
                    .ccRecipients(ccRecipientsEmails)
                    .build())
                .competentAuthorityParams(CompetentAuthorityTemplateParams.builder()
                        .competentAuthority(CompetentAuthority.ENGLAND)
                        .logo(Files.readAllBytes(Paths.get("src", "main", "resources", "images", "ca", CompetentAuthority.ENGLAND.getLogoPath())))
                        .build())
                .competentAuthorityCentralInfo(caCentralInfo)
                .signatoryParams(SignatoryTemplateParams.builder()
                        .fullName(signatoryUser.getFullName())
                        .jobTitle(signatoryUser.getJobTitle())
                        .signature(signatorySignature.getFileContent())
                        .build())
                .accountParams(AccountTemplateParams.builder()
                        .name(account.getName())
                        .siteName(account.getSiteName())
                        .emitterType(account.getEmitterType().name())
                        .location("accountLocation")
                        .legalEntityName(account.getLegalEntity().getName())
                        .legalEntityLocation("leLocation")
                        .primaryContact(accountPrimaryContact.getFullName())
                        .serviceContact("service fn service ln")
                        .build())
                .workflowParams(WorkflowTemplateParams.builder()
                        .requestId(request.getId())
                        .requestSubmissionDate(Date.from(request.getSubmissionDate().atZone(ZoneId.systemDefault()).toInstant()))
                        .requestTypeInfo(RequestType.PERMIT_ISSUANCE.getDescription().toLowerCase())
                        .params(Map.of("questions", List.of("quest1", "quest2")))
                        .build())
                .permitId(permitId)
                .build());
        
        verify(accountQueryService, times(1)).getAccountDTOById(request.getAccountId());
        verify(permitQueryService, times(1)).getPermitIdByAccountId(request.getAccountId());
        verify(requestAccountContactQueryService, times(1)).getRequestAccountContact(request, AccountContactType.SERVICE);
        verify(documentTemplateLocationInfoResolver, times(1)).constructLocationInfo(account.getLocation());
        verify(documentTemplateLocationInfoResolver, times(1)).constructAddressInfo(account.getLegalEntity().getAddress());
        verify(regulatorUserAuthService, times(1)).getRegulatorUserById(signatory);
        verify(userAuthService, times(1)).getUserSignature(signatoryUser.getSignature().getUuid());
        verify(rfiSubmitDocumentTemplateWorkflowParamsProvider, times(1)).getContextActionType();
        verify(rfiSubmitDocumentTemplateWorkflowParamsProvider, times(1)).constructParams(requestPayload);
        verify(appProperties, times(1)).getCompetentAuthorityCentralInfo();
    }
    
    @Test
    void constructTemplateParams_signature_not_exist() {
    	LocalDateTime submissionDate = LocalDateTime.now();
        Long accountId = 1L;
        PermitIssuanceRequestPayload requestPayload = PermitIssuanceRequestPayload.builder()
                .build();
        String signatory = "signatoryUserId";
        Request request = Request.builder().id("1").accountId(accountId)
                .type(RequestType.PERMIT_ISSUANCE).submissionDate(submissionDate)
                .payload(requestPayload)
                .build();
    	
    	UserInfoDTO accountPrimaryContact = UserInfoDTO.builder()
                .firstName("fn").lastName("ln").email("email@email")
                .build();
    	
    	DocumentTemplateParamsSourceData sourceParams = DocumentTemplateParamsSourceData.builder()
                .contextActionType(DocumentTemplateGenerationContextActionType.RFI_SUBMIT)
                .accountPrimaryContact(accountPrimaryContact)
                .request(request)
                .signatory(signatory)
                .toRecipientEmail(accountPrimaryContact.getEmail())
                .build();	
    	
    	AccountDTO account = AccountDTO.builder()
                .id(accountId)
                .name("accountname")
                .competentAuthority(CompetentAuthority.ENGLAND)
                .legalEntity(LegalEntityDTO.builder()
                        .name("lename")
                        .address(AddressDTO.builder()
                                .line1("le_line1")
                                .line2("le_line2")
                                .city("le_city")
                                .country("GR")
                                .postcode("15125")
                                .build())
                        .build())
                .build();
    	
    	String permitId = "permit";
        
        when(accountQueryService.getAccountDTOById(request.getAccountId())).thenReturn(account);
        when(permitQueryService.getPermitIdByAccountId(request.getAccountId())).thenReturn(Optional.of(permitId));
        when(requestAccountContactQueryService.getRequestAccountContact(request, AccountContactType.SERVICE))
            .thenReturn(Optional.of(UserInfoDTO.builder()
                    .firstName("service fn")
                    .lastName("service ln")
                    .userId("serviceContact")
                    .build()));
        when(documentTemplateLocationInfoResolver.constructLocationInfo(account.getLocation()))
            .thenReturn("accountLocation");
        when(documentTemplateLocationInfoResolver.constructAddressInfo(account.getLegalEntity().getAddress()))
            .thenReturn("leLocation");
        
        RegulatorUserDTO signatoryUser = RegulatorUserDTO.builder()
                .firstName("signtoryFn").lastName("signatoryLn").jobTitle("signatoryJobTitle")
                .build();
        when(regulatorUserAuthService.getRegulatorUserById(signatory)).thenReturn(signatoryUser);
        
        //invoke
        BusinessException be = assertThrows(BusinessException.class, () -> provider.constructTemplateParams(sourceParams));
        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.USER_SIGNATURE_NOT_EXIST);
        verifyNoInteractions(userAuthService);
    }
}
