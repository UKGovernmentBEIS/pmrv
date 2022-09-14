package uk.gov.pmrv.api.user.verifier.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.authorization.verifier.service.VerifierAuthorityService;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.user.verifier.domain.VerifierUserDTO;

@Service
@RequiredArgsConstructor
public class VerifierUserManagementService {
	
	private final VerifierAuthorityService verifierAuthorityService;
	private final VerifierUserAuthService verifierUserAuthService;

	public VerifierUserDTO getVerifierUserById(PmrvUser pmrvUser, String userId) {
        validateUserBasedOnAuthUserRole(pmrvUser, userId);
		return verifierUserAuthService.getVerifierUserById(userId);
	}

	public void updateVerifierUserById(PmrvUser pmrvUser, String userId, VerifierUserDTO verifierUserDTO) {
	    validateUserBasedOnAuthUserRole(pmrvUser, userId);
		verifierUserAuthService.updateVerifierUser(userId, verifierUserDTO);
	}

	public void updateCurrentVerifierUser(PmrvUser pmrvUser, VerifierUserDTO verifierUserDTO) {
		verifierUserAuthService.updateVerifierUser(pmrvUser.getUserId(), verifierUserDTO);
	}

	private void validateUserBasedOnAuthUserRole(PmrvUser pmrvUser, String userId) {
        switch (pmrvUser.getRoleType()) {
            case REGULATOR:
                validateUserIsVerifier(userId);
                break;
            case VERIFIER:
                validateUserHasAccessToVerificationBody(pmrvUser, userId);
                break;
            default:
                throw new UnsupportedOperationException(
                    String.format("User with role type %s can not access verifier user", pmrvUser.getRoleType()));
        }
    }

	/** Validate if user has access to queried user's verification body. */
	private void validateUserHasAccessToVerificationBody(PmrvUser pmrvUser, String userId) {
		Long verificationBodyId = pmrvUser.getVerificationBodyId();

		if(!verifierAuthorityService.existsByUserIdAndVerificationBodyId(userId, verificationBodyId)){
			throw new BusinessException(ErrorCode.AUTHORITY_USER_NOT_RELATED_TO_VERIFICATION_BODY);
		}
	}

	private void validateUserIsVerifier(String userId) {
        if(!verifierAuthorityService.existsByUserIdAndVerificationBodyIdNotNull(userId)) {
            throw new BusinessException(ErrorCode.AUTHORITY_USER_IS_NOT_VERIFIER);
        }
    }
}
