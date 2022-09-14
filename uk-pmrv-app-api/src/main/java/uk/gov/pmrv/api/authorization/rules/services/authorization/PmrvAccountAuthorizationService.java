package uk.gov.pmrv.api.authorization.rules.services.authorization;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.authorization.rules.domain.ResourceType;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;

@Service
@RequiredArgsConstructor
public class PmrvAccountAuthorizationService implements PmrvResourceAuthorizationService {

    private final AccountAuthorizationServiceDelegator accountAuthorizationService;

    @Override
    public boolean isAuthorized(PmrvUser user, AuthorizationCriteria criteria) {
        final boolean isAuthorized;
        if (criteria.getPermission() == null) {
            isAuthorized = accountAuthorizationService.isAuthorized(user, criteria.getAccountId());
        } else {
            isAuthorized = accountAuthorizationService.isAuthorized(user, criteria.getAccountId(), criteria.getPermission());
        }

        return isAuthorized;
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.ACCOUNT;
    }
}
