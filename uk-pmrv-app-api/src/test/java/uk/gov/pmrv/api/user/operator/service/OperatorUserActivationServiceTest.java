package uk.gov.pmrv.api.user.operator.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.authorization.core.domain.dto.AuthorityInfoDTO;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserDTO;
import uk.gov.pmrv.api.user.core.domain.dto.InvitedUserEnableDTO;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserRegistrationDTO;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserRegistrationWithCredentialsDTO;

@ExtendWith(MockitoExtension.class)
class OperatorUserActivationServiceTest {

    @InjectMocks
    private OperatorUserActivationService service;

    @Mock
    private OperatorUserAuthService operatorUserAuthService;

    @Mock
    private OperatorUserTokenVerificationService operatorUserTokenVerificationService;

    @Mock
    private OperatorUserNotificationGateway operatorUserNotificationGateway;

    @Test
    void activateAndEnableOperatorInvitedUser() {
        String userId = "userId";
        String token = "token";
        String email = "email";
        OperatorUserRegistrationWithCredentialsDTO userRegistrationDTO = OperatorUserRegistrationWithCredentialsDTO.builder()
            .emailToken(token).build();
        AuthorityInfoDTO authority = AuthorityInfoDTO.builder().userId(userId).build();
        OperatorUserDTO userDTO = OperatorUserDTO.builder().email(email).build();

        // Mock
        when(operatorUserTokenVerificationService.verifyInvitationTokenForPendingAuthority(token))
            .thenReturn(authority);
        when(operatorUserAuthService.updatePendingOperatorUserToRegisteredAndEnabled(userRegistrationDTO, userId))
            .thenReturn(userDTO);

        // Invoke
        service.activateAndEnableOperatorInvitedUser(userRegistrationDTO);

        // Verify
        verify(operatorUserTokenVerificationService, times(1))
            .verifyInvitationTokenForPendingAuthority(userRegistrationDTO.getEmailToken());
        verify(operatorUserAuthService, times(1))
            .updatePendingOperatorUserToRegisteredAndEnabled(userRegistrationDTO, authority.getUserId());
        verify(operatorUserNotificationGateway, times(1))
            .notifyRegisteredUser(userDTO);
    }

    @Test
    void activateOperatorInvitedUser() {
        String userId = "userId";
        String token = "token";
        String email = "email";
        OperatorUserRegistrationDTO userRegistrationDTO = OperatorUserRegistrationDTO.builder().emailToken(token).build();
        AuthorityInfoDTO authority = AuthorityInfoDTO.builder().userId(userId).build();
        OperatorUserDTO userDTO = OperatorUserDTO.builder().email(email).build();

        when(operatorUserTokenVerificationService.verifyInvitationTokenForPendingAuthority(token))
            .thenReturn(authority);
        when(operatorUserAuthService.updatePendingOperatorUserToRegistered(userRegistrationDTO, userId))
            .thenReturn(userDTO);

        //invoke
        service.activateOperatorInvitedUser(userRegistrationDTO);

        //verify
        verify(operatorUserTokenVerificationService, times(1))
            .verifyInvitationTokenForPendingAuthority(userRegistrationDTO.getEmailToken());
        verify(operatorUserAuthService, times(1))
            .updatePendingOperatorUserToRegistered(userRegistrationDTO, authority.getUserId());
    }

    @Test
    void enableOperatorInvitedUser() {
        InvitedUserEnableDTO invitedUserEnableDTO = InvitedUserEnableDTO.builder()
            .invitationToken("token")
            .password("password")
            .build();
        AuthorityInfoDTO authority = AuthorityInfoDTO.builder().userId("userId").build();
        OperatorUserDTO userDTO = OperatorUserDTO.builder().email("email").build();

        when(operatorUserTokenVerificationService
            .verifyInvitationTokenForPendingAuthority(invitedUserEnableDTO.getInvitationToken()))
            .thenReturn(authority);
        when(operatorUserAuthService
            .updateRegisteredOperatorUserToEnabled(authority.getUserId(), invitedUserEnableDTO.getPassword()))
            .thenReturn(userDTO);

        service.enableOperatorInvitedUser(invitedUserEnableDTO);

        verify(operatorUserTokenVerificationService, times(1))
            .verifyInvitationTokenForPendingAuthority(invitedUserEnableDTO.getInvitationToken());
        verify(operatorUserAuthService, times(1))
            .updateRegisteredOperatorUserToEnabled(authority.getUserId(), invitedUserEnableDTO.getPassword());
        verify(operatorUserNotificationGateway, times(1)).notifyRegisteredUser(userDTO);
    }
}