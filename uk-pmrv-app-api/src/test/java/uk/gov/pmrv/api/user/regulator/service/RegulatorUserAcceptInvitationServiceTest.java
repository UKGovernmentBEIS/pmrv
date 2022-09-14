package uk.gov.pmrv.api.user.regulator.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.authorization.core.domain.Authority;
import uk.gov.pmrv.api.authorization.core.domain.AuthorityStatus;
import uk.gov.pmrv.api.authorization.core.domain.dto.AuthorityInfoDTO;
import uk.gov.pmrv.api.authorization.regulator.service.RegulatorAuthorityService;
import uk.gov.pmrv.api.user.core.domain.dto.InvitedUserEnableDTO;
import uk.gov.pmrv.api.user.core.domain.dto.UserInfoDTO;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;

@ExtendWith(MockitoExtension.class)
class RegulatorUserAcceptInvitationServiceTest {

    @InjectMocks
    private RegulatorUserAcceptInvitationService regulatorUserAcceptInvitationService;

    @Mock
    private UserAuthService userAuthService;

    @Mock
    private RegulatorUserTokenVerificationService regulatorUserTokenVerificationService;

    @Mock
    private RegulatorAuthorityService regulatorAuthorityService;

    @Mock
    private RegulatorUserNotificationGateway regulatorUserNotificationGateway;


    @Test
    void acceptAndEnableRegulatorInvitedUser_whenNoExceptions_thenFlowCompletes() {
        InvitedUserEnableDTO invitedUserEnableDTO = InvitedUserEnableDTO.builder()
            .invitationToken("invitationToken")
            .password("password")
            .build();

        AuthorityInfoDTO authorityInfo = AuthorityInfoDTO.builder()
            .id(1L)
            .authorityStatus(AuthorityStatus.PENDING)
            .userId("userId")
            .build();

        final UserInfoDTO invitee = UserInfoDTO.builder().firstName("invitee").email("email").build();
        final UserInfoDTO inviter = UserInfoDTO.builder().firstName("inviter").build();

        when(regulatorUserTokenVerificationService.verifyInvitationTokenForPendingAuthority(
            invitedUserEnableDTO.getInvitationToken()))
            .thenReturn(authorityInfo);
        when(regulatorAuthorityService.acceptAuthority(1L)).thenReturn(
            Authority.builder().createdBy("creator").build());
        when(userAuthService.getUserByUserId("userId")).thenReturn(invitee);
        when(userAuthService.getUserByUserId("creator")).thenReturn(inviter);

        regulatorUserAcceptInvitationService.acceptAndEnableRegulatorInvitedUser(invitedUserEnableDTO);

        verify(regulatorUserTokenVerificationService, times(1))
            .verifyInvitationTokenForPendingAuthority(invitedUserEnableDTO.getInvitationToken());
        verify(regulatorAuthorityService, times(1)).acceptAuthority(authorityInfo.getId());
        verify(userAuthService, times(1)).enablePendingUser("userId", "password");
        verify(userAuthService, times(1)).getUserByUserId("userId");
        verify(userAuthService, times(1)).getUserByUserId("creator");
        verify(regulatorUserNotificationGateway, times(1)).notifyInviteeAcceptedInvitation("email");
        verify(regulatorUserNotificationGateway, times(1)).notifyInviterAcceptedInvitation(invitee, inviter);
    }

}