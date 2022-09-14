package uk.gov.pmrv.api.authorization.rules.services.resource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
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
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;

@ExtendWith(MockitoExtension.class)
class RequestTaskAuthorizationResourceServiceTest {

    @InjectMocks
    private RequestTaskAuthorizationResourceService service;
    
    @Mock
    private OperatorAuthorityResourceService operatorAuthorityResourceService;
    
    @Mock
    private RegulatorAuthorityResourceService regulatorAuthorityResourceService;
    
    @Mock
    private VerifierAuthorityResourceService verifierAuthorityResourceService;
    
    @Mock
    private ResourceScopePermissionService resourceScopePermissionService;
    
    @Mock
    private PmrvAuthorizationService pmrvAuthorizationService;
    
    @Test
    void hasUserExecuteScopeOnRequestTaskType() {
    	RoleType roleType = RoleType.OPERATOR;
    	PmrvUser authUser = PmrvUser.builder().roleType(roleType).build();
        String requestTaskType = RequestTaskType.INSTALLATION_ACCOUNT_OPENING_ARCHIVE.name();
        Long accountId = 1L;
        CompetentAuthority competentAuthority = CompetentAuthority.ENGLAND;
        Scope scope = Scope.REQUEST_TASK_EXECUTE;
        
        ResourceScopePermission resourceScopePermission = 
                ResourceScopePermission.builder().permission(Permission.PERM_INSTALLATION_ACCOUNT_OPENING_ARCHIVE_EXECUTE_TASK).build();
        
        when(resourceScopePermissionService.findByResourceTypeAndResourceSubTypeAndRoleTypeAndScope(ResourceType.REQUEST_TASK, requestTaskType, roleType, scope))
            .thenReturn(Optional.of(resourceScopePermission));
        
        ResourceCriteria resourceCriteria = 
                ResourceCriteria.builder().accountId(accountId).competentAuthority(competentAuthority).build();
        boolean result = service.hasUserExecuteScopeOnRequestTaskType(authUser, requestTaskType, resourceCriteria);
        
        assertThat(result).isTrue();
        ArgumentCaptor<AuthorizationCriteria> criteriaCaptor = ArgumentCaptor.forClass(AuthorizationCriteria.class);
        verify(resourceScopePermissionService, times(1)).findByResourceTypeAndResourceSubTypeAndRoleTypeAndScope(
                ResourceType.REQUEST_TASK, requestTaskType, roleType, scope);
        verify(pmrvAuthorizationService, times(1)).authorize(Mockito.eq(authUser), criteriaCaptor.capture());
        AuthorizationCriteria criteriaCaptured = criteriaCaptor.getValue();
        assertThat(criteriaCaptured.getAccountId()).isEqualTo(accountId);
        assertThat(criteriaCaptured.getCompetentAuthority()).isEqualTo(competentAuthority);
        assertThat(criteriaCaptured.getPermission()).isEqualTo(resourceScopePermission.getPermission());
    }
    
    @Test
    void hasUserExecuteScopeOnRequestTaskType_not_authorized() {
    	RoleType roleType = RoleType.OPERATOR;
    	PmrvUser authUser = PmrvUser.builder().roleType(roleType).build();
        String requestTaskType = RequestTaskType.INSTALLATION_ACCOUNT_OPENING_ARCHIVE.name();
        Long accountId = 1L;
        CompetentAuthority competentAuthority = CompetentAuthority.ENGLAND;
        Scope scope = Scope.REQUEST_TASK_EXECUTE;
        
        ResourceScopePermission resourceScopePermission = 
                ResourceScopePermission.builder().permission(Permission.PERM_INSTALLATION_ACCOUNT_OPENING_ARCHIVE_EXECUTE_TASK).build();
        
        AuthorizationCriteria authCriteria = 
                AuthorizationCriteria.builder()
                    .accountId(accountId)
                    .competentAuthority(competentAuthority)
                    .permission(Permission.PERM_INSTALLATION_ACCOUNT_OPENING_ARCHIVE_EXECUTE_TASK).build();
        
        when(resourceScopePermissionService.findByResourceTypeAndResourceSubTypeAndRoleTypeAndScope(ResourceType.REQUEST_TASK, requestTaskType, roleType, scope))
            .thenReturn(Optional.of(resourceScopePermission));
        
        doThrow(BusinessException.class).when(pmrvAuthorizationService).authorize(authUser, authCriteria);
        
        ResourceCriteria resourceCriteria = 
                ResourceCriteria.builder().accountId(accountId).competentAuthority(competentAuthority).build();
        boolean result = service.hasUserExecuteScopeOnRequestTaskType(authUser, requestTaskType, resourceCriteria);
        
        assertThat(result).isFalse();
        ArgumentCaptor<AuthorizationCriteria> criteriaCaptor = ArgumentCaptor.forClass(AuthorizationCriteria.class);
        verify(resourceScopePermissionService, times(1)).findByResourceTypeAndResourceSubTypeAndRoleTypeAndScope(
                ResourceType.REQUEST_TASK, requestTaskType, roleType, scope);
        verify(pmrvAuthorizationService, times(1)).authorize(Mockito.eq(authUser), criteriaCaptor.capture());
        AuthorizationCriteria criteriaCaptured = criteriaCaptor.getValue();
        assertThat(criteriaCaptured.getAccountId()).isEqualTo(accountId);
        assertThat(criteriaCaptured.getCompetentAuthority()).isEqualTo(competentAuthority);
        assertThat(criteriaCaptured.getPermission()).isEqualTo(resourceScopePermission.getPermission());
    }
    
    @Test
    void hasUserAssignScopeOnRequestTasks() {
    	RoleType roleType = RoleType.OPERATOR;
    	PmrvUser authUser = PmrvUser.builder().roleType(roleType).build();
        Long accountId = 1L;
        CompetentAuthority competentAuthority = CompetentAuthority.ENGLAND;
        Scope scope = Scope.REQUEST_TASK_ASSIGN;
        
        ResourceScopePermission resourceScopePermission = 
                ResourceScopePermission.builder().permission(Permission.PERM_TASK_ASSIGNMENT).build();
        
        when(resourceScopePermissionService.findByResourceTypeAndRoleTypeAndScope(ResourceType.REQUEST_TASK, roleType, scope))
            .thenReturn(Optional.of(resourceScopePermission));
        
        ResourceCriteria resourceCriteria = 
                ResourceCriteria.builder().accountId(accountId).competentAuthority(competentAuthority).build();
        boolean result = service.hasUserAssignScopeOnRequestTasks(authUser, resourceCriteria);
        
        assertThat(result).isTrue();
        ArgumentCaptor<AuthorizationCriteria> criteriaCaptor = ArgumentCaptor.forClass(AuthorizationCriteria.class);
        verify(resourceScopePermissionService, times(1)).findByResourceTypeAndRoleTypeAndScope(
                ResourceType.REQUEST_TASK, roleType, scope);
        verify(pmrvAuthorizationService, times(1)).authorize(Mockito.eq(authUser), criteriaCaptor.capture());
        AuthorizationCriteria criteriaCaptured = criteriaCaptor.getValue();
        assertThat(criteriaCaptured.getAccountId()).isEqualTo(accountId);
        assertThat(criteriaCaptured.getCompetentAuthority()).isEqualTo(competentAuthority);
        assertThat(criteriaCaptured.getPermission()).isEqualTo(resourceScopePermission.getPermission());
    }
    
    @Test
    void hasUserAssignScopeOnRequestTasks_not_authorized() {
    	RoleType roleType = RoleType.OPERATOR;
    	PmrvUser authUser = PmrvUser.builder().roleType(roleType).build();
        Long accountId = 1L;
        CompetentAuthority competentAuthority = CompetentAuthority.ENGLAND;
        Scope scope = Scope.REQUEST_TASK_ASSIGN;
        
        ResourceScopePermission resourceScopePermission = 
                ResourceScopePermission.builder().permission(Permission.PERM_TASK_ASSIGNMENT).build();
        
        AuthorizationCriteria authCriteria = 
                AuthorizationCriteria.builder()
                    .accountId(accountId)
                    .competentAuthority(competentAuthority)
                    .permission(Permission.PERM_TASK_ASSIGNMENT).build();
        
        when(resourceScopePermissionService.findByResourceTypeAndRoleTypeAndScope(ResourceType.REQUEST_TASK, roleType, scope))
            .thenReturn(Optional.of(resourceScopePermission));
        
        doThrow(BusinessException.class).when(pmrvAuthorizationService).authorize(authUser, authCriteria);
        
        ResourceCriteria resourceCriteria = 
                ResourceCriteria.builder().accountId(accountId).competentAuthority(competentAuthority).build();
        boolean result = service.hasUserAssignScopeOnRequestTasks(authUser, resourceCriteria);
        
        assertThat(result).isFalse();
        ArgumentCaptor<AuthorizationCriteria> criteriaCaptor = ArgumentCaptor.forClass(AuthorizationCriteria.class);
        verify(resourceScopePermissionService, times(1)).findByResourceTypeAndRoleTypeAndScope(
                ResourceType.REQUEST_TASK, roleType, scope);
        verify(pmrvAuthorizationService, times(1)).authorize(Mockito.eq(authUser), criteriaCaptor.capture());
        AuthorizationCriteria criteriaCaptured = criteriaCaptor.getValue();
        assertThat(criteriaCaptured.getAccountId()).isEqualTo(accountId);
        assertThat(criteriaCaptured.getCompetentAuthority()).isEqualTo(competentAuthority);
        assertThat(criteriaCaptured.getPermission()).isEqualTo(resourceScopePermission.getPermission());
    }
    
    @Test
    void findUsersWhoCanExecuteRequestTaskTypeByAccountCriteriaAndRoleType_operator_requires_permission() {
        String requestTaskType = RequestTaskType.INSTALLATION_ACCOUNT_OPENING_ARCHIVE.name();
        Long accountId = 1L;
        CompetentAuthority competentAuthority = CompetentAuthority.ENGLAND;
        RoleType roleType = RoleType.OPERATOR;
        Scope scope = Scope.REQUEST_TASK_EXECUTE;
        
        when(resourceScopePermissionService.existsByResourceTypeAndResourceSubTypeAndRoleTypeAndScope(
                ResourceType.REQUEST_TASK, requestTaskType, roleType, scope))
            .thenReturn(true);
        
        List<String> users = List.of("user");
        when(operatorAuthorityResourceService.findUsersWithScopeOnResourceTypeAndSubTypeAndAccountId(
                ResourceType.REQUEST_TASK, requestTaskType, scope, accountId))
            .thenReturn(users);
        
        ResourceCriteria resourceCriteria = 
                ResourceCriteria.builder().accountId(accountId).competentAuthority(competentAuthority).build();
        List<String> usersFound = 
                service.findUsersWhoCanExecuteRequestTaskTypeByAccountCriteriaAndRoleType(requestTaskType, resourceCriteria, roleType);
        
        assertThat(usersFound).containsAll(users);
        verify(resourceScopePermissionService, times(1)).existsByResourceTypeAndResourceSubTypeAndRoleTypeAndScope(
                ResourceType.REQUEST_TASK, requestTaskType, roleType, scope);
        verify(operatorAuthorityResourceService).findUsersWithScopeOnResourceTypeAndSubTypeAndAccountId(
                ResourceType.REQUEST_TASK, requestTaskType, scope, accountId);
    }
    
    @Test
    void findUsersWhoCanExecuteRequestTaskTypeByAccountCriteriaAndRoleType_operator_requires_no_permission() {
        String requestTaskType = RequestTaskType.INSTALLATION_ACCOUNT_OPENING_ARCHIVE.name();
        Long accountId = 1L;
        CompetentAuthority competentAuthority = CompetentAuthority.ENGLAND;
        RoleType roleType = RoleType.OPERATOR;
        Scope scope = Scope.REQUEST_TASK_EXECUTE;
        
        when(resourceScopePermissionService.existsByResourceTypeAndResourceSubTypeAndRoleTypeAndScope(
                ResourceType.REQUEST_TASK, requestTaskType, roleType, scope))
            .thenReturn(false);
        
        List<String> users = List.of("user");
        when(operatorAuthorityResourceService.findUsersByAccountId(accountId))
            .thenReturn(users);
        
        ResourceCriteria resourceCriteria = 
                ResourceCriteria.builder().accountId(accountId).competentAuthority(competentAuthority).build();
        List<String> usersFound = 
                service.findUsersWhoCanExecuteRequestTaskTypeByAccountCriteriaAndRoleType(requestTaskType, resourceCriteria, roleType);
        
        assertThat(usersFound).containsAll(users);
        verify(resourceScopePermissionService, times(1)).existsByResourceTypeAndResourceSubTypeAndRoleTypeAndScope(
                ResourceType.REQUEST_TASK, requestTaskType, roleType, scope);
        verify(operatorAuthorityResourceService).findUsersByAccountId(accountId);
    }
    
    @Test
    void findUsersWhoCanExecuteRequestTaskTypeByAccountCriteriaAndRoleType_regulator_requires_permission() {
        String requestTaskType = RequestTaskType.INSTALLATION_ACCOUNT_OPENING_ARCHIVE.name();
        Long accountId = 1L;
        CompetentAuthority competentAuthority = CompetentAuthority.ENGLAND;
        RoleType roleType = RoleType.REGULATOR;
        Scope scope = Scope.REQUEST_TASK_EXECUTE;
        
        when(resourceScopePermissionService.existsByResourceTypeAndResourceSubTypeAndRoleTypeAndScope(
                ResourceType.REQUEST_TASK, requestTaskType, roleType, scope))
            .thenReturn(true);
        
        List<String> users = List.of("user");
        when(regulatorAuthorityResourceService.findUsersWithScopeOnResourceTypeAndSubTypeAndCA(
                ResourceType.REQUEST_TASK, requestTaskType, scope, competentAuthority))
            .thenReturn(users);
        
        ResourceCriteria resourceCriteria = 
                ResourceCriteria.builder().accountId(accountId).competentAuthority(competentAuthority).build();
        List<String> usersFound = 
                service.findUsersWhoCanExecuteRequestTaskTypeByAccountCriteriaAndRoleType(requestTaskType, resourceCriteria, roleType);
        
        assertThat(usersFound).containsAll(users);
        verify(resourceScopePermissionService, times(1)).existsByResourceTypeAndResourceSubTypeAndRoleTypeAndScope(
                ResourceType.REQUEST_TASK, requestTaskType, roleType, scope);
        verify(regulatorAuthorityResourceService).findUsersWithScopeOnResourceTypeAndSubTypeAndCA(
                ResourceType.REQUEST_TASK, requestTaskType, scope, competentAuthority);
    }
    
    @Test
    void findUsersWhoCanExecuteRequestTaskTypeByAccountCriteriaAndRoleType_regulator_requires_no_permission() {
        String requestTaskType = RequestTaskType.INSTALLATION_ACCOUNT_OPENING_ARCHIVE.name();
        Long accountId = 1L;
        CompetentAuthority competentAuthority = CompetentAuthority.ENGLAND;
        RoleType roleType = RoleType.REGULATOR;
        Scope scope = Scope.REQUEST_TASK_EXECUTE;
        
        when(resourceScopePermissionService.existsByResourceTypeAndResourceSubTypeAndRoleTypeAndScope(
                ResourceType.REQUEST_TASK, requestTaskType, roleType, scope))
            .thenReturn(false);
        
        List<String> users = List.of("user");
        when(regulatorAuthorityResourceService.findUsersByCompetentAuthority(competentAuthority))
            .thenReturn(users);
        
        ResourceCriteria resourceCriteria = 
                ResourceCriteria.builder().accountId(accountId).competentAuthority(competentAuthority).build();
        List<String> usersFound = 
                service.findUsersWhoCanExecuteRequestTaskTypeByAccountCriteriaAndRoleType(requestTaskType, resourceCriteria, roleType);
        
        assertThat(usersFound).containsAll(users);
        verify(resourceScopePermissionService, times(1)).existsByResourceTypeAndResourceSubTypeAndRoleTypeAndScope(
                ResourceType.REQUEST_TASK, requestTaskType, roleType, scope);
        verify(regulatorAuthorityResourceService).findUsersByCompetentAuthority(competentAuthority);
    }
    
    @Test
    void findUsersWhoCanExecuteRequestTaskTypeByAccountCriteriaAndRoleType_verifier_requires_permission() {
        String requestTaskType = RequestTaskType.INSTALLATION_ACCOUNT_OPENING_ARCHIVE.name();
        Long verificationBodyId = 1L;
        RoleType roleType = RoleType.VERIFIER;
        Scope scope = Scope.REQUEST_TASK_EXECUTE;
        
        when(resourceScopePermissionService.existsByResourceTypeAndResourceSubTypeAndRoleTypeAndScope(
                ResourceType.REQUEST_TASK, requestTaskType, roleType, scope))
            .thenReturn(true);
        
        List<String> users = List.of("user");
        when(verifierAuthorityResourceService.findUsersWithScopeOnResourceTypeAndSubTypeAndVerificationBodyId(
                ResourceType.REQUEST_TASK, requestTaskType, scope, verificationBodyId))
            .thenReturn(users);
        
        ResourceCriteria resourceCriteria = 
                ResourceCriteria.builder().verificationBodyId(verificationBodyId).build();
        List<String> usersFound = 
                service.findUsersWhoCanExecuteRequestTaskTypeByAccountCriteriaAndRoleType(requestTaskType, resourceCriteria, roleType);
        
        assertThat(usersFound).containsAll(users);
        verify(resourceScopePermissionService, times(1)).existsByResourceTypeAndResourceSubTypeAndRoleTypeAndScope(
                ResourceType.REQUEST_TASK, requestTaskType, roleType, scope);
        verify(verifierAuthorityResourceService).findUsersWithScopeOnResourceTypeAndSubTypeAndVerificationBodyId(
                ResourceType.REQUEST_TASK, requestTaskType, scope, verificationBodyId);
    }
    
    @Test
    void findUsersWhoCanExecuteRequestTaskTypeByAccountCriteriaAndRoleType_verifier_requires_no_permission() {
        String requestTaskType = RequestTaskType.INSTALLATION_ACCOUNT_OPENING_ARCHIVE.name();
        Long verificationBodyId = 1L;
        RoleType roleType = RoleType.VERIFIER;
        Scope scope = Scope.REQUEST_TASK_EXECUTE;
        
        when(resourceScopePermissionService.existsByResourceTypeAndResourceSubTypeAndRoleTypeAndScope(
                ResourceType.REQUEST_TASK, requestTaskType, roleType, scope))
            .thenReturn(false);
        
        List<String> users = List.of("user");
        when(verifierAuthorityResourceService.findUsersByVerificationBodyId(verificationBodyId))
            .thenReturn(users);
        
        ResourceCriteria resourceCriteria = 
                ResourceCriteria.builder().verificationBodyId(verificationBodyId).build();
        List<String> usersFound = 
                service.findUsersWhoCanExecuteRequestTaskTypeByAccountCriteriaAndRoleType(requestTaskType, resourceCriteria, roleType);
        
        assertThat(usersFound).containsAll(users);
        verify(resourceScopePermissionService, times(1)).existsByResourceTypeAndResourceSubTypeAndRoleTypeAndScope(
                ResourceType.REQUEST_TASK, requestTaskType, roleType, scope);
        verify(verifierAuthorityResourceService).findUsersByVerificationBodyId(verificationBodyId);
    }
        
}
