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
import uk.gov.pmrv.api.user.operator.domain.OperatorUserDTO;
import uk.gov.pmrv.api.user.operator.service.OperatorUserManagementService;
import uk.gov.pmrv.api.web.config.PmrvUserArgumentResolver;
import uk.gov.pmrv.api.web.controller.exception.ExceptionControllerAdvice;
import uk.gov.pmrv.api.web.security.AuthorizationAspectUserResolver;
import uk.gov.pmrv.api.web.security.AuthorizedAspect;
import uk.gov.pmrv.api.web.security.AuthorizedRoleAspect;
import uk.gov.pmrv.api.web.security.PmrvSecurityComponent;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class OperatorUserManagementControllerTest {

    public static final String BASE_PATH = "/v1.0/operator-users";

    private MockMvc mockMvc;

    @InjectMocks
    private OperatorUserManagementController controller;

    @Mock
    private PmrvSecurityComponent pmrvSecurityComponent;

    @Mock
    private OperatorUserManagementService operatorUserManagementService;

    @Mock
    private PmrvUserAuthorizationService pmrvUserAuthorizationService;

    @Mock
    private RoleAuthorizationService roleAuthorizationService;
    
    @Mock
    private Validator validator;
    
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        AuthorizationAspectUserResolver authorizationAspectUserResolver = new AuthorizationAspectUserResolver(pmrvSecurityComponent);
        AuthorizedAspect aspect = new AuthorizedAspect(pmrvUserAuthorizationService, authorizationAspectUserResolver);
        AuthorizedRoleAspect authorizedRoleAspect = new AuthorizedRoleAspect(roleAuthorizationService, authorizationAspectUserResolver);

        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(controller);
        aspectJProxyFactory.addAspect(aspect);
        aspectJProxyFactory.addAspect(authorizedRoleAspect);

        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);

        controller = (OperatorUserManagementController) aopProxy.getProxy();
    	objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
				.setCustomArgumentResolvers(new PmrvUserArgumentResolver(pmrvSecurityComponent))
				.setValidator(validator)
            	.setControllerAdvice(new ExceptionControllerAdvice())
            	.build();
    }

	@Test
	void getOperatorUserById() throws Exception {
		PmrvUser user = PmrvUser.builder().userId("authId").build();
		String userId = "userId";
		OperatorUserDTO operatorUserDTO = OperatorUserDTO.builder()
				.firstName("fn")
				.lastName("ln")
				.email("email")
				.build();

		when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);
		when(operatorUserManagementService.getOperatorUserByAccountAndId(1L, userId))
				.thenReturn(operatorUserDTO);

		//invoke
		mockMvc.perform(
				MockMvcRequestBuilders.get(BASE_PATH + "/account/" + 1L + "/" + userId)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.firstName").value(operatorUserDTO.getFirstName()))
				.andExpect(jsonPath("$.lastName").value(operatorUserDTO.getLastName()))
				.andExpect(jsonPath("$.email").value(operatorUserDTO.getEmail()));

		verify(operatorUserManagementService, times(1)).getOperatorUserByAccountAndId(1L, userId);
	}

	@Test
	void getOperatorUserById_forbidden() throws Exception {
		PmrvUser user = PmrvUser.builder().userId("authId").build();
		String userId = "userId";
		Long accountId = 1L;

		when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(pmrvUserAuthorizationService)
            .authorize(user, "getOperatorUserById", accountId.toString());

		mockMvc.perform(
				MockMvcRequestBuilders.get(BASE_PATH + "/account/" + accountId + "/" + userId)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isForbidden());

		verify(operatorUserManagementService, never()).getOperatorUserByAccountAndId(anyLong(), anyString());
	}

    @Test
	void updateCurrentOperatorUser() throws Exception {
		PmrvUser user = PmrvUser.builder().userId("authId").roleType(RoleType.OPERATOR).build();
		OperatorUserDTO operatorUserDTO = OperatorUserDTO.builder()
				.firstName("fn")
				.lastName("ln")
				.email("email")
				.build();

		when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);

		//invoke
		mockMvc.perform(
				MockMvcRequestBuilders.patch(BASE_PATH + "/operator")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(operatorUserDTO)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.firstName").value(operatorUserDTO.getFirstName()))
				.andExpect(jsonPath("$.lastName").value(operatorUserDTO.getLastName()))
				.andExpect(jsonPath("$.email").value(operatorUserDTO.getEmail()));

		verify(operatorUserManagementService, times(1)).updateOperatorUser(user, operatorUserDTO);
	}

	@Test
	void updateCurrentOperatorUser_forbidden() throws Exception {
		PmrvUser user = PmrvUser.builder().userId("authId").roleType(RoleType.REGULATOR).build();
		OperatorUserDTO operatorUserDTO = OperatorUserDTO.builder()
				.firstName("fn")
				.lastName("ln")
				.email("email")
				.build();

		when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);
		doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(roleAuthorizationService)
            .evaluate(user, new RoleType[] {RoleType.OPERATOR});

		mockMvc.perform(
				MockMvcRequestBuilders.patch(BASE_PATH + "/operator")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(operatorUserDTO)))
				.andExpect(status().isForbidden());

		verify(operatorUserManagementService, never()).updateOperatorUser(any(), any());
	}

	@Test
	void updateOperatorUserById() throws Exception {
		PmrvUser user = PmrvUser.builder().userId("authId").build();
		String userId = "userId";
		OperatorUserDTO operatorUserDTO = OperatorUserDTO.builder()
				.firstName("fn")
				.lastName("ln")
				.email("email")
				.build();

		when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);

		//invoke
		mockMvc.perform(
				MockMvcRequestBuilders.patch(BASE_PATH + "/account/" + 1L + "/" + userId)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(operatorUserDTO)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.firstName").value(operatorUserDTO.getFirstName()))
				.andExpect(jsonPath("$.lastName").value(operatorUserDTO.getLastName()))
				.andExpect(jsonPath("$.email").value(operatorUserDTO.getEmail()));

		verify(operatorUserManagementService, times(1))
				.updateOperatorUserByAccountAndId(1L, userId, operatorUserDTO);
	}

	@Test
	void updateOperatorUserById_forbidden() throws Exception {
		PmrvUser user = PmrvUser.builder().userId("authId").build();
		String userId = "userId";
		OperatorUserDTO operatorUserDTO = OperatorUserDTO.builder()
				.firstName("fn")
				.lastName("ln")
				.email("email")
				.build();
		Long accountId = 1L;

		when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(pmrvUserAuthorizationService)
            .authorize(user, "updateOperatorUserById", accountId.toString());

		mockMvc.perform(
				MockMvcRequestBuilders.patch(BASE_PATH + "/account/" + accountId + "/" + userId)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(operatorUserDTO)))
				.andExpect(status().isForbidden());

		verify(operatorUserManagementService, never())
				.updateOperatorUserByAccountAndId( anyLong(), anyString(), any());
	}

}
