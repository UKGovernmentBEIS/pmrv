package uk.gov.pmrv.api.user.regulator.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.authorization.core.domain.dto.AuthorityInfoDTO;
import uk.gov.pmrv.api.user.JwtTokenActionEnum;
import uk.gov.pmrv.api.user.core.service.UserInvitationTokenVerificationService;

@Service
@RequiredArgsConstructor
public class RegulatorUserTokenVerificationService {

    private final UserInvitationTokenVerificationService userInvitationTokenVerificationService;

    public AuthorityInfoDTO verifyInvitationTokenForPendingAuthority(String invitationToken) {
        return userInvitationTokenVerificationService
            .verifyInvitationTokenForPendingAuthority(invitationToken, JwtTokenActionEnum.REGULATOR_INVITATION);
    }
}
