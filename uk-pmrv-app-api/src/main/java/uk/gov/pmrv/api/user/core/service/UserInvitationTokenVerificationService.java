package uk.gov.pmrv.api.user.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.authorization.core.domain.dto.AuthorityInfoDTO;
import uk.gov.pmrv.api.authorization.core.service.AuthorityService;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.user.JwtTokenActionEnum;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;

@Service
@RequiredArgsConstructor
public class UserInvitationTokenVerificationService {

    private final UserAuthService userAuthService;
    private final AuthorityService authorityService;

    public AuthorityInfoDTO verifyInvitationTokenForPendingAuthority(String invitationToken, JwtTokenActionEnum tokenAction) {
        String authorityUuid = userAuthService.resolveTokenActionClaim(invitationToken, tokenAction);
        return authorityService.findAuthorityByUuidAndStatusPending(authorityUuid)
            .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_TOKEN));
    }

}
