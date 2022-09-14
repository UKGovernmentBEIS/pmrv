package uk.gov.pmrv.api.web.controller.account;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.gov.pmrv.api.account.domain.dto.CaExternalContactDTO;
import uk.gov.pmrv.api.account.domain.dto.CaExternalContactRegistrationDTO;
import uk.gov.pmrv.api.account.domain.dto.CaExternalContactsDTO;
import uk.gov.pmrv.api.account.service.CaExternalContactService;
import uk.gov.pmrv.api.authorization.rules.services.PmrvUserAuthorizationService;
import uk.gov.pmrv.api.authorization.rules.services.RoleAuthorizationService;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.web.config.PmrvUserArgumentResolver;
import uk.gov.pmrv.api.web.controller.exception.ExceptionControllerAdvice;
import uk.gov.pmrv.api.web.security.AuthorizationAspectUserResolver;
import uk.gov.pmrv.api.web.security.AuthorizedAspect;
import uk.gov.pmrv.api.web.security.AuthorizedRoleAspect;
import uk.gov.pmrv.api.web.security.PmrvSecurityComponent;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CaExternalContactControllerTest {
    private static final String CA_EXTERNAL_CONTACT_CONTROLLER_PATH = "/v1.0/ca-external-contacts";

    private MockMvc mockMvc;

    private ObjectMapper mapper;

    @InjectMocks
    private CaExternalContactController controller;

    @Mock
    private PmrvSecurityComponent pmrvSecurityComponent;

    @Mock
    private CaExternalContactService caExternalContactService;

    @Mock
    private RoleAuthorizationService roleAuthorizationService;

    @Mock
    private PmrvUserAuthorizationService pmrvUserAuthorizationService;

    @BeforeEach
    public void setUp() {

        AuthorizationAspectUserResolver authorizationAspectUserResolver = new AuthorizationAspectUserResolver(pmrvSecurityComponent);
        AuthorizedAspect authorizedAspect = new AuthorizedAspect(pmrvUserAuthorizationService, authorizationAspectUserResolver);
        AuthorizedRoleAspect authorizedRoleAspect = new AuthorizedRoleAspect(roleAuthorizationService, authorizationAspectUserResolver);
        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(controller);
        aspectJProxyFactory.addAspect(authorizedAspect);
        aspectJProxyFactory.addAspect(authorizedRoleAspect);
        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);
        controller = (CaExternalContactController) aopProxy.getProxy();
        mapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
            .setCustomArgumentResolvers(new PmrvUserArgumentResolver(pmrvSecurityComponent))
            .setControllerAdvice(new ExceptionControllerAdvice())
            .build();
    }

    @Test
    void getCaExternalContacts() throws Exception {
        final PmrvUser user = PmrvUser.builder()
            .roleType(RoleType.REGULATOR)
            .build();

        CaExternalContactsDTO caExternalContactsDTO =
            CaExternalContactsDTO.builder()
                .caExternalContacts(List.of(
                    CaExternalContactDTO.builder().id(1L).name("c1").build(),
                    CaExternalContactDTO.builder().id(2L).name("c2").build()))
                .isEditable(false)
                .build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        when(caExternalContactService.getCaExternalContacts(user)).thenReturn(caExternalContactsDTO);

        mockMvc.perform(MockMvcRequestBuilders.get(CA_EXTERNAL_CONTACT_CONTROLLER_PATH)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("caExternalContacts[0].id").value(1L))
            .andExpect(jsonPath("caExternalContacts[0].name").value("c1"))
            .andExpect(jsonPath("caExternalContacts[1].id").value(2L))
            .andExpect(jsonPath("caExternalContacts[1].name").value("c2"));
    }

    @Test
    void getCaExternalContacts_forbidden() throws Exception {
        final PmrvUser user = PmrvUser.builder()
            .roleType(RoleType.OPERATOR)
            .build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(roleAuthorizationService)
            .evaluate(user, new RoleType[] {RoleType.REGULATOR});

        mockMvc.perform(MockMvcRequestBuilders.get(CA_EXTERNAL_CONTACT_CONTROLLER_PATH)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());

        verify(caExternalContactService, never()).getCaExternalContacts(any());
    }

    @Test
    void getCaExternalContactById() throws Exception {
        final PmrvUser user = PmrvUser.builder()
            .roleType(RoleType.REGULATOR)
            .build();
        long id = 1L;

        CaExternalContactDTO caExternalContactDTO =
            CaExternalContactDTO.builder()
                .id(1L)
                .name("c1")
                .build();

        when(caExternalContactService.getCaExternalContactById(user, id)).thenReturn(caExternalContactDTO);
        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders.get(CA_EXTERNAL_CONTACT_CONTROLLER_PATH + "/" + id)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("id").value(1L))
            .andExpect(jsonPath("name").value("c1"));

        verify(caExternalContactService, times(1)).getCaExternalContactById(user, id);
    }

    @Test
    void getCaExternalContactById_forbidden() throws Exception {
        final PmrvUser user = PmrvUser.builder()
            .roleType(RoleType.REGULATOR)
            .build();
        long id = 1L;

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(pmrvUserAuthorizationService)
            .authorize(user, "getCaExternalContactById");

        mockMvc.perform(MockMvcRequestBuilders.get(CA_EXTERNAL_CONTACT_CONTROLLER_PATH + "/" + id)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());

        verify(caExternalContactService, never()).getCaExternalContactById(user, id);
    }

    @Test
    void deleteCaExternalContactById() throws Exception {
        final PmrvUser user = PmrvUser.builder()
            .roleType(RoleType.REGULATOR)
            .build();
        long id = 1L;

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders.delete(CA_EXTERNAL_CONTACT_CONTROLLER_PATH + "/" + id))
            .andExpect(status().isNoContent());

        verify(caExternalContactService, times(1)).deleteCaExternalContactById(user, id);
    }

    @Test
    void deleteCaExternalContactById_forbidden() throws Exception {
        final PmrvUser user = PmrvUser.builder()
            .roleType(RoleType.REGULATOR)
            .build();
        Long id = 1L;

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(pmrvUserAuthorizationService)
            .authorize(user, "deleteCaExternalContactById");

        mockMvc.perform(MockMvcRequestBuilders.delete(CA_EXTERNAL_CONTACT_CONTROLLER_PATH + "/" + id))
            .andExpect(status().isForbidden());

        verify(caExternalContactService, never()).deleteCaExternalContactById(user, id);
    }

    @Test
    void createCaExternalContact() throws Exception {
        final PmrvUser user = PmrvUser.builder()
            .roleType(RoleType.REGULATOR)
            .build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);

        CaExternalContactRegistrationDTO caExternalContactRegistrationDTO =
            CaExternalContactRegistrationDTO.builder()
                .name("name")
                .email("email@email.com")
                .description("desc")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post(CA_EXTERNAL_CONTACT_CONTROLLER_PATH)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(caExternalContactRegistrationDTO)))
            .andExpect(status().isNoContent());

        verify(caExternalContactService, times(1)).createCaExternalContact(user, caExternalContactRegistrationDTO);
    }

    @Test
    void createCaExternalContact_bad_request() throws Exception {
        final PmrvUser user = PmrvUser.builder()
            .roleType(RoleType.REGULATOR)
            .build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);

        CaExternalContactRegistrationDTO caExternalContactRegistrationDTO =
            CaExternalContactRegistrationDTO.builder()
                .email("email@email.com")
                .description("desc")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post(CA_EXTERNAL_CONTACT_CONTROLLER_PATH)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(caExternalContactRegistrationDTO)))
            .andExpect(status().isBadRequest());

        verify(caExternalContactService, never()).createCaExternalContact(any(), any());
    }

    @Test
    void createCaExternalContact_forbidden() throws Exception {
        final PmrvUser user = PmrvUser.builder()
            .roleType(RoleType.REGULATOR)
            .build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(pmrvUserAuthorizationService)
            .authorize(user, "createCaExternalContact");

        CaExternalContactRegistrationDTO caExternalContactRegistrationDTO =
            CaExternalContactRegistrationDTO.builder()
                .name("name")
                .email("email@email.com")
                .description("desc")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post(CA_EXTERNAL_CONTACT_CONTROLLER_PATH)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(caExternalContactRegistrationDTO)))
            .andExpect(status().isForbidden());

        verify(caExternalContactService, never()).createCaExternalContact(any(), any());
    }

    @Test
    void editCaExternalContact() throws Exception {
        final PmrvUser user = PmrvUser.builder()
            .roleType(RoleType.REGULATOR)
            .build();
        long id = 1L;

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);

        CaExternalContactRegistrationDTO caExternalContactRegistrationDTO =
            CaExternalContactRegistrationDTO.builder()
                .name("name")
                .email("email@email.com")
                .description("desc")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.patch(CA_EXTERNAL_CONTACT_CONTROLLER_PATH + "/" + id)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(caExternalContactRegistrationDTO)))
            .andExpect(status().isNoContent());

        verify(caExternalContactService, times(1)).editCaExternalContact(user, id, caExternalContactRegistrationDTO);
    }

    @Test
    void editCaExternalContact_bad_request() throws Exception {
        final PmrvUser user = PmrvUser.builder()
            .roleType(RoleType.REGULATOR)
            .build();
        long id = 1L;

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);

        CaExternalContactRegistrationDTO caExternalContactRegistrationDTO =
            CaExternalContactRegistrationDTO.builder()
                .email("email@email.com")
                .description("desc")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.patch(CA_EXTERNAL_CONTACT_CONTROLLER_PATH + "/" + id)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(caExternalContactRegistrationDTO)))
            .andExpect(status().isBadRequest());

        verify(caExternalContactService, never()).editCaExternalContact(any(), anyLong(), any());
    }

    @Test
    void editCaExternalContact_forbidden() throws Exception {
        final PmrvUser user = PmrvUser.builder()
            .roleType(RoleType.REGULATOR)
            .build();
        long id = 1L;

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(pmrvUserAuthorizationService)
            .authorize(user, "editCaExternalContact");

        CaExternalContactRegistrationDTO caExternalContactRegistrationDTO =
            CaExternalContactRegistrationDTO.builder()
                .name("name")
                .email("email@email.com")
                .description("desc")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.patch(CA_EXTERNAL_CONTACT_CONTROLLER_PATH + "/" + id)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(caExternalContactRegistrationDTO)))
            .andExpect(status().isForbidden());

        verify(caExternalContactService, never()).editCaExternalContact(any(), anyLong(), any());
    }
}
