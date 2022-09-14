package uk.gov.pmrv.api.authorization.rules.services.resource;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.authorization.core.repository.AuthorityRepository;
import uk.gov.pmrv.api.authorization.rules.domain.ResourceType;
import uk.gov.pmrv.api.authorization.rules.domain.Scope;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class VerifierAuthorityResourceService {

    private final AuthorityRepository authorityRepository;

    public Map<Long, Set<String>> findUserScopedRequestTaskTypes(String userId) {
        return authorityRepository
            .findResourceSubTypesVerifierUserHasScope(userId, ResourceType.REQUEST_TASK, Scope.REQUEST_TASK_VIEW);
    }
    
    public List<String> findUsersWithScopeOnResourceTypeAndSubTypeAndVerificationBodyId(
            ResourceType resourceType, String resourceSubType, Scope scope, Long verificationBodyId){
        return authorityRepository.findVerifierUsersWithScopeOnResourceTypeAndResourceSubTypeAndVerificationBodyId(
                resourceType, resourceSubType, scope, verificationBodyId);
    }
    
    public List<String> findUsersByVerificationBodyId(Long verificationBodyId){
        return authorityRepository.findVerifierUsersByVerificationBodyId(verificationBodyId);
    }
}
