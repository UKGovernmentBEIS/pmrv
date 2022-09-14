package uk.gov.pmrv.api.authorization.rules.services.authorization;

import uk.gov.pmrv.api.authorization.core.domain.Permission;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;

public interface AccountAuthorizationService {
    boolean isAuthorized(PmrvUser user, Long accountId);
    boolean isAuthorized(PmrvUser user, Long accountId, Permission permission);
    RoleType getRoleType();
}
