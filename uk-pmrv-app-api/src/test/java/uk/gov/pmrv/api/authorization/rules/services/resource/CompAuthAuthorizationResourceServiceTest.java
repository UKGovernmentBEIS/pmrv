package uk.gov.pmrv.api.authorization.rules.services.resource;

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
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompAuthAuthorizationResourceServiceTest {

    @InjectMocks
    private CompAuthAuthorizationResourceService service;
    
    @Mock
    private ResourceScopePermissionService resourceScopePermissionService;
    
    @Mock
    private PmrvAuthorizationService pmrvAuthorizationService;
    
    @Test
    void hasUserScopeToCompAuth() {
    	RoleType roleType = RoleType.OPERATOR;
    	PmrvUser authUser = PmrvUser.builder().roleType(roleType).build();
        CompetentAuthority compAuth = CompetentAuthority.ENGLAND;
        Scope scope = Scope.EDIT_USER;
        
        ResourceScopePermission resourceScopePermission = 
                ResourceScopePermission.builder().permission(Permission.PERM_CA_USERS_EDIT).build();
        
        when(resourceScopePermissionService.findByResourceTypeAndRoleTypeAndScope(ResourceType.CA, roleType, scope))
            .thenReturn(Optional.of(resourceScopePermission));
        
        boolean result = service.hasUserScopeToCompAuth(authUser, compAuth, scope);
        
        assertThat(result).isTrue();
        ArgumentCaptor<AuthorizationCriteria> criteriaCaptor = ArgumentCaptor.forClass(AuthorizationCriteria.class);
        verify(resourceScopePermissionService, times(1)).findByResourceTypeAndRoleTypeAndScope(ResourceType.CA, roleType, scope);
        verify(pmrvAuthorizationService, times(1)).authorize(Mockito.eq(authUser), criteriaCaptor.capture());
        AuthorizationCriteria criteriaCaptured = criteriaCaptor.getValue();
        assertThat(criteriaCaptured.getCompetentAuthority()).isEqualTo(compAuth);
        assertThat(criteriaCaptured.getPermission()).isEqualTo(resourceScopePermission.getPermission());
    }
    
    @Test
    void hasUserScopeToCompAuth_not_authorized() {
    	RoleType roleType = RoleType.OPERATOR;
    	PmrvUser authUser = PmrvUser.builder().roleType(roleType).build();
        CompetentAuthority compAuth = CompetentAuthority.ENGLAND;
        Scope scope = Scope.EDIT_USER;
        
        ResourceScopePermission resourceScopePermission = 
                ResourceScopePermission.builder().permission(Permission.PERM_CA_USERS_EDIT).build();
        
        AuthorizationCriteria authCriteria = 
                AuthorizationCriteria.builder()
                    .competentAuthority(compAuth)
                    .permission(Permission.PERM_CA_USERS_EDIT).build();
        
        when(resourceScopePermissionService.findByResourceTypeAndRoleTypeAndScope(ResourceType.CA, roleType, scope))
            .thenReturn(Optional.of(resourceScopePermission));
        
        doThrow(BusinessException.class).when(pmrvAuthorizationService).authorize(authUser, authCriteria);
        
        boolean result = service.hasUserScopeToCompAuth(authUser, compAuth, scope);
        
        assertThat(result).isFalse();
        verify(resourceScopePermissionService, times(1)).findByResourceTypeAndRoleTypeAndScope(ResourceType.CA, roleType, scope);
        ArgumentCaptor<AuthorizationCriteria> criteriaCaptor = ArgumentCaptor.forClass(AuthorizationCriteria.class);
        verify(pmrvAuthorizationService, times(1)).authorize(Mockito.eq(authUser), criteriaCaptor.capture());
        AuthorizationCriteria criteriaCaptured = criteriaCaptor.getValue();
        assertThat(criteriaCaptured.getCompetentAuthority()).isEqualTo(compAuth);
        assertThat(criteriaCaptured.getPermission()).isEqualTo(resourceScopePermission.getPermission());
    }
}
