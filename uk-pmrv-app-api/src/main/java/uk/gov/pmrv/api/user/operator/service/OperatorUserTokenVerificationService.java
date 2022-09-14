package uk.gov.pmrv.api.user.operator.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.authorization.core.domain.dto.AuthorityInfoDTO;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.user.JwtTokenActionEnum;
import uk.gov.pmrv.api.user.core.service.UserInvitationTokenVerificationService;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;

@Service
@RequiredArgsConstructor
public class OperatorUserTokenVerificationService {
	
	private final UserAuthService userAuthService;
	private final UserInvitationTokenVerificationService userInvitationTokenVerificationService;

	public String verifyRegistrationToken(String token) {
	    String userEmail = userAuthService.resolveTokenActionClaim(token, JwtTokenActionEnum.USER_REGISTRATION);

        if (userAuthService.getUserByEmail(userEmail).isPresent()) {
            throw new BusinessException(ErrorCode.USER_ALREADY_REGISTERED);
        }

        return userEmail;
    }
	
    public AuthorityInfoDTO verifyInvitationTokenForPendingAuthority(String invitationToken) {
        return userInvitationTokenVerificationService
            .verifyInvitationTokenForPendingAuthority(invitationToken, JwtTokenActionEnum.OPERATOR_INVITATION);
    }
}
