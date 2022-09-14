package uk.gov.pmrv.api.authorization.operator.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.authorization.AuthorityConstants;
import uk.gov.pmrv.api.authorization.core.domain.Authority;
import uk.gov.pmrv.api.authorization.core.domain.AuthorityStatus;
import uk.gov.pmrv.api.authorization.core.domain.Role;
import uk.gov.pmrv.api.authorization.core.domain.dto.AuthorityRoleDTO;
import uk.gov.pmrv.api.authorization.core.domain.dto.UserRoleTypeDTO;
import uk.gov.pmrv.api.authorization.core.repository.AuthorityRepository;
import uk.gov.pmrv.api.authorization.core.repository.RoleRepository;
import uk.gov.pmrv.api.authorization.core.service.AuthorityAssignmentService;
import uk.gov.pmrv.api.authorization.core.service.UserRoleTypeService;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.common.utils.UuidGenerator;

import java.util.List;
import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class OperatorAuthorityService {

    private final AuthorityAssignmentService authorityAssignmentService;
    private final AuthorityRepository authorityRepository;
    private final RoleRepository roleRepository;
    private final UserRoleTypeService userRoleTypeService;

    /**
     * Create operator-admin role permission for the provided user and account.
     * @param accountId the account of the operator user
     * @param user the user
     */
    @Transactional
    public void createOperatorAdminAuthority(Long accountId, String user) {
        createOperatorUserAuthorityForRole(accountId, AuthorityConstants.OPERATOR_ADMIN_ROLE_CODE, user,
                AuthorityStatus.ACTIVE, user);
    }

    public Authority acceptAuthority(Long authorityId) {
        return authorityAssignmentService
                .updateAuthorityStatus(authorityId, AuthorityStatus.ACCEPTED);
    }

    /**
     * Find operator user authorities by account.
     * @param accountId the account id
     * @return the list of operator user authority info along with role info
     */
    public List<AuthorityRoleDTO> findOperatorUserAuthorityRoleListByAccount(Long accountId) {
        return authorityRepository.findOperatorUserAuthorityRoleListByAccount(accountId);
    }

    /**
     * Creates an authority entry with status {@link AuthorityStatus#PENDING} using the provided input.
     * @param accountId the if of the account related to the authority that will be created
     * @param roleCode the authority role code
     * @param authorizedUserId the user id to whom the authority will be assigned
     * @param authModificationUser the current logged-in {@link PmrvUser}
     * @return the {@link Authority} uuid
     */
    @Transactional
    public String createPendingAuthorityForOperator(Long accountId, String roleCode, String authorizedUserId,
                                                    PmrvUser authModificationUser) {
        Optional<Authority> userAuthorityForAccountOptional =
                authorityRepository.findByUserIdAndAccountId(authorizedUserId, accountId);

        Authority userAuthorityForAccount;

        if (userAuthorityForAccountOptional.isPresent()) {
            userAuthorityForAccount = userAuthorityForAccountOptional.get();

            if (AuthorityStatus.PENDING.equals(userAuthorityForAccount.getStatus())) {
                userAuthorityForAccount =
                        authorityAssignmentService.updatePendingAuthority(userAuthorityForAccount, roleCode, authModificationUser.getUserId());
            } else {
                log.warn("Authority for user '{}' in account '{}' exists with code '{}' and status'{}'",
                        () -> authorizedUserId, () -> accountId, userAuthorityForAccount::getCode, userAuthorityForAccount::getStatus);
                throw new BusinessException(ErrorCode.AUTHORITY_USER_UPDATE_NON_PENDING_AUTHORITY_NOT_ALLOWED);
            }

        } else {
            userAuthorityForAccount =  createOperatorUserAuthorityForRole(accountId, roleCode, authorizedUserId, AuthorityStatus.PENDING,
                    authModificationUser.getUserId());
        }

        return userAuthorityForAccount.getUuid();
    }

    public List<String> findActiveOperatorAdminUsersByAccount(Long accountId){
        return authorityRepository.findActiveOperatorUsersByAccountAndRoleCode(accountId, AuthorityConstants.OPERATOR_ADMIN_ROLE_CODE);
    }

    private void checkAuthorityCreationAllowance(RoleType roleType, String user) {
        if (!isUserAllowedToAssignRoleType(roleType, user)) {
            throw new BusinessException(ErrorCode.AUTHORITY_CREATION_NOT_ALLOWED);
        }
    }

    private boolean isUserAllowedToAssignRoleType(RoleType roleType, String user) {
        UserRoleTypeDTO userRoleType = userRoleTypeService.getUserRoleTypeByUserId(user);
        return userRoleType == null || roleType.equals(userRoleType.getRoleType());
    }

    private Role getRoleByCode(String roleCode) {
        return roleRepository.findByCode(roleCode)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
    }

    private Authority createOperatorUserAuthorityForRole(Long accountId, String roleCode, String authorizedUser,
                                                         AuthorityStatus authorityStatus, String authModificationUser) {
        Role role = getRoleByCode(roleCode);
        checkAuthorityCreationAllowance(role.getType(), authorizedUser);

        Authority authority = Authority.builder()
                .userId(authorizedUser)
                .code(role.getCode())
                .accountId(accountId)
                .status(authorityStatus)
                .createdBy(authModificationUser)
                .uuid(authorityStatus.equals(AuthorityStatus.PENDING) ? UuidGenerator.generate() : null)
                .build();

        return authorityAssignmentService.createAuthorityPermissionsForRole(authority, role);
    }

}
