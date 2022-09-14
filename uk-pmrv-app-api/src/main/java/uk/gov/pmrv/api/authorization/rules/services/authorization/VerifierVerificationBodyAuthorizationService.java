package uk.gov.pmrv.api.authorization.rules.services.authorization;

import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

import uk.gov.pmrv.api.authorization.core.domain.Permission;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.verificationbody.domain.VerificationBody;

/**
 * Service that checks if a VERIFIER user is authorized on {@link VerificationBody}.
 */
@Service
public class VerifierVerificationBodyAuthorizationService implements VerificationBodyAuthorizationService {

    /**
     * Checks that VERIFIER has access to the provided verification body.
     * @param user the user to authorize
     * @param verificationBodyId the verification body to check permission on
     * @return if the user is authorized on verification body.
     */
    @Override
    public boolean isAuthorized(PmrvUser user, Long verificationBodyId) {
        return user.getAuthorities()
            .stream()
            .filter(Objects::nonNull)
            .anyMatch(auth -> verificationBodyId.equals(auth.getVerificationBodyId()));
    }

    /**
     * Checks that a VERIFIER has the permissions to verification body.
     * @param user the user to authorize.
     * @param verificationBodyId the verification body to check permission on
     * @param permission the {@link Permission} to check
     * @return if the user has the permissions on the verification body
     */
    @Override
    public boolean isAuthorized(PmrvUser user, Long verificationBodyId, Permission permission) {
        return user.getAuthorities()
            .stream()
            .filter(Objects::nonNull)
            .filter(auth -> verificationBodyId.equals(auth.getVerificationBodyId()))
            .flatMap(pmrvAuthority -> pmrvAuthority.getPermissions().stream())
            .collect(Collectors.toList()).contains(permission);
    }

    @Override
    public RoleType getRoleType() {
        return RoleType.VERIFIER;
    }
}
