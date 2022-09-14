package uk.gov.pmrv.api.authorization.rules.repository;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.transaction.annotation.Transactional;

import uk.gov.pmrv.api.authorization.rules.domain.ResourceType;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;

public interface AuthorizationRuleCustomRepository {

    @Transactional(readOnly = true)
    Map<String, Set<RoleType>> findResourceSubTypesRoleTypes();
    
    @Transactional(readOnly = true)
    Optional<RoleType> findRoleTypeByResourceTypeAndSubType(ResourceType resourceType, String resourceSubType);
    
    @Transactional(readOnly = true)
    Set<String> findResourceSubTypesByResourceTypeAndRoleType(ResourceType resourceType, RoleType roleType);
}
