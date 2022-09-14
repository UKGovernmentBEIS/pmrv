package uk.gov.pmrv.api.authorization.rules.services.authorization;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.authorization.rules.domain.ResourceType;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;

/**
 * Service that delegates authorization to {@link ResourceType} based services.
 */
@Service
@RequiredArgsConstructor
public class PmrvResourceAuthorizationServiceDelegator {

    private final List<PmrvResourceAuthorizationService> pmrvResourceAuthorizationServices;

    /**
     * Checks that user has authorization to the specified resource type.
     * @param resourceType the {@link ResourceType}
     * @param user the user
     * @param criteria {@link AuthorizationCriteria}
     * @return if the user is authorized on the resource type
     */
    public boolean isAuthorized(ResourceType resourceType, PmrvUser user, AuthorizationCriteria criteria) {
        return getResourceService(resourceType)
            .map(resourceAuthorizationService -> resourceAuthorizationService.isAuthorized(user, criteria))
            .orElse(false);
    }

    private Optional<PmrvResourceAuthorizationService> getResourceService(ResourceType resourceType) {
        return pmrvResourceAuthorizationServices.stream()
            .filter(resourceAuthorizationService -> resourceAuthorizationService.getResourceType().equals(resourceType))
            .findAny();
    }
}
