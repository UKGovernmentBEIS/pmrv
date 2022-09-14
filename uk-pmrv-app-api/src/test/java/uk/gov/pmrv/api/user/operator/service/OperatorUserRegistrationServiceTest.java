package uk.gov.pmrv.api.user.operator.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.service.AccountQueryService;
import uk.gov.pmrv.api.authorization.operator.service.OperatorAuthorityService;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserDTO;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserInvitationDTO;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserRegistrationWithCredentialsDTO;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OperatorUserRegistrationServiceTest {
	
    @InjectMocks
    private OperatorUserRegistrationService service;

    @Mock
    private OperatorUserAuthService operatorUserAuthService;
    
    @Mock
    private OperatorAuthorityService operatorAuthorityService;
    
    @Mock
    private OperatorUserTokenVerificationService operatorUserTokenVerificationService;
    
    @Mock
    private AccountQueryService accountQueryService;

    @Mock
    private OperatorUserNotificationGateway operatorUserNotificationGateway;
    
    @Test
    void registerUserToAccountWithStatusPending() {
        String roleCode = "roleCode";
        String userId = "userId";
        Long accountId = 1L;
        String accountName = "accountName";
        String authorityUuid = "authorityUuid";
        PmrvUser currentUser = PmrvUser.builder().userId("current_user_id").build();
        OperatorUserInvitationDTO operatorUserInvitationDTO = createOperatorUserInvitationDTO(roleCode);

        when(operatorUserAuthService.registerOperatorUserAsPending(
            operatorUserInvitationDTO.getEmail(),
            operatorUserInvitationDTO.getFirstName(),
            operatorUserInvitationDTO.getLastName()))
            .thenReturn(userId);
        when(operatorAuthorityService.createPendingAuthorityForOperator(accountId, roleCode, userId, currentUser))
            .thenReturn(authorityUuid);
        when(accountQueryService.getAccountInstallationName(accountId))
            .thenReturn(accountName);

        service.registerUserToAccountWithStatusPending(operatorUserInvitationDTO, accountId, currentUser);

        verify(operatorUserAuthService, times(1)).registerOperatorUserAsPending(
            operatorUserInvitationDTO.getEmail(),
            operatorUserInvitationDTO.getFirstName(),
            operatorUserInvitationDTO.getLastName());
        verify(operatorAuthorityService, times(1))
            .createPendingAuthorityForOperator(accountId, roleCode, userId, currentUser);
        verify(accountQueryService, times(1)).getAccountInstallationName(accountId);
        verify(operatorUserNotificationGateway, times(1)).notifyInvitedUser(
            operatorUserInvitationDTO,
            accountName,
            authorityUuid);
    }
    
    @Test
    void registerUser() {
    	String token = "token";
    	String email = "email";
    	OperatorUserRegistrationWithCredentialsDTO operatorUserRegistrationWithCredentialsDTO = OperatorUserRegistrationWithCredentialsDTO
            .builder().emailToken(token).build();
    	OperatorUserDTO userDTO = OperatorUserDTO.builder().firstName("fn").lastName("ln").email(email).build();
    	
    	when(operatorUserTokenVerificationService.verifyRegistrationToken(token)).thenReturn(email);
    	when(operatorUserAuthService.registerOperatorUser(operatorUserRegistrationWithCredentialsDTO, email))
    		.thenReturn(userDTO);
    	
    	//invoke
    	OperatorUserDTO result = service.registerUser(operatorUserRegistrationWithCredentialsDTO);
    	
    	//assert and verify
    	assertThat(result).isEqualTo(userDTO);
    	
    	verify(operatorUserTokenVerificationService, times(1)).verifyRegistrationToken(token);
    	verify(operatorUserAuthService, times(1)).registerOperatorUser(operatorUserRegistrationWithCredentialsDTO, email);
    	verify(operatorUserNotificationGateway, times(1)).notifyRegisteredUser(userDTO);
    }
    
    @Test
	void sendVerificationEmail() {
    	String email = "email";
    	
		// invoke
		service.sendVerificationEmail(email);

		// verify mocks
		verify(operatorUserNotificationGateway, times(1)).notifyEmailVerification(email);
	}

    private OperatorUserInvitationDTO createOperatorUserInvitationDTO(String roleCode) {
        return OperatorUserInvitationDTO.builder()
            .email("email")
            .roleCode(roleCode)
            .firstName("firstName")
            .lastName("lastName")
            .build();
    }

}