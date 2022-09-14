package uk.gov.pmrv.api.authorization.rules.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.authorization.rules.domain.ResourceType;
import uk.gov.pmrv.api.authorization.rules.repository.AuthorizationRuleRepository;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthorizationRulesQueryService {

    private final AuthorizationRuleRepository authorizationRuleRepository;
    
    public Optional<RoleType> findRoleTypeByResourceTypeAndSubType(ResourceType resourceType, String resourceSubType){
        return authorizationRuleRepository
                .findRoleTypeByResourceTypeAndSubType(resourceType, resourceSubType);
    }
    
    public Map<String, Set<RoleType>> findResourceSubTypesRoleTypes(){
        return authorizationRuleRepository.findResourceSubTypesRoleTypes();
    }
    
    public Set<String> findResourceSubTypesByResourceTypeAndRoleType(ResourceType resourceType, RoleType roleType){
        return authorizationRuleRepository.findResourceSubTypesByResourceTypeAndRoleType(resourceType, roleType);
    }
}
