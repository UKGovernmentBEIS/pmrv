package uk.gov.pmrv.api.web.security;

import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.springframework.security.core.Authentication;
import uk.gov.pmrv.api.authorization.core.domain.dto.AuthorityDTO;
import uk.gov.pmrv.api.authorization.core.service.AuthorityService;
import uk.gov.pmrv.api.authorization.core.service.UserRoleTypeService;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;

import java.util.List;

import static uk.gov.pmrv.api.web.constants.SecurityConstants.CLAIM_ROLE_TYPE;

/**
 * PMRV keycloak custom authentication provider. <br/>
 * It retrieves the authenticated user from the keycloak security context and enhances the 
 * returned {@link Authentication} with the PMRV roles of the user
 *
 */
public class PmrvKeycloakProvider extends KeycloakAuthenticationProvider {

    /**
     * The {@link AuthorityService}.
     */
    private final AuthorityService authorityService;

    /**
     * The {@link UserRoleTypeService}.
     */
    private final UserRoleTypeService userRoleTypeService;

    /**
     * The {@link PmrvKeycloakProvider} constructor.
     *
     * @param authorityService {@link AuthorityService}
     * @param userRoleTypeService {@link UserRoleTypeService}
     */
    public PmrvKeycloakProvider(AuthorityService authorityService,
                                UserRoleTypeService userRoleTypeService) {
        this.authorityService = authorityService;
        this.userRoleTypeService = userRoleTypeService;
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Override
    public Authentication authenticate(Authentication authentication) {
        KeycloakPrincipal<KeycloakSecurityContext> principal = (KeycloakPrincipal<KeycloakSecurityContext>)authentication.getPrincipal();
        AccessToken accessToken = principal.getKeycloakSecurityContext().getToken();

        principal.getKeycloakSecurityContext().getToken()
            .setOtherClaims(CLAIM_ROLE_TYPE, getRoleType(accessToken.getSubject()));

        KeycloakAuthenticationToken token = (KeycloakAuthenticationToken) authentication;

        //TODO: cache query
        List<AuthorityDTO> authorities = authorityService.getActiveAuthoritiesWithAssignedPermissions(accessToken.getSubject());

        return new KeycloakAuthenticationToken(token.getAccount(), token.isInteractive(), authorities);
    }

    private RoleType getRoleType(String userId) {
        return userRoleTypeService.getUserRoleTypeByUserId(userId).getRoleType();
    }

}
