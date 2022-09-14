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
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.gov.pmrv.api.account.domain.dto.AccountContactDTO;
import uk.gov.pmrv.api.account.domain.dto.AccountContactInfoDTO;
import uk.gov.pmrv.api.account.domain.dto.AccountContactInfoResponse;
import uk.gov.pmrv.api.account.service.AccountCaSiteContactService;
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
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CaSiteContactControllerTest {

    private static final String CA_SITE_CONTACT_CONTROLLER_PATH = "/v1.0/ca-site-contacts";

    private MockMvc mockMvc;

    @InjectMocks
    private CaSiteContactController controller;

    @Mock
    private PmrvSecurityComponent pmrvSecurityComponent;

    @Mock
    private AccountCaSiteContactService accountCaSiteContactService;

    @Mock
    private RoleAuthorizationService roleAuthorizationService;

    @Mock
    private PmrvUserAuthorizationService pmrvUserAuthorizationService;

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
        controller = (CaSiteContactController) aopProxy.getProxy();

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setCustomArgumentResolvers(new PmrvUserArgumentResolver(pmrvSecurityComponent))
                .setControllerAdvice(new ExceptionControllerAdvice())
                .build();

        objectMapper = new ObjectMapper();
    }

    @Test
    void getCaSiteContacts() throws Exception {
        final PmrvUser user = PmrvUser.builder().roleType(RoleType.REGULATOR).build();

        AccountContactInfoResponse accountCASiteContactInfoResponse = AccountContactInfoResponse.builder()
                .contacts(List.of(
                        AccountContactInfoDTO.builder().accountId(1L).accountName("accountName1").userId("userId1").build(),
                        AccountContactInfoDTO.builder().accountId(2L).accountName("accountName2").userId("userId2").build()))
                .editable(false).build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        when(accountCaSiteContactService.getAccountsAndCaSiteContacts(user,0, 2)).thenReturn(accountCASiteContactInfoResponse);

        mockMvc.perform(MockMvcRequestBuilders.get(CA_SITE_CONTACT_CONTROLLER_PATH + "?page=0&size=2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("contacts[0].accountId").value(1L))
                .andExpect(jsonPath("contacts[0].accountName").value("accountName1"))
                .andExpect(jsonPath("contacts[0].userId").value("userId1"))
                .andExpect(jsonPath("contacts[1].accountId").value(2L))
                .andExpect(jsonPath("contacts[1].accountName").value("accountName2"))
                .andExpect(jsonPath("contacts[1].userId").value("userId2"));

        verify(pmrvSecurityComponent, times(1)).getAuthenticatedUser();
        verify(accountCaSiteContactService, times(1)).getAccountsAndCaSiteContacts(user,0, 2);
    }

    @Test
    void getCaSiteContacts_forbidden() throws Exception {
        final PmrvUser user = PmrvUser.builder().roleType(RoleType.OPERATOR).build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
                .when(roleAuthorizationService)
                .evaluate(user,new RoleType[] {RoleType.REGULATOR});

        mockMvc.perform(MockMvcRequestBuilders.get(CA_SITE_CONTACT_CONTROLLER_PATH + "?page=0&size=2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verify(pmrvSecurityComponent, times(1)).getAuthenticatedUser();
        verify(accountCaSiteContactService, never()).getAccountsAndCaSiteContacts(any(), anyInt(), anyInt());
    }

    @Test
    void updateCaSiteContacts() throws Exception {
        final PmrvUser user = PmrvUser.builder().roleType(RoleType.REGULATOR).build();
        List<AccountContactDTO> accountCASiteContacts = List.of(
                AccountContactDTO.builder().accountId(1L).userId("userId1").build(),
                AccountContactDTO.builder().accountId(2L).userId("userId2").build()
        );

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders.post(CA_SITE_CONTACT_CONTROLLER_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(accountCASiteContacts)))
                .andExpect(status().isNoContent());

        verify(pmrvSecurityComponent, times(1)).getAuthenticatedUser();
        verify(accountCaSiteContactService, times(1)).updateCaSiteContacts(user, accountCASiteContacts);
    }

    @Test
    void updateCaSiteContacts_forbidden() throws Exception {
        final PmrvUser user = PmrvUser.builder().roleType(RoleType.REGULATOR).build();
        List<AccountContactDTO> accountCASiteContacts = List.of(
                AccountContactDTO.builder().accountId(1L).userId("userId1").build(),
                AccountContactDTO.builder().accountId(2L).userId("userId2").build()
        );

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
                .when(pmrvUserAuthorizationService)
                .authorize(user, "updateCaSiteContacts");

        mockMvc.perform(MockMvcRequestBuilders.post(CA_SITE_CONTACT_CONTROLLER_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(accountCASiteContacts)))
                .andExpect(status().isForbidden());

        verify(pmrvSecurityComponent, times(1)).getAuthenticatedUser();
        verify(accountCaSiteContactService, never()).updateCaSiteContacts(any(), anyList());
    }
}
