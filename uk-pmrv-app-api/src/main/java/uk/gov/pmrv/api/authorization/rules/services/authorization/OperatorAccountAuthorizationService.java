package uk.gov.pmrv.api.authorization.rules.services.authorization;

import org.springframework.stereotype.Service;

import uk.gov.pmrv.api.authorization.core.domain.Permission;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;

import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Service that checks if an OPERATOR user is authorized on an account
 */
@Service
public class OperatorAccountAuthorizationService implements AccountAuthorizationService {
    /**
     * checks that OPERATOR has access to account
     * @param user the user to authorize.
     * @param accountId the account to check permission on.
     * @return if the OPERATOR is authorized on account.
     */
    @Override
    public boolean isAuthorized(PmrvUser user, Long accountId) {
        return user.getAuthorities()
                .stream()
                .filter(Objects::nonNull)
                .anyMatch(auth -> accountId.equals(auth.getAccountId()));
    }

    /**
     * checks that OPERATOR has the permissions to account
     * @param user the user to authorize.
     * @param accountId the account to check permission on.
     * @param permission the {@link Permission} to check
     * @return if the OPERATOR has the permissions on the account
     */
    @Override
    public boolean isAuthorized(PmrvUser user, Long accountId, Permission permission) {
        return user.getAuthorities()
                .stream()
                .filter(Objects::nonNull)
                .filter(auth -> accountId.equals(auth.getAccountId()))
                .flatMap(pmrvAuthority -> pmrvAuthority.getPermissions().stream())
                .collect(Collectors.toList()).contains(permission);
    }

    @Override
    public RoleType getRoleType() {
        return RoleType.OPERATOR;
    }
}
