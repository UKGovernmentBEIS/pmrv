package uk.gov.pmrv.api.user.verifier.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.authorization.core.domain.Authority;
import uk.gov.pmrv.api.authorization.core.domain.dto.AuthorityInfoDTO;
import uk.gov.pmrv.api.authorization.verifier.service.VerifierAuthorityService;
import uk.gov.pmrv.api.user.core.domain.dto.InvitedUserEnableDTO;
import uk.gov.pmrv.api.user.core.domain.dto.UserInfoDTO;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;

@ExtendWith(MockitoExtension.class)
class VerifierUserAcceptInvitationServiceTest {

    @InjectMocks
    private VerifierUserAcceptInvitationService verifierUserAcceptInvitationService;

    @Mock
    private UserAuthService userAuthService;

    @Mock
    private VerifierAuthorityService verifierAuthorityService;

    @Mock
    private VerifierUserTokenVerificationService verifierUserTokenVerificationService;

    @Mock
    private VerifierUserNotificationGateway verifierUserNotificationGateway;

    @Test
    void acceptAndEnableVerifierInvitedUser() {
        String inviterUserId = "inviterUserId";
        InvitedUserEnableDTO invitedUserEnableDTO = InvitedUserEnableDTO.builder()
            .invitationToken("token")
            .password("password")
            .build();
        AuthorityInfoDTO authorityInfoDTO = AuthorityInfoDTO.builder()
            .id(1L)
            .userId("userId")
            .code("verifier")
            .verificationBodyId(1L)
            .build();
        UserInfoDTO invitee = UserInfoDTO.builder().userId(authorityInfoDTO.getUserId()).build();
        UserInfoDTO inviter = UserInfoDTO.builder().userId(inviterUserId).build();

        when(verifierUserTokenVerificationService
            .verifyInvitationTokenForPendingAuthority(invitedUserEnableDTO.getInvitationToken()))
                .thenReturn(authorityInfoDTO);
        when(verifierAuthorityService.acceptAuthority(authorityInfoDTO.getId()))
                .thenReturn(Authority.builder().createdBy(inviterUserId).build());
        when(userAuthService.getUserByUserId(authorityInfoDTO.getUserId()))
                .thenReturn(invitee);
        when(userAuthService.getUserByUserId(inviterUserId))
                .thenReturn(inviter);

        // Invoke
        verifierUserAcceptInvitationService.acceptAndEnableVerifierInvitedUser(invitedUserEnableDTO);

        // Verify
        verify(verifierUserTokenVerificationService, times(1))
                .verifyInvitationTokenForPendingAuthority(invitedUserEnableDTO.getInvitationToken());
        verify(verifierAuthorityService, times(1))
                .acceptAuthority(authorityInfoDTO.getId());
        verify(userAuthService, times(1))
                .getUserByUserId(authorityInfoDTO.getUserId());
        verify(verifierUserNotificationGateway, times(1))
                .notifyInviteeAcceptedInvitation(invitee);
        verify(userAuthService, times(1))
                .getUserByUserId(inviterUserId);
        verify(verifierUserNotificationGateway, times(1))
                .notifyInviterAcceptedInvitation(invitee, inviter);
    }

}