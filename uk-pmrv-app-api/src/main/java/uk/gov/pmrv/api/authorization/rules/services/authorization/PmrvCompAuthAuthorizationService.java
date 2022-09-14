package uk.gov.pmrv.api.authorization.rules.services.authorization;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.authorization.rules.domain.ResourceType;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;

@Service
@RequiredArgsConstructor
public class PmrvCompAuthAuthorizationService implements PmrvResourceAuthorizationService {

    private final CompAuthAuthorizationServiceDelegator compAuthAuthorizationService;

    @Override
    public boolean isAuthorized(PmrvUser user, AuthorizationCriteria criteria) {
        boolean isAuthorized;
        if (ObjectUtils.isEmpty(criteria.getPermission())) {
            isAuthorized = compAuthAuthorizationService.isAuthorized(user, criteria.getCompetentAuthority());
        } else {
            isAuthorized = compAuthAuthorizationService.isAuthorized(user, criteria.getCompetentAuthority(), criteria.getPermission());
        }

        return isAuthorized;
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.CA;
    }
}
