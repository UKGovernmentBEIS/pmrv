package uk.gov.pmrv.api.web.orchestrator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.domain.enumeration.AccountContactType;
import uk.gov.pmrv.api.account.service.AccountContactQueryService;
import uk.gov.pmrv.api.authorization.core.domain.dto.UserAuthoritiesDTO;
import uk.gov.pmrv.api.authorization.core.domain.dto.UserAuthorityDTO;
import uk.gov.pmrv.api.authorization.operator.service.OperatorAuthorityQueryService;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.user.core.domain.dto.UserInfoDTO;
import uk.gov.pmrv.api.user.operator.service.OperatorUserInfoService;
import uk.gov.pmrv.api.web.orchestrator.dto.AccountOperatorsUsersAuthoritiesInfoDTO;
import uk.gov.pmrv.api.web.orchestrator.dto.UserAuthorityInfoDTO;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.pmrv.api.authorization.core.domain.AuthorityStatus.ACTIVE;

@ExtendWith(MockitoExtension.class)
class AccountOperatorUserAuthorityQueryOrchestratorTest {
    @InjectMocks
    private AccountOperatorUserAuthorityQueryOrchestrator service;

    @Mock
    private OperatorUserInfoService operatorUserInfoService;

    @Mock
    private AccountContactQueryService accountContactQueryService;

    @Mock
    private OperatorAuthorityQueryService operatorAuthorityQueryService;

    @Test
    void getAccountAuthorities_has_edit_user_scope_on_account() {
        PmrvUser authUser = new PmrvUser();
        Long accountId = 1L;
        String user = "user";

        UserInfoDTO userInfo = UserInfoDTO.builder().userId(user).locked(true).build();
        UserAuthorityInfoDTO accountOperatorUserAuthorityInfo =
            UserAuthorityInfoDTO.builder().userId(user).authorityStatus(ACTIVE).locked(true).build();

        Map<AccountContactType, String> contactTypes = Map.of(
                AccountContactType.PRIMARY, "primary",
                AccountContactType.SERVICE, "service"
        );

        UserAuthorityDTO accountOperatorAuthority =
            UserAuthorityDTO.builder().userId(user).authorityStatus(ACTIVE).build();
        UserAuthoritiesDTO accountOperatorAuthorities =
            UserAuthoritiesDTO.builder()
                        .authorities(List.of(accountOperatorAuthority))
                        .editable(true)
                        .build();
        when(operatorAuthorityQueryService.getAccountAuthorities(authUser, accountId)).thenReturn(accountOperatorAuthorities);
        when(operatorUserInfoService.getOperatorUsersInfo(authUser, accountId, List.of(user))).thenReturn(List.of(userInfo));
        when(accountContactQueryService.findOperatorContactTypesByAccount(accountId)).thenReturn(contactTypes);

        AccountOperatorsUsersAuthoritiesInfoDTO
            result = service.getAccountOperatorsUsersAuthoritiesInfo(authUser, accountId);

        assertTrue(result.isEditable());
        assertThat(result.getAuthorities()).hasSize(1);
        assertEquals(accountOperatorUserAuthorityInfo, result.getAuthorities().get(0));
        assertThat(result.getContactTypes()).isEqualTo(contactTypes);

        verify(operatorAuthorityQueryService, times(1)).getAccountAuthorities(authUser, accountId);
        verify(operatorUserInfoService, times(1)).getOperatorUsersInfo(authUser, accountId, List.of(user));
        verify(accountContactQueryService, times(1)).findOperatorContactTypesByAccount(accountId);
    }

    @Test
    void getAccountAuthorities_has_not_edit_user_scope_on_account() {
        PmrvUser authUser = new PmrvUser();
        Long accountId = 1L;
        String user = "user";

        UserInfoDTO userInfo = UserInfoDTO.builder().userId(user).build();
        UserAuthorityInfoDTO accountOperatorUserAuthorityInfo =
            UserAuthorityInfoDTO.builder().userId(user).build();

        Map<AccountContactType, String> contactTypes = Map.of(
                AccountContactType.PRIMARY, "primary",
                AccountContactType.SERVICE, "service"
        );

        UserAuthorityDTO accountOperatorAuthority =
            UserAuthorityDTO.builder().userId(user).build();
        UserAuthoritiesDTO accountOperatorAuthorities =
            UserAuthoritiesDTO.builder()
                        .authorities(List.of(accountOperatorAuthority))
                        .editable(false)
                        .build();
        when(operatorAuthorityQueryService.getAccountAuthorities(authUser, accountId)).thenReturn(accountOperatorAuthorities);
        when(operatorUserInfoService.getOperatorUsersInfo(authUser, accountId, List.of(user))).thenReturn(List.of(userInfo));
        when(accountContactQueryService.findOperatorContactTypesByAccount(accountId)).thenReturn(contactTypes);

        AccountOperatorsUsersAuthoritiesInfoDTO
            result = service.getAccountOperatorsUsersAuthoritiesInfo(authUser, accountId);

        assertFalse(result.isEditable());
        assertThat(result.getAuthorities()).hasSize(1);
        assertEquals(accountOperatorUserAuthorityInfo, result.getAuthorities().get(0));
        assertThat(result.getContactTypes()).isEqualTo(contactTypes);

        verify(operatorAuthorityQueryService, times(1)).getAccountAuthorities(authUser, accountId);
        verify(operatorUserInfoService, times(1)).getOperatorUsersInfo(authUser, accountId, List.of(user));
        verify(accountContactQueryService, times(1)).findOperatorContactTypesByAccount(accountId);
    }
}