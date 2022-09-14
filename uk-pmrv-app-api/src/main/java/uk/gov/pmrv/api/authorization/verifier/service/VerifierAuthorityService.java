package uk.gov.pmrv.api.authorization.verifier.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.authorization.core.domain.Authority;
import uk.gov.pmrv.api.authorization.core.domain.AuthorityStatus;
import uk.gov.pmrv.api.authorization.core.domain.Role;
import uk.gov.pmrv.api.authorization.core.repository.AuthorityRepository;
import uk.gov.pmrv.api.authorization.core.repository.RoleRepository;
import uk.gov.pmrv.api.authorization.core.service.AuthorityAssignmentService;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.common.utils.UuidGenerator;

@Log4j2
@Service
@RequiredArgsConstructor
public class VerifierAuthorityService {

    private final AuthorityRepository authorityRepository;
    private final RoleRepository roleRepository;
    private final AuthorityAssignmentService authorityAssignmentService;

    public boolean existsByUserIdAndVerificationBodyId(String userId, Long verificationBodyId) {
        return authorityRepository.existsByUserIdAndVerificationBodyId(userId, verificationBodyId);
    }

    /**
     * Creates an authority entry with status {@link AuthorityStatus#PENDING} using the provided input.
     * @param vbId the verification body id related to the authority that will be created
     * @param roleCode the authority role code
     * @param verifierUser the user id to whom the authority will be assigned
     * @param authCreationUser the current logged-in user {@link PmrvUser}
     * @return the created authority uuid
     */
    @Transactional
    public String createPendingAuthority(Long vbId, String roleCode, String verifierUser, PmrvUser authCreationUser) {
        Optional<Authority> userAuthorityOptional = authorityRepository.findByUserIdAndVerificationBodyId(verifierUser, vbId);

        Authority authority;
        if (userAuthorityOptional.isPresent()) {
            authority = userAuthorityOptional.get();

            if (AuthorityStatus.PENDING.equals(authority.getStatus())) {
                authority = authorityAssignmentService.updatePendingAuthority(authority, roleCode, authCreationUser.getUserId());
            } else {
                log.warn("Authority for user '{}' in verification body '{}' exists with code '{}' and status'{}'",
                    () -> verifierUser, () -> vbId, authority::getCode, authority::getStatus);
                throw new BusinessException(ErrorCode.AUTHORITY_USER_UPDATE_NON_PENDING_AUTHORITY_NOT_ALLOWED);
            }

        } else {
            authority = createPendingAuthorityForRole(vbId, roleCode, verifierUser, authCreationUser.getUserId());
        }
        return authority.getUuid();
    }

    public boolean existsByUserIdAndVerificationBodyIdNotNull(String userId) {
        return authorityRepository.findByUserIdAndVerificationBodyIdNotNull(userId).isPresent();
    }

    public Authority acceptAuthority(Long authorityId) {
        return authorityAssignmentService
                .updateAuthorityStatus(authorityId, AuthorityStatus.ACCEPTED);
    }

    private Authority createPendingAuthorityForRole(Long vbId, String roleCode, String verifierUser,
                                                    String authCreationUser) {
        Role role = getRoleByRoleCode(roleCode);
        Authority authority = Authority.builder()
            .userId(verifierUser)
            .code(role.getCode())
            .status(AuthorityStatus.PENDING)
            .verificationBodyId(vbId)
            .createdBy(authCreationUser)
            .uuid(UuidGenerator.generate())
            .build();

        return authorityAssignmentService.createAuthorityPermissionsForRole(authority, role);
    }

    private Role getRoleByRoleCode(String roleCode) {
        return roleRepository.findByCode(roleCode)
            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
    }

}
