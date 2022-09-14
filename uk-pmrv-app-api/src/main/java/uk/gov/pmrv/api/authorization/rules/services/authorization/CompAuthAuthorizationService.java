package uk.gov.pmrv.api.authorization.rules.services.authorization;

import uk.gov.pmrv.api.authorization.core.domain.Permission;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;

public interface CompAuthAuthorizationService {
    boolean isAuthorized(PmrvUser user, CompetentAuthority competentAuthority);
    boolean isAuthorized(PmrvUser user, CompetentAuthority competentAuthority, Permission permission);
    RoleType getRoleType();
}
