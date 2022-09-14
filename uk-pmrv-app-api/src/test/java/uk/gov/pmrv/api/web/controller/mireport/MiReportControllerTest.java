package uk.gov.pmrv.api.web.controller.mireport;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.aop.framework.AopProxy;
import org.springframework.aop.framework.DefaultAopProxyFactory;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;
import org.springframework.http.MediaType;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.gov.pmrv.api.account.domain.enumeration.AccountStatus;
import uk.gov.pmrv.api.account.domain.enumeration.AccountType;
import uk.gov.pmrv.api.authorization.core.domain.AuthorityStatus;
import uk.gov.pmrv.api.authorization.rules.services.RoleAuthorizationService;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.common.domain.model.PmrvAuthority;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.mireport.domain.MiReportEntity;
import uk.gov.pmrv.api.mireport.domain.dto.AccountUserContact;
import uk.gov.pmrv.api.mireport.domain.dto.AccountsUsersContactsMiReportResult;
import uk.gov.pmrv.api.mireport.domain.dto.EmptyMiReportParams;
import uk.gov.pmrv.api.mireport.domain.dto.MiReportResult;
import uk.gov.pmrv.api.mireport.domain.dto.MiReportSearchResult;
import uk.gov.pmrv.api.mireport.enumeration.MiReportType;
import uk.gov.pmrv.api.mireport.service.MiReportService;
import uk.gov.pmrv.api.permit.domain.PermitType;
import uk.gov.pmrv.api.web.config.PmrvUserArgumentResolver;
import uk.gov.pmrv.api.web.controller.exception.ExceptionControllerAdvice;
import uk.gov.pmrv.api.web.security.AuthorizationAspectUserResolver;
import uk.gov.pmrv.api.web.security.AuthorizedRoleAspect;
import uk.gov.pmrv.api.web.security.PmrvSecurityComponent;

@ExtendWith(MockitoExtension.class)
class MiReportControllerTest {

    public static final String MI_REPORT_BASE_CONTROLLER_PATH = "/v1.0/mireports";

    private MockMvc mockMvc;

    @InjectMocks
    private MiReportController miReportController;

    @Mock
    private PmrvSecurityComponent pmrvSecurityComponent;

    @Mock
    private MiReportService miReportService;

    @Mock
    private RoleAuthorizationService roleAuthorizationService;

    private ObjectMapper objectMapper;

    private static final String USER_ID = "userId";
    private static final Long ACCOUNT_ID = 1L;

    @BeforeEach
    public void setUp() {
        AuthorizationAspectUserResolver authorizationAspectUserResolver = new AuthorizationAspectUserResolver(pmrvSecurityComponent);
        AuthorizedRoleAspect
            authorizedRoleAspect = new AuthorizedRoleAspect(roleAuthorizationService, authorizationAspectUserResolver);
        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(miReportController);
        aspectJProxyFactory.addAspect(authorizedRoleAspect);
        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);
        miReportController = (MiReportController) aopProxy.getProxy();

        mockMvc = MockMvcBuilders.standaloneSetup(miReportController)
            .setControllerAdvice(new ExceptionControllerAdvice())
            .setCustomArgumentResolvers(new PmrvUserArgumentResolver(pmrvSecurityComponent))
            .addFilters(new FilterChainProxy(Collections.emptyList()))
            .build();

        objectMapper = new ObjectMapper();
    }

    @Test
    void getCurrentUserReports() throws Exception {
        List<MiReportSearchResult> searchResults = buildMockMiReports();
        PmrvUser pmrvUser = buildMockAuthenticatedUser();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(pmrvUser);
        when(miReportService.findByCompetentAuthority(pmrvUser.getCompetentAuthority())).thenReturn(searchResults);

        mockMvc.perform(MockMvcRequestBuilders.get(MI_REPORT_BASE_CONTROLLER_PATH + "/types")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(searchResults.size()))
            .andExpect(jsonPath("$.[0].miReportType").value(MiReportType.LIST_OF_ACCOUNTS_USERS_CONTACTS.name()))
        ;

        verify(miReportService, times(1)).findByCompetentAuthority(pmrvUser.getCompetentAuthority());
    }

    @Test
    void getCurrentUserReports_forbidden() throws Exception {
        PmrvUser pmrvUser = PmrvUser.builder().roleType(RoleType.VERIFIER).build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(pmrvUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(roleAuthorizationService)
            .evaluate(pmrvUser, new RoleType[]{RoleType.REGULATOR});

        mockMvc.perform(MockMvcRequestBuilders.get(MI_REPORT_BASE_CONTROLLER_PATH + "/types")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());

        verify(miReportService, never()).findByCompetentAuthority(pmrvUser.getCompetentAuthority());
    }

    @Test
    void getReportBy_LIST_OF_ACCOUNTS_USERS_CONTACTS() throws Exception {
        MiReportResult miReportResult = buildMockMiAccountsUsersContactsReport();
        AccountsUsersContactsMiReportResult
            accountsUsersContactsMiReport = (AccountsUsersContactsMiReportResult) miReportResult;
        AccountUserContact accountUserContact = accountsUsersContactsMiReport.getPayload().get(0);
        PmrvUser pmrvUser = buildMockAuthenticatedUser();
        MiReportType reportType = MiReportType.LIST_OF_ACCOUNTS_USERS_CONTACTS;
        EmptyMiReportParams reportParams = EmptyMiReportParams.builder().reportType(reportType).build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(pmrvUser);
        when(miReportService.generateReport(pmrvUser, reportParams)).thenReturn(miReportResult);

        mockMvc.perform(MockMvcRequestBuilders
                .post(MI_REPORT_BASE_CONTROLLER_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reportParams)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.reportType").value(MiReportType.LIST_OF_ACCOUNTS_USERS_CONTACTS.name()))
            .andExpect(jsonPath("$.payload[0].name").value(accountUserContact.getName()))
            .andExpect(jsonPath("$.payload[0].telephone").value(accountUserContact.getTelephone()))
            .andExpect(jsonPath("$.payload[0].lastLogon").value(accountUserContact.getLastLogon()))
            .andExpect(jsonPath("$.payload[0].email").value(accountUserContact.getEmail()))
            .andExpect(jsonPath("$.payload[0].role").value(accountUserContact.getRole()))
            .andExpect(jsonPath("$.payload[0].accountId").value(accountUserContact.getAccountId()))
            .andExpect(jsonPath("$.payload[0].accountName").value(accountUserContact.getAccountName()))
            .andExpect(jsonPath("$.payload[0].accountStatus").value(accountUserContact.getAccountStatus().toString()))
            .andExpect(jsonPath("$.payload[0].accountType").value(accountUserContact.getAccountType().toString()))
            .andExpect(jsonPath("$.payload[0].authorityStatus").value(accountUserContact.getAuthorityStatus().toString()))
            .andExpect(jsonPath("$.payload[0].isFinancialContact").value(accountUserContact.isFinancialContact()))
            .andExpect(jsonPath("$.payload[0].isPrimaryContact").value(accountUserContact.isPrimaryContact()))
            .andExpect(jsonPath("$.payload[0].isSecondaryContact").value(accountUserContact.isSecondaryContact()))
            .andExpect(jsonPath("$.payload[0].isServiceContact").value(accountUserContact.isServiceContact()))
            .andExpect(jsonPath("$.payload[0].legalEntityName").value(accountUserContact.getLegalEntityName()))
            .andExpect(jsonPath("$.payload[0].permitId").value(accountUserContact.getPermitId()))
            .andExpect(jsonPath("$.payload[0].permitType").value(accountUserContact.getPermitType()));

        verify(miReportService, times(1)).generateReport(pmrvUser, reportParams);
    }

    @Test
    void getReport_not_found() throws Exception {
        PmrvUser pmrvUser = buildMockAuthenticatedUser();
        MiReportType reportType = MiReportType.LIST_OF_ACCOUNTS_USERS_CONTACTS;
        EmptyMiReportParams reportParams = EmptyMiReportParams.builder().reportType(reportType).build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(pmrvUser);
        doThrow(new BusinessException(ErrorCode.MI_REPORT_TYPE_NOT_SUPPORTED)).when(miReportService).generateReport(pmrvUser, reportParams);

        mockMvc.perform(MockMvcRequestBuilders
                .post(MI_REPORT_BASE_CONTROLLER_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reportParams)))
            .andExpect(status().isConflict());

        verify(miReportService, times(1)).generateReport(pmrvUser, reportParams);
    }

    @Test
    void getReport_forbidden() throws Exception {
        PmrvUser pmrvUser = buildMockAuthenticatedUser();
        MiReportType reportType = MiReportType.LIST_OF_ACCOUNTS_USERS_CONTACTS;
        EmptyMiReportParams reportParams = EmptyMiReportParams.builder().reportType(reportType).build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(pmrvUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(roleAuthorizationService)
            .evaluate(pmrvUser, new RoleType[]{RoleType.REGULATOR});

        mockMvc.perform(MockMvcRequestBuilders
                .post(MI_REPORT_BASE_CONTROLLER_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reportParams)))
            .andExpect(status().isForbidden());

        verify(miReportService, times(0)).generateReport(pmrvUser, reportParams);
    }

    private MiReportResult buildMockMiAccountsUsersContactsReport() {
        AccountUserContact accountUserContact = AccountUserContact.builder()
            .name("Foo Bar")
            .telephone("")
            .lastLogon("")
            .email("test@test.com")
            .role("Operator")
            .accountId(ACCOUNT_ID)
            .accountName("account name")
            .accountStatus(AccountStatus.LIVE)
            .accountType(AccountType.INSTALLATION)
            .authorityStatus(AuthorityStatus.ACTIVE)
            .isFinancialContact(Boolean.TRUE)
            .isPrimaryContact(Boolean.TRUE)
            .isSecondaryContact(Boolean.FALSE)
            .isServiceContact(Boolean.FALSE)
            .legalEntityName("Legal")
            .permitId("Permit id 1")
            .permitType(PermitType.GHGE.toString())
            .build();

        return AccountsUsersContactsMiReportResult.builder()
            .reportType(MiReportType.LIST_OF_ACCOUNTS_USERS_CONTACTS)
            .payload(List.of(accountUserContact))
            .build();
    }

    private PmrvUser buildMockAuthenticatedUser() {
        return PmrvUser.builder()
            .authorities(
                Arrays.asList(
                    PmrvAuthority.builder().competentAuthority(CompetentAuthority.ENGLAND).build()
                )
            )
            .roleType(RoleType.REGULATOR)
            .userId(USER_ID)
            .build();
    }

    private List<MiReportSearchResult> buildMockMiReports() {
        ProjectionFactory factory = new SpelAwareProxyProjectionFactory();
        return Arrays.stream(MiReportType.values())
            .map(t -> MiReportEntity.builder().miReportType(t).competentAuthority(CompetentAuthority.ENGLAND))
            .map(e -> factory.createProjection(MiReportSearchResult.class, e))
            .collect(Collectors.toList());
    }

}