package uk.gov.pmrv.api.web.controller.account;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import uk.gov.pmrv.api.account.domain.dto.AccountDetailsDTO;
import uk.gov.pmrv.api.account.domain.dto.AccountHeaderInfoDTO;
import uk.gov.pmrv.api.account.domain.enumeration.AccountStatus;
import uk.gov.pmrv.api.authorization.rules.services.PmrvUserAuthorizationService;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.web.controller.exception.ExceptionControllerAdvice;
import uk.gov.pmrv.api.web.orchestrator.AccountPermitQueryOrchestrator;
import uk.gov.pmrv.api.web.security.AuthorizationAspectUserResolver;
import uk.gov.pmrv.api.web.security.AuthorizedAspect;
import uk.gov.pmrv.api.web.security.PmrvSecurityComponent;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class AccountViewControllerTest {

    private static final String CONTROLLER_PATH = "/v1.0/account";

    private MockMvc mockMvc;

    @InjectMocks
    private AccountViewController accountViewController;

    @Mock
    private PmrvSecurityComponent pmrvSecurityComponent;

    @Mock
    private PmrvUserAuthorizationService pmrvUserAuthorizationService;

    @Mock
    private AccountPermitQueryOrchestrator orchestrator;

    private AuthorizationAspectUserResolver authorizationAspectUserResolver;

    @BeforeEach
    public void setUp() {
        authorizationAspectUserResolver = new AuthorizationAspectUserResolver(pmrvSecurityComponent);
        AuthorizedAspect aspect = new AuthorizedAspect(pmrvUserAuthorizationService, authorizationAspectUserResolver);

        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(accountViewController);
        aspectJProxyFactory.addAspect(aspect);

        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);

        accountViewController = (AccountViewController) aopProxy.getProxy();

        mockMvc = MockMvcBuilders.standaloneSetup(accountViewController)
            .setControllerAdvice(new ExceptionControllerAdvice()).build();
    }

    @Test
    void getAccountById() throws Exception {
        final Long accountId = 1L;
        final PmrvUser user = PmrvUser.builder().userId("userId").build();
        final AccountDetailsDTO accountExpected =
            AccountDetailsDTO.builder()
                .id(accountId)
                .build();
        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        when(orchestrator.getAccountDetailsDtoWithPermit(accountId)).thenReturn(accountExpected);

        mockMvc.perform(
                MockMvcRequestBuilders
                    .get(CONTROLLER_PATH + "/" + accountId)
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(accountId));

        verify(pmrvSecurityComponent, times(1)).getAuthenticatedUser();
        verify(orchestrator, times(1)).getAccountDetailsDtoWithPermit(accountId);
    }

    @Test
    void getAccountById_account_forbidden() throws Exception {
        final long invalidAccountId = 1L;
        final PmrvUser user = PmrvUser.builder().userId("userId").build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(pmrvUserAuthorizationService)
            .authorize(user, "getAccountById", Long.toString(invalidAccountId));

        mockMvc.perform(
                MockMvcRequestBuilders
                    .get(CONTROLLER_PATH + "/" + invalidAccountId)
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());

        verify(orchestrator, never()).getAccountDetailsDtoWithPermit(anyLong());
    }

    @Test
    void getAccountById_account_not_found() throws Exception {
        final Long invalidAccountId = 1L;
        final PmrvUser user = PmrvUser.builder().userId("userId").build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        when(orchestrator.getAccountDetailsDtoWithPermit(invalidAccountId))
            .thenThrow(new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

        mockMvc.perform(
                MockMvcRequestBuilders
                    .get(CONTROLLER_PATH + "/" + invalidAccountId)
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());

        verify(orchestrator, times(1)).getAccountDetailsDtoWithPermit(invalidAccountId);
    }

    @Test
    void getAccountHeaderInfoById() throws Exception {
        Long accountId = 1L;
        String accountName = "accountName";
        AccountStatus accountStatus = AccountStatus.LIVE;
        PmrvUser user = PmrvUser.builder().userId("userId").build();
        AccountHeaderInfoDTO accountHeaderInfo =
            AccountHeaderInfoDTO.builder()
                .name(accountName)
                .status(accountStatus)
                .build();
        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        when(orchestrator.getAccountHeaderInfoWithPermitId(accountId)).thenReturn(Optional.of(accountHeaderInfo));

        mockMvc.perform(
            MockMvcRequestBuilders
                .get(CONTROLLER_PATH + "/" + accountId + "/header-info")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value(accountName))
            .andExpect(jsonPath("$.status").value(accountStatus.name()));

        verify(pmrvSecurityComponent, times(1)).getAuthenticatedUser();
        verify(orchestrator, times(1)).getAccountHeaderInfoWithPermitId(accountId);
    }

    @Test
    void getAccountHeaderInfoById_forbidden() throws Exception {
        String accountId = "1";
        PmrvUser user = PmrvUser.builder().userId("userId").build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(pmrvUserAuthorizationService)
            .authorize(user, "getAccountHeaderInfoById", accountId);

        mockMvc.perform(
            MockMvcRequestBuilders
                .get(CONTROLLER_PATH + "/" + accountId + "/header-info")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());

        verifyNoInteractions(orchestrator);
    }

}
