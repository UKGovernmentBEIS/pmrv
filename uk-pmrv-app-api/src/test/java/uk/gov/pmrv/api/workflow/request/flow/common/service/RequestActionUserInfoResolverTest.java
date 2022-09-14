package uk.gov.pmrv.api.workflow.request.flow.common.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.domain.enumeration.AccountContactType;
import uk.gov.pmrv.api.account.service.AccountContactQueryService;
import uk.gov.pmrv.api.authorization.core.domain.dto.AuthorityRoleDTO;
import uk.gov.pmrv.api.authorization.operator.service.OperatorAuthorityQueryService;
import uk.gov.pmrv.api.user.core.domain.dto.UserInfoDTO;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;

@ExtendWith(MockitoExtension.class)
class RequestActionUserInfoResolverTest {

    @InjectMocks
    private RequestActionUserInfoResolver resolver;

    @Mock
    private UserAuthService userAuthService;

    @Mock
    private AccountContactQueryService accountContactQueryService;

    @Mock
    private OperatorAuthorityQueryService operatorAuthorityQueryService;

    @Mock
    private RequestAccountContactQueryService requestAccountContactQueryService;

    @Test
    void getUserFullName() {
        String userId = "userId";
        UserInfoDTO user = UserInfoDTO.builder().userId(userId).firstName("Ope").lastName("Rator").build();
        when(userAuthService.getUserByUserId(userId)).thenReturn(user);
        String result = resolver.getUserFullName(userId);
        assertThat(result).isEqualTo("Ope Rator");
        verify(userAuthService, times(1)).getUserByUserId(userId);
    }
    
    @Test
    void getUsersInfo() {
        final Set<String> operators = Set.of("operator");
        final String signatory = "regulator";
        final Request request = Request.builder().accountId(1L).build();

        when(requestAccountContactQueryService.getRequestAccountPrimaryContact(request))
            .thenReturn(UserInfoDTO.builder().userId("accountPrimaryContactUser").build());
        when(userAuthService.getUserByUserId("operator"))
            .thenReturn(UserInfoDTO.builder().userId("operator").firstName("Ope").lastName("Rator").build());
        when(userAuthService.getUserByUserId("regulator"))
            .thenReturn(UserInfoDTO.builder().userId("regulator").firstName("Reg").lastName("Ulator").build());
        when(userAuthService.getUserByUserId("accountPrimaryContactUser"))
            .thenReturn(UserInfoDTO.builder().userId("accountPrimaryContactUser").firstName("Primary").lastName("Contact").build());
        when(accountContactQueryService.findContactTypesByAccount(request.getAccountId()))
            .thenReturn(Map.of(AccountContactType.PRIMARY, "accountPrimaryContactUser", AccountContactType.FINANCIAL, "operator"));
        when(operatorAuthorityQueryService.findOperatorUserAuthoritiesListByAccount(request.getAccountId()))
            .thenReturn(List.of(
                AuthorityRoleDTO.builder().userId("operator").roleCode("operator_user").build(),
                AuthorityRoleDTO.builder().userId("accountPrimaryContactUser").roleCode("operator_admin").build()
            ));

        final Map<String, RequestActionUserInfo> usersInfo = resolver.getUsersInfo(operators, signatory, request);

        verify(requestAccountContactQueryService, times(1)).getRequestAccountPrimaryContact(request);
        verify(userAuthService, times(1)).getUserByUserId("operator");
        verify(userAuthService, times(1)).getUserByUserId("regulator");
        verify(userAuthService, times(1)).getUserByUserId("accountPrimaryContactUser");
        verify(accountContactQueryService, times(1)).findContactTypesByAccount(request.getAccountId());
        verify(operatorAuthorityQueryService, times(1)).findOperatorUserAuthoritiesListByAccount(request.getAccountId());
        
        assertThat(usersInfo).containsExactlyInAnyOrderEntriesOf(Map.of(
            "operator", 
            RequestActionUserInfo.builder()
                .name("Ope Rator")
                .contactTypes(Set.of(AccountContactType.FINANCIAL))
                .roleCode("operator_user")
                .build(),
            "regulator",
            RequestActionUserInfo.builder()
                .name("Reg Ulator")
                .contactTypes(Set.of())
                .build(),
            "accountPrimaryContactUser",
            RequestActionUserInfo.builder()
                .name("Primary Contact")
                .contactTypes(Set.of(AccountContactType.PRIMARY))
                .roleCode("operator_admin")
                .build()
            ));
    }
}
