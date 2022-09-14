package uk.gov.pmrv.api.authorization.verifier.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.authorization.AuthorityConstants;
import uk.gov.pmrv.api.authorization.core.domain.dto.AuthorityRoleDTO;
import uk.gov.pmrv.api.authorization.core.domain.dto.UserAuthoritiesDTO;
import uk.gov.pmrv.api.authorization.core.domain.dto.UserAuthorityDTO;
import uk.gov.pmrv.api.authorization.core.repository.AuthorityRepository;
import uk.gov.pmrv.api.authorization.core.transform.UserAuthorityMapper;
import uk.gov.pmrv.api.authorization.rules.domain.Scope;
import uk.gov.pmrv.api.authorization.rules.services.resource.VerificationBodyAuthorizationResourceService;
import uk.gov.pmrv.api.common.domain.model.PmrvAuthority;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;

@Service
@RequiredArgsConstructor
public class VerifierAuthorityQueryService {

	private final AuthorityRepository authorityRepository;
	private final VerificationBodyAuthorizationResourceService verificationBodyAuthorizationResourceService;
    private final UserAuthorityMapper userAuthorityMapper = Mappers.getMapper(UserAuthorityMapper.class);

	public List<String> findVerifierAdminsByVerificationBody(Long verificationBodyId){
	    return authorityRepository.findUsersByVerificationBodyAndCode(verificationBodyId, AuthorityConstants.VERIFIER_ADMIN_ROLE_CODE);
	}

    public boolean existsByUserIdAndVerificationBodyId(String userId, Long verificationBodyId) {
        return authorityRepository.existsByUserIdAndVerificationBodyId(userId, verificationBodyId);
    }

    public UserAuthoritiesDTO getVerifierAuthorities(PmrvUser authUser) {
        final PmrvAuthority verificationBodyAuthority = authUser.getAuthorities().get(0);
        final Long verificationBodyId = verificationBodyAuthority.getVerificationBodyId();

        boolean hasEditUserScopeOnVerificationBody = verificationBodyAuthorizationResourceService
            .hasUserScopeToVerificationBody(authUser, verificationBodyId, Scope.EDIT_USER);

        return getVerificationBodyAuthorities(verificationBodyId, hasEditUserScopeOnVerificationBody);
    }

    public UserAuthoritiesDTO getVerificationBodyAuthorities(Long verificationBodyId, boolean hasAuthorityToEditVerifiersAuthorities) {
        List<AuthorityRoleDTO> verifierUserAuthorities = hasAuthorityToEditVerifiersAuthorities ?
            authorityRepository.findVerifierUserAuthorityRoleListByVerificationBody(verificationBodyId) :
            authorityRepository.findNonPendingVerifierUserAuthorityRoleListByVerificationBody(verificationBodyId);

        List<UserAuthorityDTO> vbUserAuthorities = verifierUserAuthorities.stream()
            .map(authorityRole -> userAuthorityMapper.toUserAuthority(authorityRole, hasAuthorityToEditVerifiersAuthorities))
            .collect(Collectors.toList());

        return UserAuthoritiesDTO.builder()
            .authorities(vbUserAuthorities)
            .editable(hasAuthorityToEditVerifiersAuthorities)
            .build();
    }
}
