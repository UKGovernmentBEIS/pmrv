package uk.gov.pmrv.api.authorization.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.authorization.core.domain.Authority;
import uk.gov.pmrv.api.authorization.core.domain.AuthorityPermission;
import uk.gov.pmrv.api.authorization.core.domain.AuthorityStatus;
import uk.gov.pmrv.api.authorization.core.domain.Permission;
import uk.gov.pmrv.api.authorization.core.domain.Role;
import uk.gov.pmrv.api.authorization.core.domain.RolePermission;
import uk.gov.pmrv.api.authorization.core.repository.AuthorityRepository;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.common.utils.UuidGenerator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthorityAssignmentService {

    private final AuthorityRepository authorityRepository;
    private final RoleService roleService;

    /**
     * Creates a new authority for a specific role.
     * @param authority the authority to be created.
     * @param role the {@link Role} related to the created authority
     * @return the persisted {@link Authority}
     */
    @Transactional
    public Authority createAuthorityPermissionsForRole(Authority authority, Role role) {
        return createAuthorityWithPermissions(authority,
            role.getRolePermissions().stream()
                .map(RolePermission::getPermission)
                .collect(Collectors.toList()));
    }

    /**
     * Creates a new authority with specific authority permissions.
     * @param authority the authority to be created.
     * @param permissions {@link Map} with the permissions to be assigned to the newly created authority
     * @return the persisted {@link Authority}
     */
    @Transactional
    public Authority createAuthorityWithPermissions(Authority authority, List<Permission> permissions) {
        permissions.forEach(permission -> addPermissionToAuthority(authority, permission));
        return authorityRepository.save(authority);
    }

    /**
     * Updates an authority with the {@code roleCode}.
     * @param authority the authority to be updated
     * @param roleCode the new authority role code
     * @return the updated {@link Authority}
     */
    @Transactional
    public Authority updatePendingAuthority(Authority authority, String roleCode, String authModificationUserId) {
        if (!authority.getCode().equals(roleCode)) {
            assignAuthorityWithNewRole(authority, roleService.getRoleByRoleCode(roleCode));
        }

        authority.setUuid(UuidGenerator.generate());
        updateAuthorityAuditingInfo(authority, authModificationUserId);
        return authorityRepository.save(authority);
    }

    /**
     * Updates the {@code authority} with new {@code newRole}.
     * @param authority the {@link Authority} to be assigned the new role
     * @param newRole the {@link Role} to be be assigned to the authority
     * @return the updated authority
     */
    @Transactional
    public Authority updateAuthorityWithNewRole(Authority authority, Role newRole) {
        assignAuthorityWithNewRole(authority, newRole);
        return authorityRepository.save(authority);
    }

    /**
     *  Updates the {@code authority} with new {@code permissions}.
     * @param authority authority the {@link Authority} to be updated
     * @param permissions permissions the permissions to be assigned to the authority
     * @param authModificationUserId the user created the authority
     * @return the updated {@link Authority}
     */
    @Transactional
    public Authority updatePendingAuthorityWithNewPermissions(Authority authority, List<Permission> permissions,
                                                              String authModificationUserId) {
        authority.setUuid(UuidGenerator.generate());
        updateAuthorityAuditingInfo(authority, authModificationUserId);
        return updateAuthorityWithNewPermissions(authority, permissions);
    }

    /**
     * Updates the {@code authority} with new {@code permissions}.
     * @param authority the {@link Authority} to be updated
     * @param permissions the permissions to be assigned to the authority
     * @return the updated {@link Authority}
     */
    @Transactional
    public Authority updateAuthorityWithNewPermissions(Authority authority, List<Permission> permissions) {
        Set<AuthorityPermission> newAuthorityPermissions = buildNewAuthorityPermissions(authority, permissions);
        assignNewAuthorityPermissions(authority, newAuthorityPermissions);
        return authorityRepository.save(authority);
    }

    /**
     * Set authority's status to {@link AuthorityStatus#ACTIVE}.
     * @param authorityId the authority id
     * @return {@link Authority}
     */
    @Transactional
    public Authority activateAuthority(Long authorityId) {
        Authority authority = authorityRepository.findById(authorityId)
            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
        authority.setStatus(AuthorityStatus.ACTIVE);
        return authorityRepository.save(authority);
    }

    @Transactional
    public Authority updateAuthorityStatus(Long authorityId, AuthorityStatus status) {
        Authority authority = authorityRepository.findById(authorityId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
        authority.setStatus(status);

        return authorityRepository.save(authority);
    }

    private void assignAuthorityWithNewRole(Authority authority, Role newRole) {
        authority.setCode(newRole.getCode());
        Set<AuthorityPermission> newAuthorityPermissions = buildNewAuthorityPermissions(authority, newRole);
        assignNewAuthorityPermissions(authority, newAuthorityPermissions);
    }

    private void assignNewAuthorityPermissions(Authority authority, Set<AuthorityPermission> newAuthorityPermissions) {
        // remove those that are not contained in new permissions
        authority.getAuthorityPermissions()
            .removeIf(ap -> !newAuthorityPermissions.contains(ap));

        //add new ones
        newAuthorityPermissions
            .stream()
            .filter(nap -> !authority.getAuthorityPermissions().contains(nap))
            .forEach(authority::addPermission);
    }

    private void addPermissionToAuthority(Authority authority, Permission permission) {
        authority
            .addPermission(
                AuthorityPermission.builder()
                    .permission(permission)
                    .build());
    }

    private Set<AuthorityPermission> buildNewAuthorityPermissions(Authority authority, Role newRole) {
        return newRole.getRolePermissions().stream()
            .map(rp ->
                AuthorityPermission.builder()
                    .authority(authority)
                    .permission(rp.getPermission())
                    .build()
            )
            .collect(Collectors.toSet());
    }

    private Set<AuthorityPermission> buildNewAuthorityPermissions(Authority authority, List<Permission> permissions) {
        return permissions
            .stream()
            .map(p ->
                    AuthorityPermission.builder()
                        .authority(authority)
                        .permission(p)
                        .build()
            )
            .collect(Collectors.toSet());
    }

    private void updateAuthorityAuditingInfo(Authority authority, String authModificationUserId) {
        authority.setCreatedBy(authModificationUserId);
        authority.setCreationDate(LocalDateTime.now());
    }
}
