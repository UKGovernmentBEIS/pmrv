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

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.aop.framework.AopProxy;
import org.springframework.aop.framework.DefaultAopProxyFactory;
import org.springframework.http.MediaType;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.gov.pmrv.api.authorization.rules.services.PmrvUserAuthorizationService;
import uk.gov.pmrv.api.authorization.rules.services.RoleAuthorizationService;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.common.domain.model.PmrvAuthority;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.notification.template.domain.dto.NotificationTemplateDTO;
import uk.gov.pmrv.api.notification.template.domain.dto.NotificationTemplateSearchCriteria;
import uk.gov.pmrv.api.notification.template.domain.dto.NotificationTemplateUpdateDTO;
import uk.gov.pmrv.api.notification.template.domain.dto.TemplateInfoDTO;
import uk.gov.pmrv.api.notification.template.domain.dto.TemplateSearchResults;
import uk.gov.pmrv.api.notification.template.service.NotificationTemplateQueryService;
import uk.gov.pmrv.api.notification.template.service.NotificationTemplateUpdateService;
import uk.gov.pmrv.api.web.config.PmrvUserArgumentResolver;
import uk.gov.pmrv.api.web.controller.exception.ExceptionControllerAdvice;
import uk.gov.pmrv.api.web.security.AuthorizationAspectUserResolver;
import uk.gov.pmrv.api.web.security.AuthorizedAspect;
import uk.gov.pmrv.api.web.security.AuthorizedRoleAspect;
import uk.gov.pmrv.api.web.security.PmrvSecurityComponent;

@ExtendWith(MockitoExtension.class)
class NotificationTemplateControllerTest {

    private static final String NOTIFICATION_TEMPLATE_CONTROLLER_PATH = "/v1.0/notification-templates";

    private MockMvc mockMvc;

    @InjectMocks
    private NotificationTemplateController notificationTemplateController;

    @Mock
    private NotificationTemplateQueryService notificationTemplateQueryService;

    @Mock
    private NotificationTemplateUpdateService notificationTemplateUpdateService;

    @Mock
    private PmrvSecurityComponent pmrvSecurityComponent;

    @Mock
    private RoleAuthorizationService roleAuthorizationService;

    @Mock
    private PmrvUserAuthorizationService pmrvUserAuthorizationService;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        AuthorizationAspectUserResolver authorizationAspectUserResolver = new AuthorizationAspectUserResolver(pmrvSecurityComponent);
        AuthorizedRoleAspect
            authorizedRoleAspect = new AuthorizedRoleAspect(roleAuthorizationService, authorizationAspectUserResolver);
        AuthorizedAspect aspect = new AuthorizedAspect(pmrvUserAuthorizationService, authorizationAspectUserResolver);

        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(notificationTemplateController);
        aspectJProxyFactory.addAspect(authorizedRoleAspect);
        aspectJProxyFactory.addAspect(aspect);

        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);

        notificationTemplateController = (NotificationTemplateController) aopProxy.getProxy();
        mockMvc = MockMvcBuilders.standaloneSetup(notificationTemplateController)
            .addFilters(new FilterChainProxy(Collections.emptyList()))
            .setControllerAdvice(new ExceptionControllerAdvice())
            .setCustomArgumentResolvers(new PmrvUserArgumentResolver(pmrvSecurityComponent))
            .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void getCurrentUserNotificationTemplates() throws Exception {
        CompetentAuthority ca = CompetentAuthority.ENGLAND;
        PmrvAuthority pmrvAuthority = PmrvAuthority.builder().competentAuthority(ca).build();
        PmrvUser pmrvUser = PmrvUser.builder()
            .userId("userId")
            .roleType(RoleType.REGULATOR)
            .authorities(List.of(pmrvAuthority))
            .build();
        NotificationTemplateSearchCriteria searchCriteria = NotificationTemplateSearchCriteria.builder()
            .term("term")
            .roleType(RoleType.OPERATOR)
            .page(0L)
            .pageSize(30L)
            .build();

        List<TemplateInfoDTO> notificationTemplates = List.of(
                new TemplateInfoDTO(1L, "template1", "INSTALLATION", "Workflow Name", LocalDateTime.now()),
                new TemplateInfoDTO(2L, "template2", "INSTALLATION", "Workflow Name", LocalDateTime.now())
        );
        TemplateSearchResults results = TemplateSearchResults.builder()
            .templates(notificationTemplates)
            .total(2L)
            .build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(pmrvUser);
        when(notificationTemplateQueryService.getNotificationTemplatesByCaAndSearchCriteria(ca, searchCriteria)).thenReturn(results);

        mockMvc.perform(MockMvcRequestBuilders
            .get(NOTIFICATION_TEMPLATE_CONTROLLER_PATH)
            .param("term", searchCriteria.getTerm())
            .param("role", searchCriteria.getRoleType().name())
            .param("page", String.valueOf(searchCriteria.getPage()))
            .param("size", String.valueOf(searchCriteria.getPageSize()))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.templates[0].id").value(1L))
            .andExpect(jsonPath("$.templates[0].name").value("template1"))
            .andExpect(jsonPath("$.templates[1].id").value(2L))
            .andExpect(jsonPath("$.templates[1].name").value("template2"));

        verify(notificationTemplateQueryService, times(1))
            .getNotificationTemplatesByCaAndSearchCriteria(ca, searchCriteria);
    }

    @Test
    void getCurrentUserNotificationTemplates_forbidden() throws Exception {
        PmrvUser pmrvUser = PmrvUser.builder()
            .userId("userId")
            .roleType(OPERATOR)
            .build();
        NotificationTemplateSearchCriteria searchCriteria = NotificationTemplateSearchCriteria.builder()
            .term("term")
            .roleType(RoleType.OPERATOR)
            .page(0L)
            .pageSize(30L)
            .build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(pmrvUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(roleAuthorizationService)
            .evaluate(pmrvUser, new RoleType[]{REGULATOR});

        mockMvc.perform(MockMvcRequestBuilders
            .get(NOTIFICATION_TEMPLATE_CONTROLLER_PATH)
            .param("term", searchCriteria.getTerm())
            .param("role", searchCriteria.getRoleType().name())
            .param("page", String.valueOf(searchCriteria.getPage()))
            .param("size", String.valueOf(searchCriteria.getPageSize()))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());

        verifyNoInteractions(notificationTemplateQueryService);
    }

    @Test
    void getNotificationTemplateById() throws Exception {
        Long notificationTemplateId = 1L;
        String notificationTemplateName = "notif_template_name";

        NotificationTemplateDTO notificationTemplateDTO = NotificationTemplateDTO.builder()
            .id(notificationTemplateId)
            .name(notificationTemplateName)
            .build();

        when(notificationTemplateQueryService.getManagedNotificationTemplateById(notificationTemplateId)).thenReturn(notificationTemplateDTO);

        mockMvc.perform(MockMvcRequestBuilders
            .get(NOTIFICATION_TEMPLATE_CONTROLLER_PATH + "/" + notificationTemplateId)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(notificationTemplateId))
            .andExpect(jsonPath("$.name").value(notificationTemplateName));

        verify(notificationTemplateQueryService, times(1)).getManagedNotificationTemplateById(notificationTemplateId);
    }

    @Test
    void getNotificationTemplateById_forbidden() throws Exception {
        long notificationTemplateId = 1L;
        PmrvUser pmrvUser = PmrvUser.builder()
            .userId("userId")
            .roleType(OPERATOR)
            .build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(pmrvUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(pmrvUserAuthorizationService)
            .authorize(pmrvUser, "getNotificationTemplateById", Long.toString(notificationTemplateId));

        mockMvc.perform(MockMvcRequestBuilders
            .get(NOTIFICATION_TEMPLATE_CONTROLLER_PATH + "/" + notificationTemplateId)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());

        verifyNoInteractions(notificationTemplateQueryService);
    }

    @Test
    void updateNotificationTemplate() throws Exception {
        long notificationTemplateId = 1L;
        NotificationTemplateUpdateDTO notificationTemplateUpdateDTO = NotificationTemplateUpdateDTO.builder()
            .subject("subject")
            .text("text")
            .build();

        mockMvc.perform(
            MockMvcRequestBuilders
                .put(NOTIFICATION_TEMPLATE_CONTROLLER_PATH + "/" + notificationTemplateId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(notificationTemplateUpdateDTO)))
            .andExpect(status().isNoContent());

        verify(notificationTemplateUpdateService, times(1))
            .updateNotificationTemplate(notificationTemplateId, notificationTemplateUpdateDTO);
    }

    @Test
    void updateNotificationTemplate_forbidden() throws Exception {
        long notificationTemplateId = 1L;
        PmrvUser pmrvUser = PmrvUser.builder()
            .userId("userId")
            .roleType(OPERATOR)
            .build();
        NotificationTemplateUpdateDTO notificationTemplateUpdateDTO = NotificationTemplateUpdateDTO.builder()
            .subject("subject")
            .text("text")
            .build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(pmrvUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(pmrvUserAuthorizationService)
            .authorize(pmrvUser, "updateNotificationTemplate", "1");

        mockMvc.perform(
            MockMvcRequestBuilders
                .put(NOTIFICATION_TEMPLATE_CONTROLLER_PATH + "/" + notificationTemplateId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(notificationTemplateUpdateDTO)))
            .andExpect(status().isForbidden());

        verifyNoInteractions(notificationTemplateUpdateService);
    }
}