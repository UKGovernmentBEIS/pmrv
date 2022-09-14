package uk.gov.pmrv.api.authorization.verifier.service;

import static uk.gov.pmrv.api.authorization.AuthorityConstants.VERIFIER_ADMIN_ROLE_CODE;
import static uk.gov.pmrv.api.authorization.core.domain.AuthorityStatus.ACTIVE;
import static uk.gov.pmrv.api.common.exception.ErrorCode.AUTHORITY_VERIFIER_ADMIN_SHOULD_EXIST;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.authorization.core.domain.Authority;
import uk.gov.pmrv.api.authorization.verifier.domain.VerifierAuthorityUpdateDTO;
import uk.gov.pmrv.api.authorization.core.repository.AuthorityRepository;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;

@Component
@RequiredArgsConstructor
public class VerifierAdminExistenceValidator implements VerifierAuthorityUpdateValidator {

	private final AuthorityRepository authorityRepository;

	@Override
	public void validateUpdate(List<VerifierAuthorityUpdateDTO> verifiersUpdate, Long verificationBodyId) {
		if(ObjectUtils.isEmpty(verifiersUpdate) || isActiveAdminSelected(verifiersUpdate)) {
			return;
		}
		
		List<String> currentVerifierAdmins = 
				authorityRepository.findUsersByVerificationBodyAndCode(
						verificationBodyId, VERIFIER_ADMIN_ROLE_CODE);
		
		List<String> verifiersToUpdate = verifiersUpdate.stream()
            .filter(verifierAuthorityUpdate -> currentVerifierAdmins.contains(verifierAuthorityUpdate.getUserId()) &&
                (!VERIFIER_ADMIN_ROLE_CODE.equalsIgnoreCase(verifierAuthorityUpdate.getRoleCode()) ||
                    ACTIVE != verifierAuthorityUpdate.getAuthorityStatus()))
            .map(VerifierAuthorityUpdateDTO::getUserId)
            .collect(Collectors.toList());
		
		if(verifiersToUpdate.containsAll(currentVerifierAdmins)) {
			throw new BusinessException(ErrorCode.AUTHORITY_VERIFIER_ADMIN_SHOULD_EXIST);
		}
	}

	public void validateDeletion(Authority authority) {
		if (VERIFIER_ADMIN_ROLE_CODE.equals(authority.getCode()) 
			&& !authorityRepository.existsOtherVerificationBodyAdmin(authority.getUserId(), authority.getVerificationBodyId()))
			throw new BusinessException(AUTHORITY_VERIFIER_ADMIN_SHOULD_EXIST);
	}
	
	private boolean isActiveAdminSelected(List<VerifierAuthorityUpdateDTO> verifiersUpdate) {
		return verifiersUpdate.stream()
            .anyMatch(v -> VERIFIER_ADMIN_ROLE_CODE.equals(v.getRoleCode()) && ACTIVE == v.getAuthorityStatus());
	}
}
