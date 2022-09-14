package uk.gov.pmrv.api.authorization.core.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.authorization.core.domain.Authority;
import uk.gov.pmrv.api.authorization.core.domain.AuthorityPermission;
import uk.gov.pmrv.api.authorization.core.domain.AuthorityStatus;
import uk.gov.pmrv.api.authorization.core.domain.dto.LoginStatus;
import uk.gov.pmrv.api.authorization.core.repository.AuthorityRepository;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.user.core.domain.dto.UserInfoDTO;
import uk.gov.pmrv.api.user.core.domain.enumeration.AuthenticationStatus;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.pmrv.api.authorization.core.domain.AuthorityStatus.ACCEPTED;
import static uk.gov.pmrv.api.authorization.core.domain.AuthorityStatus.ACTIVE;
import static uk.gov.pmrv.api.authorization.core.domain.AuthorityStatus.DISABLED;
import static uk.gov.pmrv.api.authorization.core.domain.AuthorityStatus.PENDING;
import static uk.gov.pmrv.api.authorization.core.domain.AuthorityStatus.TEMP_DISABLED;
import static uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority.ENGLAND;

@ExtendWith(MockitoExtension.class)
class UserStatusServiceTest {

    @InjectMocks
    private UserStatusService userStatusService;

    @Mock
    private UserAuthService userAuthService;

    @Mock
    private AuthorityRepository authorityRepository;

    @Test
    void getLoginStatus_when_active_authorities_with_permissions_then_enabled() {
        final String userId = "userId";

        Authority authority1 = createAuthority(userId, 1L, ENGLAND, ACTIVE);
        authority1.setAuthorityPermissions(List.of(AuthorityPermission.builder().build()));

        when(authorityRepository.findByUserId(userId)).thenReturn(List.of(authority1));

        LoginStatus loginStatus = userStatusService.getLoginStatus(userId);

        assertThat(loginStatus).isEqualTo(LoginStatus.ENABLED);
        verify(userAuthService, never()).getUserByUserId(anyString());
    }

    @Test
    void getLoginStatus_when_active_authorities_without_permissions_then_no_authority() {
        final String userId = "userId";

        Authority authority1 = createAuthority(userId, 1L, ENGLAND, ACTIVE);

        when(authorityRepository.findByUserId(userId)).thenReturn(List.of(authority1));

        LoginStatus loginStatus = userStatusService.getLoginStatus(userId);

        assertThat(loginStatus).isEqualTo(LoginStatus.NO_AUTHORITY);
        verify(userAuthService, never()).getUserByUserId(anyString());
    }

    @Test
    void getLoginStatus_when_two_active_authorities_only_one_with_permissions_then_enabled() {
        final String userId = "userId";

        Authority authority1 = createAuthority(userId, 1L, ENGLAND, ACTIVE);
        authority1.setAuthorityPermissions(List.of(AuthorityPermission.builder().build()));

        Authority authority2 = createAuthority(userId, 2L, ENGLAND, ACTIVE);

        when(authorityRepository.findByUserId(userId)).thenReturn(List.of(authority1, authority2));

        LoginStatus loginStatus = userStatusService.getLoginStatus(userId);

        assertThat(loginStatus).isEqualTo(LoginStatus.ENABLED);
        verify(userAuthService, never()).getUserByUserId(anyString());
    }

    @Test
    void getLoginStatus_when_no_active_authorities_but_temp_disabled_then_temp_disabled() {
        final String userId = "userId";

        Authority authority1 = createAuthority(userId, 1L, ENGLAND, TEMP_DISABLED);
        Authority authority2 = createAuthority(userId, 2L, ENGLAND, DISABLED);

        when(authorityRepository.findByUserId(userId)).thenReturn(List.of(authority1, authority2));

        LoginStatus loginStatus = userStatusService.getLoginStatus(userId);

        assertThat(loginStatus).isEqualTo(LoginStatus.TEMP_DISABLED);
        verify(userAuthService, never()).getUserByUserId(anyString());
    }

    @Test
    void getLoginStatus_when_nor_active_authorities_neither_temp_disabled_then_disabled() {
        final String userId = "userId";

        Authority authority1 = createAuthority(userId, 1L, ENGLAND, DISABLED);
        Authority authority2 = createAuthority(userId, 2L, ENGLAND, PENDING);

        when(authorityRepository.findByUserId(userId)).thenReturn(List.of(authority1, authority2));

        LoginStatus loginStatus = userStatusService.getLoginStatus(userId);

        assertThat(loginStatus).isEqualTo(LoginStatus.DISABLED);
        verify(userAuthService, never()).getUserByUserId(anyString());
    }

    @Test
    void getLoginStatus_when_nor_active_authorities_and_accepted() {
        final String userId = "userId";

        Authority authority = createAuthority(userId, 1L, ENGLAND, ACCEPTED);

        when(authorityRepository.findByUserId(userId)).thenReturn(List.of(authority));

        LoginStatus loginStatus = userStatusService.getLoginStatus(userId);

        assertThat(loginStatus).isEqualTo(LoginStatus.ACCEPTED);
        verify(userAuthService, never()).getUserByUserId(anyString());
    }

    @Test
    void getLoginStatus_when_no_authorities_and_auth_status_not_deleted_then_no_authority() {
        final String userId = "userId";
        final UserInfoDTO userInfoDTO = UserInfoDTO.builder().status(AuthenticationStatus.REGISTERED).build();

        when(authorityRepository.findByUserId(userId)).thenReturn(Collections.emptyList());
        when(userAuthService.getUserByUserId(userId)).thenReturn(userInfoDTO);

        LoginStatus loginStatus = userStatusService.getLoginStatus(userId);

        assertThat(loginStatus).isEqualTo(LoginStatus.NO_AUTHORITY);
        verify(userAuthService, times(1)).getUserByUserId(userId);
    }

    @Test
    void getLoginStatus_when_no_authorities_and_auth_status_is_deleted_then_deleted() {
        final String userId = "userId";
        final UserInfoDTO userInfoDTO = UserInfoDTO.builder().status(AuthenticationStatus.DELETED).build();

        when(authorityRepository.findByUserId(userId)).thenReturn(Collections.emptyList());
        when(userAuthService.getUserByUserId(userId)).thenReturn(userInfoDTO);

        LoginStatus loginStatus = userStatusService.getLoginStatus(userId);

        assertThat(loginStatus).isEqualTo(LoginStatus.DELETED);
        verify(userAuthService, times(1)).getUserByUserId(userId);
    }

    private Authority createAuthority(String userId, Long accountId, CompetentAuthority competentAuthority, AuthorityStatus status) {
        return Authority.builder()
                .userId(userId)
                .competentAuthority(competentAuthority)
                .accountId(accountId)
                .status(status)
                .build();
    }
}
