package uk.gov.pmrv.api.authorization.rules.services.authorization;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.authorization.core.domain.Permission;
import uk.gov.pmrv.api.authorization.rules.services.authorityinfo.providers.AccountAuthorityInfoProvider;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;

/**
 * Service that checks if a REGULATOR user is authorized on an account
 */
@Service
@RequiredArgsConstructor
public class RegulatorAccountAuthorizationService implements AccountAuthorizationService {
    private final AccountAuthorityInfoProvider accountAuthorityInfoProvider;
    private final RegulatorCompAuthAuthorizationService regulatorCompAuthAuthorizationService;

    /**
     * checks that REGULATOR has access to account
     * @param user the user to authorize.
     * @param accountId the account to check permission on.
     * @return if the REGULATOR is authorized on account.
     */
    @Override
    public boolean isAuthorized(PmrvUser user, Long accountId) {
        CompetentAuthority accountCompetentAuthority = accountAuthorityInfoProvider.getAccountCa(accountId);
        return regulatorCompAuthAuthorizationService.isAuthorized(user, accountCompetentAuthority);
    }

    /**
     * checks that REGULATOR has the permissions to account
     * @param user the user to authorize.
     * @param accountId the account to check permission on.
     * @param permission the {@link Permission} to check
     * @return if the REGULATOR has the permissions on the account
     */
    @Override
    public boolean isAuthorized(PmrvUser user, Long accountId, Permission permission) {
        CompetentAuthority accountCompetentAuthority = accountAuthorityInfoProvider.getAccountCa(accountId);
        return regulatorCompAuthAuthorizationService.isAuthorized(user, accountCompetentAuthority, permission);
    }

    @Override
    public RoleType getRoleType() {
        return RoleType.REGULATOR;
    }
}
