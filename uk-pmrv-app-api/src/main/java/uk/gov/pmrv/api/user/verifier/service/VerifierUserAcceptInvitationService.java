package uk.gov.pmrv.api.user.verifier.service;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.pmrv.api.authorization.core.domain.dto.AuthorityInfoDTO;
import uk.gov.pmrv.api.authorization.verifier.service.VerifierAuthorityService;
import uk.gov.pmrv.api.user.core.domain.dto.InvitedUserEnableDTO;
import uk.gov.pmrv.api.user.core.domain.dto.UserInfoDTO;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;

@Service
@RequiredArgsConstructor
public class VerifierUserAcceptInvitationService {

    private final UserAuthService userAuthService;
    private final VerifierUserTokenVerificationService verifierUserTokenVerificationService;
    private final VerifierAuthorityService verifierAuthorityService;
    private final VerifierUserNotificationGateway verifierUserNotificationGateway;

    @Transactional
    public void acceptAndEnableVerifierInvitedUser(InvitedUserEnableDTO invitedUserEnableDTO) {
        AuthorityInfoDTO authorityInfo = verifierUserTokenVerificationService
            .verifyInvitationTokenForPendingAuthority(invitedUserEnableDTO.getInvitationToken());

        // Update authority to ACCEPTED
        String inviterUserId = verifierAuthorityService.acceptAuthority(authorityInfo.getId())
                .getCreatedBy();

        // Enable user in keycloak
        userAuthService.enablePendingUser(authorityInfo.getUserId(), invitedUserEnableDTO.getPassword());

        // Notify invitee
        UserInfoDTO invitee = userAuthService.getUserByUserId(authorityInfo.getUserId());
        verifierUserNotificationGateway.notifyInviteeAcceptedInvitation(invitee);

        // Notify inviter
        UserInfoDTO inviter = userAuthService.getUserByUserId(inviterUserId);
        verifierUserNotificationGateway.notifyInviterAcceptedInvitation(invitee, inviter);
    }
}
