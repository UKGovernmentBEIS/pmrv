package uk.gov.pmrv.api.web.security;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.representations.AccessToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.authorization.core.domain.dto.AuthorityDTO;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.common.transform.PmrvUserMapper;

import java.util.List;
import java.util.stream.Collectors;

import static uk.gov.pmrv.api.common.domain.enumeration.RoleType.OPERATOR;
import static uk.gov.pmrv.api.web.constants.SecurityConstants.CLAIM_ROLE_TYPE;

/**
 * The PmrvSecurity extracting security acknowledge objects.
 */
@Component
@RequiredArgsConstructor
public class PmrvSecurityComponent {

    private final PmrvUserMapper pmrvUserMapper;

    /**
     * Returns authorities permissions of authenticated user.
     *
     * @return List of {@link AuthorityDTO}
     */
    public PmrvUser getAuthenticatedUser() {
        AccessToken accessToken = getKeycloakSecurityContext().getToken();
        RoleType roleType = (RoleType) accessToken.getOtherClaims().get(CLAIM_ROLE_TYPE);
        return pmrvUserMapper.toPmrvUser(accessToken, getAuthorities(roleType), roleType);
    }

    /**
     * Returns the user's access token.
     *
     * @return {@link AccessToken}
     */
    public String getAccessToken() {
        return getKeycloakSecurityContext().getTokenString();
    }

    @SuppressWarnings("unchecked")
    private KeycloakSecurityContext getKeycloakSecurityContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        KeycloakPrincipal<KeycloakSecurityContext> principal = (KeycloakPrincipal<KeycloakSecurityContext>) authentication
                .getPrincipal();
        return principal.getKeycloakSecurityContext();
    }

    private List<AuthorityDTO> getAuthorities(RoleType roleType) {
        return OPERATOR.equals(roleType) ? getOperatorUserAuthorities() : getUserAuthorities();
    }

    private List<AuthorityDTO> getUserAuthorities() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .map(AuthorityDTO.class::cast)
                .collect(Collectors.toList());
    }

    private List<AuthorityDTO> getOperatorUserAuthorities() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .map(AuthorityDTO.class::cast)
                .filter(authority -> !ObjectUtils.isEmpty(authority.getAuthorityPermissions()))
                .collect(Collectors.toList());
    }

}
