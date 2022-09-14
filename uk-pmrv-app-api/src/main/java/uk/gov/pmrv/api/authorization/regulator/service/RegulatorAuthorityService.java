package uk.gov.pmrv.api.authorization.regulator.service;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.pmrv.api.authorization.core.domain.Authority;
import uk.gov.pmrv.api.authorization.core.domain.AuthorityStatus;
import uk.gov.pmrv.api.authorization.core.domain.Permission;
import uk.gov.pmrv.api.authorization.core.repository.AuthorityRepository;
import uk.gov.pmrv.api.authorization.core.service.AuthorityAssignmentService;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.common.utils.UuidGenerator;

@Service
@RequiredArgsConstructor
public class RegulatorAuthorityService {

    private final AuthorityRepository authorityRepository;
    private final AuthorityAssignmentService authorityAssignmentService;
    
    @Transactional
    public String createRegulatorAuthorityPermissions(
            PmrvUser authModificationUser,
            String userId,
            CompetentAuthority competentAuthority,
            List<Permission> permissions) {
        Optional<Authority> authorityOpt = authorityRepository.findByUserIdAndCompetentAuthority(userId, competentAuthority);

        Authority authority;
        if (authorityOpt.isEmpty()) {
            //create new pending authority
            authority = createPendingAuthority(competentAuthority, userId, authModificationUser.getUserId(), permissions);
        } else {
            //update existing authority
            authority = authorityOpt.get();
            if (authority.getStatus() != AuthorityStatus.PENDING) {
                throw new BusinessException(ErrorCode.AUTHORITY_USER_UPDATE_NON_PENDING_AUTHORITY_NOT_ALLOWED);
            }
            authority = authorityAssignmentService.updatePendingAuthorityWithNewPermissions(
                    authority, permissions, authModificationUser.getUserId());
        }
        return authority.getUuid();
    }
    
    @Transactional
    public Authority acceptAuthority(Long authorityId) {
        return authorityAssignmentService.updateAuthorityStatus(authorityId, AuthorityStatus.ACCEPTED);
    }
    
    public boolean existsByUserIdAndCompetentAuthority(String userId, CompetentAuthority competentAuthority) {
        return authorityRepository.existsByUserIdAndCompetentAuthority(userId, competentAuthority);
    }

    private Authority createPendingAuthority(CompetentAuthority competentAuthority, String user, String authModificationUser,
                                             List<Permission> permissions) {
        Authority authority = Authority.builder()
            .userId(user)
            .competentAuthority(competentAuthority)
            .status(AuthorityStatus.PENDING)
            .createdBy(authModificationUser)
            .uuid(UuidGenerator.generate())
            .build();

        return authorityAssignmentService.createAuthorityWithPermissions(authority, permissions);
    }
}
