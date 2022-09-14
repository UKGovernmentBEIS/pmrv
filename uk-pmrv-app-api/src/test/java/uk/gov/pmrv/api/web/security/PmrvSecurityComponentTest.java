package uk.gov.pmrv.api.web.security;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.pmrv.api.web.constants.SecurityConstants.CLAIM_ROLE_TYPE;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.RefreshableKeycloakSecurityContext;
import org.keycloak.adapters.springsecurity.account.SimpleKeycloakAccount;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import uk.gov.pmrv.api.authorization.core.domain.Permission;
import uk.gov.pmrv.api.authorization.core.domain.dto.AuthorityDTO;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.common.transform.PmrvUserMapper;

@ExtendWith(MockitoExtension.class)
class PmrvSecurityComponentTest {

    @InjectMocks
    private PmrvSecurityComponent pmrvSecurityComponent;

    @Mock
    private PmrvUserMapper pmrvUserMapper;

    private SecurityContext securityContext;
    private KeycloakPrincipal principal;
    private SimpleKeycloakAccount keycloakAccount;
    private Authentication authentication;
    private KeycloakSecurityContext keycloakSecurityContext;
    private AccessToken accessToken;

    @BeforeEach
    void setUp() {
        securityContext = mock(SecurityContext.class);
        principal = mock(KeycloakPrincipal.class);
        keycloakSecurityContext = mock(KeycloakSecurityContext.class);
        accessToken = mock(AccessToken.class);
        keycloakAccount = new SimpleKeycloakAccount(principal, new HashSet<>(), mock(RefreshableKeycloakSecurityContext.class));
    }

    @Test
    void getAuthenticatedUser_regulator() {
        List<AuthorityDTO> authorityDTOS = List.of(AuthorityDTO.builder()
            .code("code")
            .competentAuthority(CompetentAuthority.ENGLAND)
            .build());

        authentication = new KeycloakAuthenticationToken(keycloakAccount, true, authorityDTOS);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(principal.getKeycloakSecurityContext()).thenReturn(keycloakSecurityContext);
        when(keycloakSecurityContext.getToken()).thenReturn(accessToken);
        when(accessToken.getOtherClaims()).thenReturn(mock(Map.class));
        when(accessToken.getOtherClaims().get(CLAIM_ROLE_TYPE)).thenReturn(RoleType.REGULATOR);

        SecurityContextHolder.setContext(securityContext);

        pmrvSecurityComponent.getAuthenticatedUser();

        verify(pmrvUserMapper, times(1)).toPmrvUser(accessToken, authorityDTOS, RoleType.REGULATOR);
    }


    @Test
    void getAuthenticatedUser_operator() {
        List<AuthorityDTO> authorityDTOS = List.of(AuthorityDTO.builder()
            .code("code")
            .accountId(1L)
            .authorityPermissions(List.of(Permission.PERM_ACCOUNT_USERS_EDIT))
            .build());

        authentication = new KeycloakAuthenticationToken(keycloakAccount, true, authorityDTOS);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(principal.getKeycloakSecurityContext()).thenReturn(keycloakSecurityContext);
        when(keycloakSecurityContext.getToken()).thenReturn(accessToken);
        when(accessToken.getOtherClaims()).thenReturn(mock(Map.class));
        when(accessToken.getOtherClaims().get(CLAIM_ROLE_TYPE)).thenReturn(RoleType.OPERATOR);

        SecurityContextHolder.setContext(securityContext);

        pmrvSecurityComponent.getAuthenticatedUser();

        verify(pmrvUserMapper, times(1)).toPmrvUser(accessToken, authorityDTOS, RoleType.OPERATOR);
    }

    @Test
    void getAuthenticatedUser_operator_authority_without_permission() {
        List<AuthorityDTO> authorityDTOS = List.of(AuthorityDTO.builder()
            .code("code")
            .accountId(1L)
            .build());

        authentication = new KeycloakAuthenticationToken(keycloakAccount, true, authorityDTOS);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(principal.getKeycloakSecurityContext()).thenReturn(keycloakSecurityContext);
        when(keycloakSecurityContext.getToken()).thenReturn(accessToken);
        when(accessToken.getOtherClaims()).thenReturn(mock(Map.class));
        when(accessToken.getOtherClaims().get(CLAIM_ROLE_TYPE)).thenReturn(RoleType.OPERATOR);

        SecurityContextHolder.setContext(securityContext);

        pmrvSecurityComponent.getAuthenticatedUser();

        verify(pmrvUserMapper, times(1))
            .toPmrvUser(accessToken, Collections.emptyList(), RoleType.OPERATOR);
    }
}
