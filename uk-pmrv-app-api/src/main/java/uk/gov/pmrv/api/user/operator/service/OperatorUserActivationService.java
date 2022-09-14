package uk.gov.pmrv.api.user.operator.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.authorization.core.domain.dto.AuthorityInfoDTO;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserDTO;
import uk.gov.pmrv.api.user.core.domain.dto.InvitedUserEnableDTO;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserRegistrationDTO;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserRegistrationWithCredentialsDTO;

@Service
@RequiredArgsConstructor
public class OperatorUserActivationService {

    private final OperatorUserAuthService operatorUserAuthService;
    private final OperatorUserTokenVerificationService operatorUserTokenVerificationService;
    private final OperatorUserNotificationGateway operatorUserNotificationGateway;

    public OperatorUserDTO activateAndEnableOperatorInvitedUser(
        OperatorUserRegistrationWithCredentialsDTO operatorUserRegistrationWithCredentialsDTO) {
        // Get user's authority
        AuthorityInfoDTO authority = operatorUserTokenVerificationService
            .verifyInvitationTokenForPendingAuthority(operatorUserRegistrationWithCredentialsDTO.getEmailToken());

        // Activate and update User
        OperatorUserDTO operatorUserDTO = operatorUserAuthService
            .updatePendingOperatorUserToRegisteredAndEnabled(operatorUserRegistrationWithCredentialsDTO, authority.getUserId());

        // Send notification email
        operatorUserNotificationGateway.notifyRegisteredUser(operatorUserDTO);

        return operatorUserDTO;
    }

    public OperatorUserDTO activateOperatorInvitedUser(OperatorUserRegistrationDTO operatorUserRegistrationDTO) {
        AuthorityInfoDTO authority = operatorUserTokenVerificationService
            .verifyInvitationTokenForPendingAuthority(operatorUserRegistrationDTO.getEmailToken());

        return operatorUserAuthService
            .updatePendingOperatorUserToRegistered(operatorUserRegistrationDTO, authority.getUserId());
    }

    public void enableOperatorInvitedUser(InvitedUserEnableDTO invitedUserEnableDTO) {
        AuthorityInfoDTO authority = operatorUserTokenVerificationService
            .verifyInvitationTokenForPendingAuthority(invitedUserEnableDTO.getInvitationToken());

        OperatorUserDTO operatorUserDTO = operatorUserAuthService
            .updateRegisteredOperatorUserToEnabled(authority.getUserId(), invitedUserEnableDTO.getPassword());

        operatorUserNotificationGateway.notifyRegisteredUser(operatorUserDTO);
    }
}
