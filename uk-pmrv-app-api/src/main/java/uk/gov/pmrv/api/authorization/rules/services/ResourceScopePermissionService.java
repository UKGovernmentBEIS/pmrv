package uk.gov.pmrv.api.authorization.rules.services;


import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.authorization.rules.domain.ResourceScopePermission;
import uk.gov.pmrv.api.authorization.rules.domain.ResourceType;
import uk.gov.pmrv.api.authorization.rules.domain.Scope;
import uk.gov.pmrv.api.authorization.rules.repository.ResourceScopePermissionRepository;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;

@Service
@RequiredArgsConstructor
public class ResourceScopePermissionService {

    private final ResourceScopePermissionRepository resourceScopePermissionRepository;
    
    public Optional<ResourceScopePermission> findByResourceTypeAndResourceSubTypeAndRoleTypeAndScope(
            ResourceType resourceType, String resourceSubType, RoleType roleType, Scope scope){
        return resourceScopePermissionRepository.findByResourceTypeAndResourceSubTypeAndRoleTypeAndScope(resourceType, resourceSubType, roleType, scope);
    }
    
    public Optional<ResourceScopePermission> findByResourceTypeAndRoleTypeAndScope(ResourceType resourceType, RoleType roleType, Scope scope){
        return resourceScopePermissionRepository.findByResourceTypeAndRoleTypeAndScope(resourceType, roleType, scope);
    }
    
    public boolean existsByResourceTypeAndResourceSubTypeAndRoleTypeAndScope(ResourceType resourceType, String resourceSubType, RoleType roleType, Scope scope) {
        return resourceScopePermissionRepository.existsByResourceTypeAndResourceSubTypeAndRoleTypeAndScope(resourceType, resourceSubType, roleType, scope);
    }
    
}
