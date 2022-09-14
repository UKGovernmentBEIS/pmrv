package uk.gov.pmrv.api.authorization.rules.services.authorization;

import uk.gov.pmrv.api.authorization.core.domain.Permission;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;

public interface VerificationBodyAuthorizationService {
    boolean isAuthorized(PmrvUser user, Long verificationBodyId);
    boolean isAuthorized(PmrvUser user, Long verificationBodyId, Permission permission);
    RoleType getRoleType();
}
