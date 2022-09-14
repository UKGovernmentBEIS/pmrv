package uk.gov.pmrv.api.authorization.core.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.pmrv.api.authorization.core.domain.Permission.PERM_ACCOUNT_USERS_EDIT;
import static uk.gov.pmrv.api.authorization.core.domain.Permission.PERM_INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW_VIEW_TASK;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.authorization.core.domain.Authority;
import uk.gov.pmrv.api.authorization.core.domain.AuthorityPermission;
import uk.gov.pmrv.api.authorization.core.domain.AuthorityStatus;
import uk.gov.pmrv.api.authorization.core.domain.Permission;
import uk.gov.pmrv.api.authorization.core.domain.Role;
import uk.gov.pmrv.api.authorization.core.domain.RolePermission;
import uk.gov.pmrv.api.authorization.core.repository.AuthorityRepository;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;

@ExtendWith(MockitoExtension.class)
class AuthorityAssignmentServiceTest {

    @InjectMocks
    private AuthorityAssignmentService authorityAssignmentService;

    @Mock
    private AuthorityRepository authorityRepository;

    @Mock
    private RoleService roleService;

    @Test
    void createAuthorityForRole() {
        Authority authority = Authority.builder().userId("user").status(AuthorityStatus.ACTIVE).build();

        List<AuthorityPermission> expectedAuthPermissions = List.of(
            AuthorityPermission.builder().authority(authority).permission(PERM_INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW_VIEW_TASK).build(),
            AuthorityPermission.builder().authority(authority).permission(PERM_ACCOUNT_USERS_EDIT).build()
        );

        Role role = Role.builder().code("code").build();
        expectedAuthPermissions.forEach(permission -> role.addPermission(
            RolePermission.builder().permission(permission.getPermission()).role(role).build()));

        authorityAssignmentService.createAuthorityPermissionsForRole(authority, role);

        ArgumentCaptor<Authority> authorityCaptor = ArgumentCaptor.forClass(Authority.class);
        verify(authorityRepository, times(1)).save(authorityCaptor.capture());
        Authority savedAuthority = authorityCaptor.getValue();

        assertThat(savedAuthority.getAuthorityPermissions()).containsExactlyInAnyOrderElementsOf(expectedAuthPermissions);
    }

    @Test
    void createAuthorityWithPermissions() {
        Authority authority = Authority.builder().userId("user").status(AuthorityStatus.ACTIVE).build();

        List<AuthorityPermission> expectedAuthPermissions = List.of(
            AuthorityPermission.builder().authority(authority).permission(PERM_INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW_VIEW_TASK).build(),
            AuthorityPermission.builder().authority(authority).permission(PERM_ACCOUNT_USERS_EDIT).build()
        );

        authorityAssignmentService.createAuthorityWithPermissions(authority,
            expectedAuthPermissions.stream().map(AuthorityPermission::getPermission).collect(Collectors.toList()));

        ArgumentCaptor<Authority> authorityCaptor = ArgumentCaptor.forClass(Authority.class);
        verify(authorityRepository, times(1)).save(authorityCaptor.capture());
        Authority savedAuthority = authorityCaptor.getValue();

        assertThat(savedAuthority.getAuthorityPermissions()).containsExactlyInAnyOrderElementsOf(expectedAuthPermissions);
    }

    @Test
    void updatePendingAuthority_different_role_code() {
        final String roleCode = "newRoleCode";
        final String authModificationUserId = "userId";
        Authority authority = Authority.builder().userId("user").status(AuthorityStatus.ACTIVE).code("roleCode").build();
        authority.setAuthorityPermissions(new ArrayList<>(List.of(
            AuthorityPermission.builder().authority(authority).permission(PERM_ACCOUNT_USERS_EDIT).build(),
            AuthorityPermission.builder().authority(authority).permission(PERM_INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW_VIEW_TASK).build()
        )));

        List<AuthorityPermission> expectedAuthPermissions = List.of(
            AuthorityPermission.builder().authority(authority).permission(PERM_INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW_VIEW_TASK).build()
        );

        Role role = Role.builder().code(roleCode).build();
        expectedAuthPermissions.forEach(permission -> role.addPermission(
            RolePermission.builder().permission(permission.getPermission()).role(role).build()));

        when(roleService.getRoleByRoleCode(roleCode)).thenReturn(role);

        authorityAssignmentService.updatePendingAuthority(authority, roleCode, authModificationUserId);

        ArgumentCaptor<Authority> authorityCaptor = ArgumentCaptor.forClass(Authority.class);
        verify(authorityRepository, times(1)).save(authorityCaptor.capture());
        Authority savedAuthority = authorityCaptor.getValue();

        assertThat(savedAuthority.getUuid()).isNotBlank();
        assertThat(savedAuthority.getAuthorityPermissions()).containsExactlyInAnyOrderElementsOf(expectedAuthPermissions);
        assertThat(savedAuthority.getUuid()).isNotNull();
        assertThat(savedAuthority.getCreatedBy()).isEqualTo(authModificationUserId);
    }

    @Test
    void updatePendingAuthority_same_role_code() {
        final String authModificationUserId = "userId";
        final String roleCode = "roleCode";
        Authority authority = Authority.builder().userId("user").status(AuthorityStatus.ACTIVE).code(roleCode).build();

        List<AuthorityPermission> expectedAuthPermissions = List.of(
            AuthorityPermission.builder().authority(authority).permission(PERM_INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW_VIEW_TASK).build()
        );

        expectedAuthPermissions.forEach(permission -> authority.addPermission(
            AuthorityPermission.builder().permission(permission.getPermission()).authority(authority).build()));

        authorityAssignmentService.updatePendingAuthority(authority, roleCode, authModificationUserId);

        ArgumentCaptor<Authority> authorityCaptor = ArgumentCaptor.forClass(Authority.class);
        verify(authorityRepository, times(1)).save(authorityCaptor.capture());
        Authority savedAuthority = authorityCaptor.getValue();

        assertThat(savedAuthority.getUuid()).isNotBlank();
        assertThat(savedAuthority.getUserId()).isEqualTo(authority.getUserId());
        assertThat(savedAuthority.getStatus()).isEqualTo(authority.getStatus());
        assertThat(savedAuthority.getAuthorityPermissions()).containsExactlyInAnyOrderElementsOf(expectedAuthPermissions);
        assertThat(savedAuthority.getUuid()).isNotNull();
        assertThat(savedAuthority.getCreatedBy()).isEqualTo(authModificationUserId);

        verifyNoInteractions(roleService);
    }

    @Test
    void updateAuthorityWithNewRole() {
        final String newRoleCode = "newRoleCode";
        Authority authority = Authority.builder().userId("user").status(AuthorityStatus.ACTIVE).code("roleCode").build();
        authority.setAuthorityPermissions(new ArrayList<>(List.of(
            AuthorityPermission.builder().authority(authority).permission(PERM_ACCOUNT_USERS_EDIT).build(),
            AuthorityPermission.builder().authority(authority).permission(PERM_INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW_VIEW_TASK).build()
        )));

        List<AuthorityPermission> expectedAuthPermissions = List.of(
            AuthorityPermission.builder().authority(authority).permission(PERM_INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW_VIEW_TASK).build(),
            AuthorityPermission.builder().authority(authority).permission(PERM_ACCOUNT_USERS_EDIT).build()
        );

        Role newRole = Role.builder().code(newRoleCode).build();

        expectedAuthPermissions.forEach(permission -> newRole.addPermission(
            RolePermission.builder().permission(permission.getPermission()).role(newRole).build()));

        authorityAssignmentService.updateAuthorityWithNewRole(authority, newRole);

        ArgumentCaptor<Authority> authorityCaptor = ArgumentCaptor.forClass(Authority.class);
        verify(authorityRepository, times(1)).save(authorityCaptor.capture());
        Authority savedAuthority = authorityCaptor.getValue();

        assertThat(savedAuthority.getCode()).isEqualTo(newRole.getCode());
        assertThat(savedAuthority.getUserId()).isEqualTo(authority.getUserId());
        assertThat(savedAuthority.getStatus()).isEqualTo(authority.getStatus());
        assertThat(savedAuthority.getAuthorityPermissions()).containsExactlyInAnyOrderElementsOf(expectedAuthPermissions);
    }

    @Test
    void updateAuthorityWithNewPermissions() {
        Authority authority = Authority.builder().userId("user").status(AuthorityStatus.ACTIVE).build();
        authority.setAuthorityPermissions(new ArrayList<>(List.of(
            AuthorityPermission.builder().authority(authority).permission(PERM_ACCOUNT_USERS_EDIT).build()
        )));

        List<Permission> permissions = List.of(PERM_INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW_VIEW_TASK, PERM_ACCOUNT_USERS_EDIT);

        List<AuthorityPermission> expectedAuthPermissions = permissions.stream()
            .map(p -> AuthorityPermission.builder().authority(authority).permission(p).build())
            .collect(Collectors.toList());

        authorityAssignmentService.updateAuthorityWithNewPermissions(authority, permissions);

        ArgumentCaptor<Authority> authorityCaptor = ArgumentCaptor.forClass(Authority.class);
        verify(authorityRepository, times(1)).save(authorityCaptor.capture());
        Authority savedAuthority = authorityCaptor.getValue();

        assertThat(savedAuthority.getUserId()).isEqualTo(authority.getUserId());
        assertThat(savedAuthority.getStatus()).isEqualTo(authority.getStatus());
        assertThat(savedAuthority.getAuthorityPermissions()).containsExactlyInAnyOrderElementsOf(expectedAuthPermissions);
    }

    @Test
    void updatePendingAuthorityWithNewPermissions() {
        final String authModificationUserId = "userId";
        Authority authority = Authority.builder().userId("user").status(AuthorityStatus.ACTIVE).build();
        authority.setAuthorityPermissions(new ArrayList<>(List.of(
            AuthorityPermission.builder().authority(authority).permission(PERM_ACCOUNT_USERS_EDIT).build()
        )));

        List<Permission> permissions = List.of(PERM_INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW_VIEW_TASK, PERM_ACCOUNT_USERS_EDIT);

        List<AuthorityPermission> expectedAuthPermissions = permissions.stream()
            .map(p -> AuthorityPermission.builder().authority(authority).permission(p).build())
            .collect(Collectors.toList());

        authorityAssignmentService.updatePendingAuthorityWithNewPermissions(authority, permissions, authModificationUserId);

        ArgumentCaptor<Authority> authorityCaptor = ArgumentCaptor.forClass(Authority.class);
        verify(authorityRepository, times(1)).save(authorityCaptor.capture());
        Authority savedAuthority = authorityCaptor.getValue();

        assertThat(savedAuthority.getUserId()).isEqualTo(authority.getUserId());
        assertThat(savedAuthority.getStatus()).isEqualTo(authority.getStatus());
        assertThat(savedAuthority.getAuthorityPermissions()).containsExactlyInAnyOrderElementsOf(expectedAuthPermissions);
        assertThat(savedAuthority.getUuid()).isNotNull();
        assertThat(savedAuthority.getCreatedBy()).isEqualTo(authModificationUserId);
    }

    @Test
    void activateAuthority() {
        Long authorityId = 1L;
        Authority authority = Authority.builder().userId("user").status(AuthorityStatus.PENDING).build();

        when(authorityRepository.findById(authorityId)).thenReturn(Optional.of(authority));

        authorityAssignmentService.activateAuthority(authorityId);

        verify(authorityRepository, times(1)).findById(authorityId);
        ArgumentCaptor<Authority> authorityCaptor = ArgumentCaptor.forClass(Authority.class);
        verify(authorityRepository, times(1)).save(authorityCaptor.capture());
        Authority authoritySaved = authorityCaptor.getValue();
        assertThat(authoritySaved.getStatus()).isEqualTo(AuthorityStatus.ACTIVE);
    }

    @Test
    void activateAuthority_authority_not_found() {
        Long authorityId = 1L;

        when(authorityRepository.findById(authorityId)).thenReturn(Optional.empty());

        BusinessException businessException = assertThrows(BusinessException.class,
            () -> authorityAssignmentService.activateAuthority(authorityId));

        assertEquals(ErrorCode.RESOURCE_NOT_FOUND, businessException.getErrorCode());

        verify(authorityRepository, never()).save(any());
    }

    @Test
    void updateAuthorityStatus() {
    	Long authorityId = 1L;
        Authority authority = Authority.builder().userId("user").status(AuthorityStatus.PENDING).build();

    	when(authorityRepository.findById(authorityId)).thenReturn(Optional.of(authority));

        authorityAssignmentService.updateAuthorityStatus(authorityId, AuthorityStatus.ACTIVE);

    	verify(authorityRepository, times(1)).findById(authorityId);
    	ArgumentCaptor<Authority> authorityCaptor = ArgumentCaptor.forClass(Authority.class);
    	verify(authorityRepository, times(1)).save(authorityCaptor.capture());
    	Authority authoritySaved = authorityCaptor.getValue();
    	assertThat(authoritySaved.getStatus()).isEqualTo(AuthorityStatus.ACTIVE);
    }
}