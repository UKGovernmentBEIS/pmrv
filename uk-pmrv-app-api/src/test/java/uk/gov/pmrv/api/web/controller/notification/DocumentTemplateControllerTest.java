package uk.gov.pmrv.api.web.controller.notification;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.pmrv.api.common.domain.enumeration.RoleType.OPERATOR;
import static uk.gov.pmrv.api.common.domain.enumeration.RoleType.REGULATOR;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.aop.framework.AopProxy;
import org.springframework.aop.framework.DefaultAopProxyFactory;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import uk.gov.pmrv.api.authorization.rules.services.PmrvUserAuthorizationService;
import uk.gov.pmrv.api.authorization.rules.services.RoleAuthorizationService;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.common.domain.model.PmrvAuthority;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.files.common.domain.dto.FileDTO;
import uk.gov.pmrv.api.notification.template.domain.dto.DocumentTemplateDTO;
import uk.gov.pmrv.api.notification.template.domain.dto.DocumentTemplateSearchCriteria;
import uk.gov.pmrv.api.notification.template.domain.dto.TemplateInfoDTO;
import uk.gov.pmrv.api.notification.template.domain.dto.TemplateSearchResults;
import uk.gov.pmrv.api.notification.template.service.DocumentTemplateQueryService;
import uk.gov.pmrv.api.notification.template.service.DocumentTemplateUpdateService;
import uk.gov.pmrv.api.web.config.PmrvUserArgumentResolver;
import uk.gov.pmrv.api.web.controller.exception.ExceptionControllerAdvice;
import uk.gov.pmrv.api.web.security.AuthorizationAspectUserResolver;
import uk.gov.pmrv.api.web.security.AuthorizedAspect;
import uk.gov.pmrv.api.web.security.AuthorizedRoleAspect;
import uk.gov.pmrv.api.web.security.PmrvSecurityComponent;

@ExtendWith(MockitoExtension.class)
class DocumentTemplateControllerTest {

    private static final String DOCUMENT_TEMPLATE_CONTROLLER_PATH = "/v1.0/document-templates";

    private MockMvc mockMvc;

    @InjectMocks
    private DocumentTemplateController documentTemplateController;

    @Mock
    private DocumentTemplateQueryService documentTemplateQueryService;

    @Mock
    private DocumentTemplateUpdateService documentTemplateUpdateService;

    @Mock
    private PmrvSecurityComponent pmrvSecurityComponent;

    @Mock
    private RoleAuthorizationService roleAuthorizationService;

    @Mock
    private PmrvUserAuthorizationService pmrvUserAuthorizationService;

    @BeforeEach
    public void setUp() {
        AuthorizationAspectUserResolver authorizationAspectUserResolver = new AuthorizationAspectUserResolver(pmrvSecurityComponent);
        AuthorizedRoleAspect
            authorizedRoleAspect = new AuthorizedRoleAspect(roleAuthorizationService, authorizationAspectUserResolver);
        AuthorizedAspect authorizedAspect = new AuthorizedAspect(pmrvUserAuthorizationService, authorizationAspectUserResolver);

        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(documentTemplateController);
        aspectJProxyFactory.addAspect(authorizedRoleAspect);
        aspectJProxyFactory.addAspect(authorizedAspect);

        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);

        documentTemplateController = (DocumentTemplateController) aopProxy.getProxy();

        mockMvc = MockMvcBuilders.standaloneSetup(documentTemplateController)
            .addFilters(new FilterChainProxy(Collections.emptyList()))
            .setControllerAdvice(new ExceptionControllerAdvice())
            .setCustomArgumentResolvers(new PmrvUserArgumentResolver(pmrvSecurityComponent))
            .build();
    }

    @Test
    void getCurrentUserDocumentTemplates() throws Exception {
        CompetentAuthority ca = CompetentAuthority.ENGLAND;
        PmrvAuthority pmrvAuthority = PmrvAuthority.builder().competentAuthority(ca).build();
        PmrvUser pmrvUser = PmrvUser.builder()
            .userId("userId")
            .roleType(RoleType.REGULATOR)
            .authorities(List.of(pmrvAuthority))
            .build();
        DocumentTemplateSearchCriteria searchCriteria = DocumentTemplateSearchCriteria.builder()
            .term("term")
            .page(0L)
            .pageSize(30L)
            .build();

        List<TemplateInfoDTO> documentTemplates = List.of(
            new TemplateInfoDTO(1L, "template1", "INSTALLATION", "Workflow Name", LocalDateTime.now()),
            new TemplateInfoDTO(2L, "template2", "INSTALLATION", "Workflow Name", LocalDateTime.now())
        );
        TemplateSearchResults results = TemplateSearchResults.builder()
            .templates(documentTemplates)
            .total(2L)
            .build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(pmrvUser);
        when(documentTemplateQueryService.getDocumentTemplatesByCaAndSearchCriteria(ca, searchCriteria)).thenReturn(results);

        mockMvc.perform(MockMvcRequestBuilders
            .get(DOCUMENT_TEMPLATE_CONTROLLER_PATH)
            .param("term", searchCriteria.getTerm())
            .param("page", String.valueOf(searchCriteria.getPage()))
            .param("size", String.valueOf(searchCriteria.getPageSize()))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.templates[0].id").value(1L))
            .andExpect(jsonPath("$.templates[0].name").value("template1"))
            .andExpect(jsonPath("$.templates[1].id").value(2L))
            .andExpect(jsonPath("$.templates[1].name").value("template2"));

        verify(documentTemplateQueryService, times(1))
            .getDocumentTemplatesByCaAndSearchCriteria(ca, searchCriteria);
    }

    @Test
    void getCurrentUserDocumentTemplates_forbidden() throws Exception {
        PmrvUser pmrvUser = PmrvUser.builder()
            .userId("userId")
            .roleType(OPERATOR)
            .build();
        DocumentTemplateSearchCriteria searchCriteria = DocumentTemplateSearchCriteria.builder()
            .term("term")
            .page(0L)
            .pageSize(30L)
            .build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(pmrvUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(roleAuthorizationService)
            .evaluate(pmrvUser, new RoleType[]{REGULATOR});

        mockMvc.perform(MockMvcRequestBuilders
            .get(DOCUMENT_TEMPLATE_CONTROLLER_PATH)
            .param("term", searchCriteria.getTerm())
            .param("page", String.valueOf(searchCriteria.getPage()))
            .param("size", String.valueOf(searchCriteria.getPageSize()))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());

        verifyNoInteractions(documentTemplateQueryService);
    }

    @Test
    void getDocumentTemplateById() throws Exception {
        Long documentTemplateId = 1L;
        String documentTemplateName = "document_template_name";

        DocumentTemplateDTO documentTemplateDTO = DocumentTemplateDTO.builder()
            .id(documentTemplateId)
            .name(documentTemplateName)
            .build();

        when(documentTemplateQueryService.getDocumentTemplateDTOById(documentTemplateId)).thenReturn(documentTemplateDTO);

        mockMvc.perform(MockMvcRequestBuilders
            .get(DOCUMENT_TEMPLATE_CONTROLLER_PATH + "/" + documentTemplateId)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(documentTemplateId))
            .andExpect(jsonPath("$.name").value(documentTemplateName));

        verify(documentTemplateQueryService, times(1)).getDocumentTemplateDTOById(documentTemplateId);
    }

    @Test
    void getDocumentTemplateById_forbidden() throws Exception {
        long documentTemplateId = 1L;
        PmrvUser pmrvUser = PmrvUser.builder()
            .userId("userId")
            .roleType(OPERATOR)
            .build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(pmrvUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(pmrvUserAuthorizationService)
            .authorize(pmrvUser, "getDocumentTemplateById", Long.toString(documentTemplateId));

        mockMvc.perform(MockMvcRequestBuilders
            .get(DOCUMENT_TEMPLATE_CONTROLLER_PATH + "/" + documentTemplateId)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());

        verifyNoInteractions(documentTemplateQueryService);
    }

    @Test
    void updateDocumentTemplate() throws Exception {
        Long documentTemplateId = 1L;
        String userId = "userId";
        PmrvUser authUser = PmrvUser.builder().userId(userId).roleType(RoleType.REGULATOR).build();
        String originalFilename = "filename.txt";
        String contentType = "text/plain";
        byte[] fileContent = "content".getBytes();
        MockMultipartFile file = new MockMultipartFile("file", originalFilename, contentType, fileContent);
        FileDTO fileDTO = FileDTO.builder()
            .fileName(originalFilename)
            .fileType(contentType)
            .fileContent(fileContent)
            .fileSize(file.getSize())
            .build();
        
        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(authUser);
        
        mockMvc.perform(MockMvcRequestBuilders.multipart(DOCUMENT_TEMPLATE_CONTROLLER_PATH + "/" + documentTemplateId)
                .file(file)).andExpect(status().isNoContent());

        verify(pmrvSecurityComponent, times(1)).getAuthenticatedUser();
        verify(documentTemplateUpdateService, times(1)).updateDocumentTemplateFile(documentTemplateId, fileDTO, userId);
    }
    
    @Test
    void updateDocumentTemplate_forbidden() throws Exception {
        Long documentTemplateId = 1L;
        String userId = "userId";
        PmrvUser authUser = PmrvUser.builder().userId(userId).roleType(RoleType.REGULATOR).build();
        String originalFilename = "filename.txt";
        String contentType = "text/plain";
        byte[] fileContent = "content".getBytes();
        MockMultipartFile file = new MockMultipartFile("file", originalFilename, contentType, fileContent);
        
        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(authUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN)).when(pmrvUserAuthorizationService).authorize(authUser,
                "updateDocumentTemplate", Long.toString(documentTemplateId));
        
        mockMvc.perform(MockMvcRequestBuilders.multipart(DOCUMENT_TEMPLATE_CONTROLLER_PATH + "/" + documentTemplateId)
                .file(file)).andExpect(status().isForbidden());

        verify(pmrvSecurityComponent, times(1)).getAuthenticatedUser();
        verifyNoInteractions(documentTemplateUpdateService);
    }
    
    @Test
    @DisplayName("Should throw BAD REQUEST (400) when no attachment is provided")
    void updateDocumentTemplate_noDocumentTemplateFileProvided() throws Exception {
        PmrvUser authUser = PmrvUser.builder().userId("userId").roleType(RoleType.REGULATOR).build();
        long documentTemplateId = 1L;

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(authUser);

        mockMvc.perform(MockMvcRequestBuilders.multipart(DOCUMENT_TEMPLATE_CONTROLLER_PATH + "/" + documentTemplateId))
            .andExpect(
                result -> Assertions.assertTrue(
                    result.getResolvedException() instanceof MissingServletRequestPartException))
            .andExpect(status().isBadRequest());
    }

}