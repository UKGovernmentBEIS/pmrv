package uk.gov.pmrv.api.web.controller.user;

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
import org.springframework.validation.Validator;
import uk.gov.pmrv.api.authorization.rules.services.PmrvUserAuthorizationService;
import uk.gov.pmrv.api.authorization.rules.services.RoleAuthorizationService;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.user.verifier.domain.VerifierUserDTO;
import uk.gov.pmrv.api.user.verifier.service.VerifierUserManagementService;
import uk.gov.pmrv.api.web.config.PmrvUserArgumentResolver;
import uk.gov.pmrv.api.web.controller.exception.ExceptionControllerAdvice;
import uk.gov.pmrv.api.web.security.AuthorizationAspectUserResolver;
import uk.gov.pmrv.api.web.security.AuthorizedAspect;
import uk.gov.pmrv.api.web.security.AuthorizedRoleAspect;
import uk.gov.pmrv.api.web.security.PmrvSecurityComponent;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class VerifierUserManagementControllerTest {

	public static final String BASE_PATH = "/v1.0/verifier-users";

    private MockMvc mockMvc;

    @InjectMocks
    private VerifierUserManagementController controller;

    @Mock
    private PmrvSecurityComponent pmrvSecurityComponent;

    @Mock
    private VerifierUserManagementService verifierUserManagementService;
    
    @Mock
    private PmrvUserAuthorizationService pmrvUserAuthorizationService;

    @Mock
    private RoleAuthorizationService roleAuthorizationService;

    @Mock
    private Validator validator;

    private ObjectMapper objectMapper;
    
    @BeforeEach
    public void setUp() {
        AuthorizationAspectUserResolver authorizationAspectUserResolver = new AuthorizationAspectUserResolver(pmrvSecurityComponent);
        AuthorizedAspect aspect = new AuthorizedAspect(pmrvUserAuthorizationService, authorizationAspectUserResolver);
        AuthorizedRoleAspect authorizedRoleAspect = new AuthorizedRoleAspect(roleAuthorizationService, authorizationAspectUserResolver);

        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(controller);
        aspectJProxyFactory.addAspect(aspect);
        aspectJProxyFactory.addAspect(authorizedRoleAspect);

        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);

        controller = (VerifierUserManagementController) aopProxy.getProxy();
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
				.setCustomArgumentResolvers(new PmrvUserArgumentResolver(pmrvSecurityComponent))
				.setValidator(validator)
            	.setControllerAdvice(new ExceptionControllerAdvice())
            	.build();
    }
    
    @Test
    void getVerifierUserById() throws Exception {
        final String userId = "userId";
        PmrvUser user = PmrvUser.builder().userId("currentuser").build();
        VerifierUserDTO verifierUserDTO = VerifierUserDTO.builder().firstName("fName")
                .lastName("lName").email("email").build();

        // Mock
        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        when(verifierUserManagementService.getVerifierUserById(user, userId)).thenReturn(verifierUserDTO);

        // Invoke
        mockMvc.perform(
                MockMvcRequestBuilders.get(BASE_PATH + "/" + userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value(verifierUserDTO.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(verifierUserDTO.getLastName()))
                .andExpect(jsonPath("$.email").value(verifierUserDTO.getEmail()));

        // Verify
        verify(pmrvSecurityComponent, times(1)).getAuthenticatedUser();
        verify(verifierUserManagementService, times(1)).getVerifierUserById(user, userId);
    }

    @Test
    void getVerifierUserById_forbidden() throws Exception {
        final String userId = "userId";
        PmrvUser user = PmrvUser.builder().userId("currentuser").build();

        // Mock
        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
                .when(pmrvUserAuthorizationService)
                .authorize(user, "getVerifierUserById");

        // Invoke
        mockMvc.perform(
                MockMvcRequestBuilders.get(BASE_PATH + "/" + userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        // Verify
        verify(pmrvSecurityComponent, times(1)).getAuthenticatedUser();
        verify(verifierUserManagementService, never()).getVerifierUserById(any(), anyString());
    }

    @Test
    void updateVerifierUserById() throws Exception {
        final String userId = "userId";
        PmrvUser user = PmrvUser.builder().userId("currentuser").build();
        VerifierUserDTO verifierUserDTO = VerifierUserDTO.builder().firstName("fName")
                .lastName("lName").email("email").build();

        // Mock
        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);

        // Invoke
        mockMvc.perform(
                MockMvcRequestBuilders.patch(BASE_PATH + "/" + userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(verifierUserDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value(verifierUserDTO.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(verifierUserDTO.getLastName()))
                .andExpect(jsonPath("$.email").value(verifierUserDTO.getEmail()));

        // Verify
        verify(pmrvSecurityComponent, times(1)).getAuthenticatedUser();
        verify(verifierUserManagementService, times(1)).updateVerifierUserById(user, userId, verifierUserDTO);
    }

    @Test
    void updateVerifierUserById_forbidden() throws Exception {
        final String userId = "userId";
        PmrvUser user = PmrvUser.builder().userId("currentuser").build();
        VerifierUserDTO verifierUserDTO = VerifierUserDTO.builder().firstName("fName")
                .lastName("lName").email("email").build();

        // Mock
        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
                .when(pmrvUserAuthorizationService)
                .authorize(user, "updateVerifierUserById");

        // Invoke
        mockMvc.perform(
                MockMvcRequestBuilders.patch(BASE_PATH + "/" + userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(verifierUserDTO)))
                .andExpect(status().isForbidden());

        // Verify
        verify(pmrvSecurityComponent, times(1)).getAuthenticatedUser();
        verify(verifierUserManagementService, never()).updateVerifierUserById(any(), anyString(), any());
    }

    @Test
    void updateCurrentVerifierUser() throws Exception {
        PmrvUser user = PmrvUser.builder().userId("currentuser").build();
        VerifierUserDTO verifierUserDTO = VerifierUserDTO.builder().firstName("fName")
                .lastName("lName").email("email").build();

        // Mock
        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);

        // Invoke
        mockMvc.perform(
                MockMvcRequestBuilders.patch(BASE_PATH + "/verifier")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(verifierUserDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value(verifierUserDTO.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(verifierUserDTO.getLastName()))
                .andExpect(jsonPath("$.email").value(verifierUserDTO.getEmail()));

        // Verify
        verify(pmrvSecurityComponent, times(1)).getAuthenticatedUser();
        verify(verifierUserManagementService, times(1)).updateCurrentVerifierUser(user, verifierUserDTO);
    }

    @Test
    void updateCurrentVerifierUser_forbidden() throws Exception {
        PmrvUser user = PmrvUser.builder().userId("currentuser").build();
        VerifierUserDTO verifierUserDTO = VerifierUserDTO.builder().firstName("fName")
                .lastName("lName").email("email").build();

        // Mock
        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
                .when(roleAuthorizationService)
                .evaluate(user, new RoleType[] {RoleType.VERIFIER});

        // Invoke
        mockMvc.perform(
                MockMvcRequestBuilders.patch(BASE_PATH + "/verifier")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(verifierUserDTO)))
                .andExpect(status().isForbidden());

        // Verify
        verify(pmrvSecurityComponent, times(1)).getAuthenticatedUser();
        verify(verifierUserManagementService, never()).updateCurrentVerifierUser(any(), any());
    }
}
