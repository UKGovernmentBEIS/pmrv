package uk.gov.pmrv.api.authorization.rules.services.resource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.authorization.core.domain.Permission;
import uk.gov.pmrv.api.authorization.rules.domain.ResourceScopePermission;
import uk.gov.pmrv.api.authorization.rules.domain.ResourceType;
import uk.gov.pmrv.api.authorization.rules.domain.Scope;
import uk.gov.pmrv.api.authorization.rules.services.ResourceScopePermissionService;
import uk.gov.pmrv.api.authorization.rules.services.authorization.AuthorizationCriteria;
import uk.gov.pmrv.api.authorization.rules.services.authorization.PmrvAuthorizationService;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;

@ExtendWith(MockitoExtension.class)
class AccountAuthorizationResourceServiceTest {

    @InjectMocks
    private AccountAuthorizationResourceService service;
    
    @Mock
    private ResourceScopePermissionService resourceScopePermissionService;
    
    @Mock
    private PmrvAuthorizationService pmrvAuthorizationService;
    
    @Test
    void hasUserScopeToAccount() {
    	RoleType roleType = RoleType.OPERATOR;
        PmrvUser authUser = PmrvUser.builder().roleType(roleType).build();
        Long accountId = 1L;
        Scope scope = Scope.EDIT_USER;
        
        ResourceScopePermission resourceScopePermission = 
                ResourceScopePermission.builder().permission(Permission.PERM_ACCOUNT_USERS_EDIT).build();
        
        when(resourceScopePermissionService.findByResourceTypeAndRoleTypeAndScope(ResourceType.ACCOUNT, roleType, scope))
            .thenReturn(Optional.of(resourceScopePermission));
        
        boolean result = service.hasUserScopeToAccount(authUser, accountId, scope);
        
        assertThat(result).isTrue();
        ArgumentCaptor<AuthorizationCriteria> criteriaCaptor = ArgumentCaptor.forClass(AuthorizationCriteria.class);
        verify(resourceScopePermissionService, times(1)).findByResourceTypeAndRoleTypeAndScope(ResourceType.ACCOUNT, roleType, scope);
        verify(pmrvAuthorizationService, times(1)).authorize(Mockito.eq(authUser), criteriaCaptor.capture());
        AuthorizationCriteria criteriaCaptured = criteriaCaptor.getValue();
        assertThat(criteriaCaptured.getAccountId()).isEqualTo(accountId);
        assertThat(criteriaCaptured.getPermission()).isEqualTo(resourceScopePermission.getPermission());
    }
    
    @Test
    void hasUserScopeToAccount_not_authorized() {
    	RoleType roleType = RoleType.OPERATOR;
    	PmrvUser authUser = PmrvUser.builder().roleType(roleType).build();
        Long accountId = 1L;
        Scope scope = Scope.EDIT_USER;
        
        ResourceScopePermission resourceScopePermission = 
                ResourceScopePermission.builder().permission(Permission.PERM_ACCOUNT_USERS_EDIT).build();
        
        AuthorizationCriteria authCriteria = 
                AuthorizationCriteria.builder()
                    .accountId(accountId)
                    .permission(Permission.PERM_ACCOUNT_USERS_EDIT).build();
        
        when(resourceScopePermissionService.findByResourceTypeAndRoleTypeAndScope(ResourceType.ACCOUNT, roleType, scope))
            .thenReturn(Optional.of(resourceScopePermission));
        
        doThrow(BusinessException.class).when(pmrvAuthorizationService).authorize(authUser, authCriteria);
        
        boolean result = service.hasUserScopeToAccount(authUser, accountId, scope);
        
        assertThat(result).isFalse();
        verify(resourceScopePermissionService, times(1)).findByResourceTypeAndRoleTypeAndScope(ResourceType.ACCOUNT, roleType, scope);
        ArgumentCaptor<AuthorizationCriteria> criteriaCaptor = ArgumentCaptor.forClass(AuthorizationCriteria.class);
        verify(pmrvAuthorizationService, times(1)).authorize(Mockito.eq(authUser), criteriaCaptor.capture());
        AuthorizationCriteria criteriaCaptured = criteriaCaptor.getValue();
        assertThat(criteriaCaptured.getAccountId()).isEqualTo(accountId);
        assertThat(criteriaCaptured.getPermission()).isEqualTo(resourceScopePermission.getPermission());
    }
}
