package uk.gov.pmrv.api.web.controller.authorization;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.aop.framework.AopProxy;
import org.springframework.aop.framework.DefaultAopProxyFactory;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;
import uk.gov.pmrv.api.account.domain.enumeration.AccountContactType;
import uk.gov.pmrv.api.authorization.core.domain.AuthorityStatus;
import uk.gov.pmrv.api.authorization.operator.domain.AccountOperatorAuthorityUpdateDTO;
import uk.gov.pmrv.api.web.orchestrator.dto.AccountOperatorAuthorityUpdateWrapperDTO;
import uk.gov.pmrv.api.authorization.operator.service.OperatorAuthorityDeletionService;
import uk.gov.pmrv.api.authorization.rules.services.PmrvUserAuthorizationService;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.web.config.PmrvUserArgumentResolver;
import uk.gov.pmrv.api.web.controller.exception.ExceptionControllerAdvice;
import uk.gov.pmrv.api.web.orchestrator.AccountOperatorUserAuthorityQueryOrchestrator;
import uk.gov.pmrv.api.web.orchestrator.AccountOperatorUserAuthorityUpdateOrchestrator;
import uk.gov.pmrv.api.web.orchestrator.dto.AccountOperatorsUsersAuthoritiesInfoDTO;
import uk.gov.pmrv.api.web.orchestrator.dto.UserAuthorityInfoDTO;
import uk.gov.pmrv.api.web.security.AuthorizationAspectUserResolver;
import uk.gov.pmrv.api.web.security.AuthorizedAspect;
import uk.gov.pmrv.api.web.security.PmrvSecurityComponent;

import java.util.List;
import java.util.Map;

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
class OperatorAuthorityControllerTest {

    private static final String BASE_PATH = "/v1.0/operator-authorities";
    public static final String ACCOUNT_OPERATOR_USERS_PATH = "/account";

    private MockMvc mockMvc;

    @InjectMocks
    private OperatorAuthorityController operatorAuthorityController;

    @Mock
    private PmrvUserAuthorizationService pmrvUserAuthorizationService;

    @Mock
    private OperatorAuthorityDeletionService operatorAuthorityDeletionService;

    @Mock
    private AccountOperatorUserAuthorityQueryOrchestrator accountOperatorUserAuthorityQueryOrchestrator;

    @Mock
    private AccountOperatorUserAuthorityUpdateOrchestrator accountOperatorUserAuthorityUpdateOrchestrator;

    @Mock
    private Validator validator;

    @Mock
    private PmrvSecurityComponent pmrvSecurityComponent;
    
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        AuthorizationAspectUserResolver authorizationAspectUserResolver = new AuthorizationAspectUserResolver(pmrvSecurityComponent);
        AuthorizedAspect aspect = new AuthorizedAspect(pmrvUserAuthorizationService, authorizationAspectUserResolver);

        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(operatorAuthorityController);
        aspectJProxyFactory.addAspect(aspect);

        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);

        operatorAuthorityController = (OperatorAuthorityController) aopProxy.getProxy();
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(operatorAuthorityController)
            .setCustomArgumentResolvers(new PmrvUserArgumentResolver(pmrvSecurityComponent))
            .setControllerAdvice(new ExceptionControllerAdvice())
            .setValidator(validator)
            .build();
    }
    
    @Test
    void getAccountOperatorAuthorities() throws Exception {
    	PmrvUser user = PmrvUser.builder().userId("currentuser").build();
        UserAuthorityInfoDTO accountOperatorAuthorityUserInfo = UserAuthorityInfoDTO.builder()
    			.userId("user")
    			.firstName("fn")
    			.lastName("ln")
    			.roleName("Operator admin")
    			.build();
    	Map<AccountContactType, String> contactTypes = Map.of(
                AccountContactType.PRIMARY, "primary",
                AccountContactType.SERVICE, "service"
                );
    	
    	when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        when(accountOperatorUserAuthorityQueryOrchestrator.getAccountOperatorsUsersAuthoritiesInfo(user, 1L))
        	.thenReturn(AccountOperatorsUsersAuthoritiesInfoDTO.builder()
        			.authorities(List.of(accountOperatorAuthorityUserInfo))
        			.editable(true)
        			.contactTypes(contactTypes)
        			.build());
        
        //invoke
        mockMvc.perform(
        		MockMvcRequestBuilders.get(BASE_PATH + ACCOUNT_OPERATOR_USERS_PATH + "/" + 1L)
        							.contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.editable").value(Boolean.TRUE))
            .andExpect(jsonPath("$.authorities[0].userId").value(accountOperatorAuthorityUserInfo.getUserId()))
            .andExpect(jsonPath("$.contactTypes", Matchers.hasEntry(AccountContactType.PRIMARY.name(), "primary")))
            .andExpect(jsonPath("$.contactTypes", Matchers.hasEntry(AccountContactType.SERVICE.name(), "service")));
    
	    verify(accountOperatorUserAuthorityQueryOrchestrator, times(1)).getAccountOperatorsUsersAuthoritiesInfo(user, 1L);
    }
    
    @Test
    void getAccountOperatorAuthorities_forbidden() throws Exception {
    	PmrvUser user = PmrvUser.builder().userId("currentuser").build();
    	long accountId = 1L;

    	when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(pmrvUserAuthorizationService)
            .authorize(user, "getAccountOperatorAuthorities", String.valueOf(accountId));
        
        mockMvc.perform(
	        		MockMvcRequestBuilders.get(BASE_PATH + ACCOUNT_OPERATOR_USERS_PATH + "/" + accountId)
	        							.contentType(MediaType.APPLICATION_JSON))
	            .andExpect(status().isForbidden());
          
	    verify(pmrvSecurityComponent, times(1)).getAuthenticatedUser();
	    verify(accountOperatorUserAuthorityQueryOrchestrator, never()).getAccountOperatorsUsersAuthoritiesInfo(any(), anyLong());
    }

    @Test
    void updateAccountOperatorAuthorities() throws Exception {
        PmrvUser currentUser = PmrvUser.builder().userId("currentuser").build();

        List<AccountOperatorAuthorityUpdateDTO> accountUsers = List.of(
            AccountOperatorAuthorityUpdateDTO.builder().userId("1").roleCode("role1").authorityStatus(AuthorityStatus.ACTIVE).build(),
            AccountOperatorAuthorityUpdateDTO.builder().userId("2").roleCode("invalid_role").authorityStatus(AuthorityStatus.ACTIVE).build()
        );
        Map<AccountContactType, String> contactTypes = Map.of(AccountContactType.FINANCIAL, "user");
        AccountOperatorAuthorityUpdateWrapperDTO wrapper = 
                AccountOperatorAuthorityUpdateWrapperDTO.builder()
                    .accountOperatorAuthorityUpdateList(accountUsers)
                    .contactTypes(contactTypes)
                    .build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(currentUser);

        //invoke
        mockMvc.perform(
            MockMvcRequestBuilders.post(BASE_PATH + ACCOUNT_OPERATOR_USERS_PATH + "/" + 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(wrapper))
        )
            .andExpect(status().isNoContent());

        verify(accountOperatorUserAuthorityUpdateOrchestrator, times(1))
                .updateAccountOperatorAuthorities(accountUsers, contactTypes, 1L);
    }

    @Test
    void updateAccountOperatorAuthorities_forbidden() throws Exception {
        PmrvUser currentUser = PmrvUser.builder().userId("currentuser").build();
        long accountId = 1L;

        List<AccountOperatorAuthorityUpdateDTO> accountUsers = List.of(
            AccountOperatorAuthorityUpdateDTO.builder().userId("1").roleCode("role1").authorityStatus(AuthorityStatus.ACTIVE).build(),
            AccountOperatorAuthorityUpdateDTO.builder().userId("2").roleCode("invalid_role").authorityStatus(AuthorityStatus.ACTIVE).build()
        );
        Map<AccountContactType, String> contactTypes = Map.of(AccountContactType.FINANCIAL, "user");
        AccountOperatorAuthorityUpdateWrapperDTO wrapper = 
                AccountOperatorAuthorityUpdateWrapperDTO.builder()
                    .accountOperatorAuthorityUpdateList(accountUsers)
                    .contactTypes(contactTypes)
                    .build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(currentUser);

        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(pmrvUserAuthorizationService)
            .authorize(currentUser, "updateAccountOperatorAuthorities", String.valueOf(accountId));

        //invoke
        mockMvc.perform(
            MockMvcRequestBuilders.post(BASE_PATH + ACCOUNT_OPERATOR_USERS_PATH + "/" + accountId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(wrapper))
        )
            .andExpect(status().isForbidden());

        verify(accountOperatorUserAuthorityUpdateOrchestrator, never())
                .updateAccountOperatorAuthorities(Mockito.anyList(), Mockito.anyMap(), anyLong());
    }

    @Test
    void deleteAccountOperatorAuthority() throws Exception {
        PmrvUser currentUser = PmrvUser.builder().userId("currentuser").build();
        long accountId = 1L;
        String userId = "userId";

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(currentUser);

        //invoke
        mockMvc.perform(
            MockMvcRequestBuilders.delete(
                BASE_PATH + ACCOUNT_OPERATOR_USERS_PATH  + "/" + accountId + "/" + userId))
            .andExpect(status().isNoContent());

        verify(operatorAuthorityDeletionService, times(1))
                .deleteAccountOperatorAuthority(userId, accountId);
    }

    @Test
    void deleteAccountOperatorAuthority_forbidden() throws Exception {
        PmrvUser currentUser = PmrvUser.builder().userId("currentuser").build();
        long accountId = 1L;
        String userId = "userId";

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(currentUser);

        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(pmrvUserAuthorizationService)
            .authorize(currentUser, "deleteAccountOperatorAuthority", String.valueOf(accountId));

        //invoke
        mockMvc.perform(
            MockMvcRequestBuilders.delete(
                BASE_PATH + ACCOUNT_OPERATOR_USERS_PATH  + "/" + accountId + "/" + userId))
            .andExpect(status().isForbidden());

        verify(operatorAuthorityDeletionService, never())
                .deleteAccountOperatorAuthority(anyString(), anyLong());
    }

    @Test
    void deleteCurrentUserAccountOperatorAuthority() throws Exception {
        PmrvUser currentUser = PmrvUser.builder().userId("currentuser").build();
        long accountId = 1L;

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(currentUser);

        //invoke
        mockMvc.perform(
            MockMvcRequestBuilders.delete(
                BASE_PATH + ACCOUNT_OPERATOR_USERS_PATH  + "/" + accountId))
            .andExpect(status().isNoContent());
    }

    @Test
    void deleteCurrentUserAccountOperatorAuthority_forbidden() throws Exception {
        PmrvUser currentUser = PmrvUser.builder().userId("currentuser").build();
        long accountId = 1L;

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(currentUser);

        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(pmrvUserAuthorizationService)
            .authorize(currentUser, "deleteCurrentUserAccountOperatorAuthority", String.valueOf(accountId));

        //invoke
        mockMvc.perform(
            MockMvcRequestBuilders.delete(
                BASE_PATH + ACCOUNT_OPERATOR_USERS_PATH  + "/" + accountId))
            .andExpect(status().isForbidden());
    }
}
