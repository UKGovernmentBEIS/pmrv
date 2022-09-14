package uk.gov.pmrv.api.authorization.rules.services.resource;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.authorization.core.domain.Permission;
import uk.gov.pmrv.api.authorization.rules.domain.ResourceScopePermission;
import uk.gov.pmrv.api.authorization.rules.domain.ResourceType;
import uk.gov.pmrv.api.authorization.rules.domain.Scope;
import uk.gov.pmrv.api.authorization.rules.services.ResourceScopePermissionService;
import uk.gov.pmrv.api.authorization.rules.services.authorization.AuthorizationCriteria;
import uk.gov.pmrv.api.authorization.rules.services.authorization.PmrvAuthorizationService;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;

@Service
@RequiredArgsConstructor
public class AccountAuthorizationResourceService {
    
    private final ResourceScopePermissionService resourceScopePermissionService;
    private final PmrvAuthorizationService pmrvAuthorizationService;

    public boolean hasUserScopeToAccount(PmrvUser authUser, Long accountId, Scope scope) {
        Permission requiredPermission = 
                resourceScopePermissionService.findByResourceTypeAndRoleTypeAndScope(ResourceType.ACCOUNT, authUser.getRoleType(), scope)
                .map(ResourceScopePermission::getPermission)
                .orElse(null);
        
        AuthorizationCriteria authCriteria = 
                AuthorizationCriteria.builder()
                    .accountId(accountId)
                    .permission(requiredPermission).build();
        try {
            pmrvAuthorizationService.authorize(authUser, authCriteria);
        } catch (BusinessException e) {
            return false;
        }
        
        return true;
    }
}
