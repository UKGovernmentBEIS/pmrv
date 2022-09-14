package uk.gov.pmrv.api.user.regulator.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.authorization.core.domain.Authority;
import uk.gov.pmrv.api.authorization.core.domain.dto.AuthorityInfoDTO;
import uk.gov.pmrv.api.authorization.regulator.service.RegulatorAuthorityService;
import uk.gov.pmrv.api.user.core.domain.dto.InvitedUserEnableDTO;
import uk.gov.pmrv.api.user.core.domain.dto.UserInfoDTO;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;

@Service
@RequiredArgsConstructor
public class RegulatorUserAcceptInvitationService {

    private final UserAuthService userAuthService;
    private final RegulatorUserTokenVerificationService regulatorUserTokenVerificationService;
    private final RegulatorAuthorityService regulatorAuthorityService;
    private final RegulatorUserNotificationGateway regulatorUserNotificationGateway;

    @Transactional
    public void acceptAndEnableRegulatorInvitedUser(InvitedUserEnableDTO invitedUserEnableDTO) {
        AuthorityInfoDTO authorityInfo = regulatorUserTokenVerificationService
            .verifyInvitationTokenForPendingAuthority(invitedUserEnableDTO.getInvitationToken());

        final Long authorityId = authorityInfo.getId();
        final String userId = authorityInfo.getUserId();

        final Authority authority = regulatorAuthorityService.acceptAuthority(authorityId);
        userAuthService.enablePendingUser(userId, invitedUserEnableDTO.getPassword());

        final UserInfoDTO invitee = userAuthService.getUserByUserId(userId);
        final UserInfoDTO inviter = userAuthService.getUserByUserId(authority.getCreatedBy());
        final String inviteeEmail = invitee.getEmail();

        regulatorUserNotificationGateway.notifyInviteeAcceptedInvitation(inviteeEmail);
        regulatorUserNotificationGateway.notifyInviterAcceptedInvitation(invitee, inviter);
    }
}
