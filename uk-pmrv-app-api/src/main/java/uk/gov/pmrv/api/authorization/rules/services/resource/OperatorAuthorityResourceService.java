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
public class OperatorAuthorityResourceService {

    private final AuthorityRepository authorityRepository;

    /**
     * Find operator users by scope and resource type and resource sub type
     * @param resourceType
     * @param resourceSubType
     * @param scope
     * @param accountId
     * @return
     */
    public List<String> findUsersWithScopeOnResourceTypeAndSubTypeAndAccountId(
            ResourceType resourceType, String resourceSubType, Scope scope, Long accountId){
        return authorityRepository.findOperatorUsersWithScopeOnResourceTypeAndResourceSubTypeAndAccountId(
                resourceType, resourceSubType, scope, accountId);
    }
    
    /**
     * Find operator users by account id
     * @param accountId
     * @return
     */
    public List<String> findUsersByAccountId(Long accountId){
        return authorityRepository.findOperatorUsersByAccountId(accountId);
    }

    public Map<Long, Set<String>> findUserScopedRequestTaskTypesByAccounts(String userId, Set<Long> accounts){
        return authorityRepository
            .findResourceSubTypesOperatorUserHasScopeByAccounts(userId, accounts, ResourceType.REQUEST_TASK, Scope.REQUEST_TASK_VIEW);
    }
}
