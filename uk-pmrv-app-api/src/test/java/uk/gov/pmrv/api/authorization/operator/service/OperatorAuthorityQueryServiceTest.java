package uk.gov.pmrv.api.authorization.operator.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.authorization.core.domain.dto.AuthorityRoleDTO;
import uk.gov.pmrv.api.authorization.core.domain.dto.UserAuthoritiesDTO;
import uk.gov.pmrv.api.authorization.core.domain.dto.UserAuthorityDTO;
import uk.gov.pmrv.api.authorization.core.repository.AuthorityRepository;
import uk.gov.pmrv.api.authorization.rules.domain.Scope;
import uk.gov.pmrv.api.authorization.rules.services.resource.AccountAuthorizationResourceService;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;

@ExtendWith(MockitoExtension.class)
class OperatorAuthorityQueryServiceTest {

    @InjectMocks
    private OperatorAuthorityQueryService service;
    
    @Mock
    private AuthorityRepository authorityRepository;

    @Mock
    private AccountAuthorizationResourceService accountAuthorizationResourceService;
    
    @Test
    void getAccountAuthorities_has_edit_user_scope_on_account() {
        PmrvUser authUser = new PmrvUser();
        Long accountId = 1L;
        String user = "user";

        List<AuthorityRoleDTO> authorityRoles = List.of(AuthorityRoleDTO.builder().userId(user).build());
        UserAuthorityDTO accountOperatorAuthority =
            UserAuthorityDTO.builder().userId(user).build();


        when(accountAuthorizationResourceService.hasUserScopeToAccount(authUser, accountId, Scope.EDIT_USER)).thenReturn(true);
        when(authorityRepository.findOperatorUserAuthorityRoleListByAccount(accountId)).thenReturn(authorityRoles);

        UserAuthoritiesDTO result = service.getAccountAuthorities(authUser, accountId);

        assertTrue(result.isEditable());
        assertThat(result.getAuthorities()).hasSize(1);
        assertThat(result.getAuthorities().get(0)).isEqualTo(accountOperatorAuthority);

        verify(accountAuthorizationResourceService, times(1))
                .hasUserScopeToAccount(authUser, accountId, Scope.EDIT_USER);
        verify(authorityRepository, times(1))
                .findOperatorUserAuthorityRoleListByAccount(accountId);
        verifyNoMoreInteractions(authorityRepository);
    }

    @Test
    void getAccountAuthorities_has_not_edit_user_scope_on_account() {
        PmrvUser authUser = new PmrvUser();
        Long accountId = 1L;
        String user = "user";

        List<AuthorityRoleDTO> authorityRoles = List.of(AuthorityRoleDTO.builder().userId(user).build());
        UserAuthorityDTO accountOperatorAuthority =
            UserAuthorityDTO.builder().userId(user).build();


        when(accountAuthorizationResourceService.hasUserScopeToAccount(authUser, accountId, Scope.EDIT_USER)).thenReturn(false);
        when(authorityRepository.findNonPendingOperatorUserAuthorityRoleListByAccount(accountId)).thenReturn(authorityRoles);

        UserAuthoritiesDTO result = service.getAccountAuthorities(authUser, accountId);

        assertFalse(result.isEditable());
        assertThat(result.getAuthorities()).hasSize(1);
        assertThat(result.getAuthorities().get(0)).isEqualTo(accountOperatorAuthority);

        verify(accountAuthorizationResourceService, times(1))
            .hasUserScopeToAccount(authUser, accountId, Scope.EDIT_USER);
        verify(authorityRepository, times(1))
            .findNonPendingOperatorUserAuthorityRoleListByAccount(accountId);
        verifyNoMoreInteractions(authorityRepository);
    }
}
