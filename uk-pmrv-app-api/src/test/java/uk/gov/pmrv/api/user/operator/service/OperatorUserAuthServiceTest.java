package uk.gov.pmrv.api.user.operator.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.user.core.domain.enumeration.AuthenticationStatus;
import uk.gov.pmrv.api.user.core.domain.enumeration.KeycloakUserAttributes;
import uk.gov.pmrv.api.user.core.service.auth.AuthService;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserDTO;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserInvitationDTO;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserRegistrationDTO;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserRegistrationWithCredentialsDTO;
import uk.gov.pmrv.api.user.operator.transform.OperatorUserMapper;
import uk.gov.pmrv.api.user.operator.transform.OperatorUserRegistrationMapper;

@ExtendWith(MockitoExtension.class)
class OperatorUserAuthServiceTest {

	@InjectMocks
    private OperatorUserAuthService service;

	@Mock
	private AuthService authService;

	@Mock
	private OperatorUserMapper operatorUserMapper;

	@Mock
	private OperatorUserRegistrationMapper operatorUserRegistrationMapper;

	@Spy
	private Clock fixedClock = Clock.fixed(Instant.now(), ZoneId.of("UTC"));

	@Test
	void getOperatorUserById() {
		String email = "email";
		String firstName = "firstName";
		String lastName = "lastName";
		String userId = "userId";
		OperatorUserDTO operatorUserDTO =
				OperatorUserDTO.builder().email(email).firstName(firstName).lastName(lastName).build();
		UserRepresentation userRepresentation = createUserRepresentation(userId, email, "username");

		when(authService.getUserRepresentationById(userId)).thenReturn(userRepresentation);
		when(operatorUserMapper.toOperatorUserDTO(userRepresentation)).thenReturn(operatorUserDTO);

		//invoke
		OperatorUserDTO result = service.getOperatorUserById(userId);

		assertThat(result).isEqualTo(operatorUserDTO);
		verify(authService, times(1)).getUserRepresentationById(userId);
		verify(operatorUserMapper, times(1)).toOperatorUserDTO(userRepresentation);
	}

	@Test
	void registerOperatorUser() {
		String email = "email";
		String firstName = "firstName";
		String lastName = "lastName";
		OperatorUserRegistrationWithCredentialsDTO userRegistrationDTO =
				OperatorUserRegistrationWithCredentialsDTO.builder().build();
		OperatorUserDTO operatorUserDTO =
				OperatorUserDTO.builder().email(email).firstName(firstName).lastName(lastName).build();

		UserRepresentation userRepresentation = new UserRepresentation();
		userRepresentation.setEmail(email);
		userRepresentation.setFirstName(firstName);
		userRepresentation.setLastName(lastName);

		// mock
		when(operatorUserRegistrationMapper.toUserRepresentation(userRegistrationDTO, email)).thenReturn(userRepresentation);
		when(operatorUserMapper.toOperatorUserDTO(userRepresentation)).thenReturn(operatorUserDTO);

		// invoke
		OperatorUserDTO actualUser = service.registerOperatorUser(userRegistrationDTO, email);

		//assert
		assertThat(actualUser).isEqualTo(operatorUserDTO);
		assertThat(userRepresentation.isEnabled()).isTrue();
		assertThat(userRepresentation.isEmailVerified()).isTrue();
		assertThat(userRepresentation.getAttributes().get(KeycloakUserAttributes.USER_STATUS.getName()).get(0))
				.isEqualTo(AuthenticationStatus.REGISTERED.name());

		// verify mocks
		verify(operatorUserRegistrationMapper, times(1)).toUserRepresentation(userRegistrationDTO, email);
		verify(authService, times(1)).registerUser(userRepresentation);
		verify(operatorUserMapper, times(1)).toOperatorUserDTO(userRepresentation);
	}

	@Test
	void registerOperatorUserAsPending() {
		String email = "email";
		String firstName = "firstName";
		String lastName = "lastName";
		UserRepresentation userRepresentation = new UserRepresentation();
		userRepresentation.setEmail(email);
		userRepresentation.setFirstName(firstName);
		userRepresentation.setLastName(lastName);

		// mock
		when(operatorUserMapper.toUserRepresentation(email, firstName, lastName)).thenReturn(userRepresentation);
		when(authService.registerUserWithStatusPending(userRepresentation)).thenReturn("user");
		// invoke
		String actualUserId = service.registerOperatorUserAsPending(email, firstName, lastName);

		//assert
		assertThat(actualUserId).isEqualTo("user");

		// verify mocks
		verify(operatorUserMapper, times(1)).toUserRepresentation(email, firstName, lastName);
		verify(authService, times(1)).registerUserWithStatusPending(userRepresentation);
	}

	@Test
	void updateOperatorUser() {
		String userId = "user";
		String username = "username";
		UserRepresentation userRepresentation = createUserRepresentation(userId, "email1", username);
		UserRepresentation userRepresentationUpdated = createUserRepresentation(userId, "email2", username);

		OperatorUserDTO operatorUserDTO =
				OperatorUserDTO.builder().email("email2").firstName("fn").lastName("ln").build();

		when(authService.getUserRepresentationById(userId)).thenReturn(userRepresentation);
		when(operatorUserMapper.toUserRepresentation(operatorUserDTO, userId, userRepresentation.getUsername(),
				userRepresentation.getEmail(), userRepresentation.getAttributes())).thenReturn(userRepresentationUpdated);

		//invoke
		service.updateOperatorUser(userId, operatorUserDTO);

		verify(authService, times(1)).getUserRepresentationById(userId);
		verify(operatorUserMapper, times(1)).toUserRepresentation(operatorUserDTO, userId,
				userRepresentation.getUsername(), userRepresentation.getEmail(), userRepresentation.getAttributes());
		verify(authService, times(1)).updateUser(userRepresentationUpdated);
	}

	@Test
	void updatePendingOperatorUserToRegisteredAndEnabled() {
		String userId = "userId";
		String email = "email";

		UserRepresentation userRepresentation = createUserRepresentation(userId, email, email);
		userRepresentation.singleAttribute(KeycloakUserAttributes.USER_STATUS.getName(), AuthenticationStatus.PENDING.name());
		OperatorUserRegistrationWithCredentialsDTO
            userRegistrationDTO = OperatorUserRegistrationWithCredentialsDTO.builder().build();

		// Mock
		when(authService.getUserRepresentationById(userId)).thenReturn(userRepresentation);
		when(operatorUserRegistrationMapper.toUserRepresentation(userRegistrationDTO, userRepresentation.getEmail(), userRepresentation.getId()))
				.thenReturn(userRepresentation);

		// Invoke
		service.updatePendingOperatorUserToRegisteredAndEnabled(userRegistrationDTO, userId);

		// Verify
		assertThat(userRepresentation.isEnabled()).isTrue();
		assertThat(userRepresentation.isEmailVerified()).isTrue();
		assertThat(userRepresentation.getAttributes().get(KeycloakUserAttributes.USER_STATUS.getName()).get(0))
				.isEqualTo(AuthenticationStatus.REGISTERED.name());
		verify(authService, times(1)).getUserRepresentationById(userId);
		verify(operatorUserRegistrationMapper, times(1))
				.toUserRepresentation(userRegistrationDTO, userRepresentation.getEmail(), userRepresentation.getId());
		verify(authService, times(1)).updateUser(userRepresentation);
		verify(operatorUserMapper, times(1)).toOperatorUserDTO(userRepresentation);
	}

	@Test
	void updatePendingOperatorUserToRegisteredAndEnabled_not_pending() {
		String userId = "userId";
		String email = "email";

		UserRepresentation userRepresentation = createUserRepresentation(userId, email, email);
		userRepresentation.singleAttribute(KeycloakUserAttributes.USER_STATUS.getName(), AuthenticationStatus.REGISTERED.name());
		OperatorUserRegistrationWithCredentialsDTO
            userRegistrationDTO = OperatorUserRegistrationWithCredentialsDTO.builder().build();

		// Mock
		when(authService.getUserRepresentationById(userId)).thenReturn(userRepresentation);

		// Invoke
		BusinessException exception = assertThrows(BusinessException.class,
				() -> service.updatePendingOperatorUserToRegisteredAndEnabled(userRegistrationDTO, userId));

		// Verify
		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.AUTHORITY_USER_UPDATE_NON_PENDING_AUTHORITY_NOT_ALLOWED);
		verify(authService, times(1)).getUserRepresentationById(userId);
		verify(operatorUserRegistrationMapper, never()).toUserRepresentation(any(),anyString(), anyString());
		verify(authService, never()).updateUser(any());
		verify(operatorUserMapper, never()).toOperatorUserDTO(any());
	}

	@Test
    void updatePendingOperatorUserToRegistered() {
        String userId = "userId";
        String email = "email";

        UserRepresentation userRepresentation = createUserRepresentation(userId, email, email);
        userRepresentation.singleAttribute(KeycloakUserAttributes.USER_STATUS.getName(), AuthenticationStatus.PENDING.name());
        OperatorUserRegistrationDTO userRegistrationDTO = OperatorUserRegistrationDTO.builder().build();

        when(authService.getUserRepresentationById(userId)).thenReturn(userRepresentation);
        when(operatorUserRegistrationMapper.toUserRepresentation(userRegistrationDTO, userRepresentation.getEmail(), userRepresentation.getId()))
            .thenReturn(userRepresentation);

        //invoke
        service.updatePendingOperatorUserToRegistered(userRegistrationDTO, userId);

        //verify
        assertFalse(userRepresentation.isEnabled());
        assertTrue(userRepresentation.isEmailVerified());
        assertThat(userRepresentation.getAttributes().get(KeycloakUserAttributes.USER_STATUS.getName()).get(0))
            .isEqualTo(AuthenticationStatus.REGISTERED.name());
        verify(authService, times(1)).getUserRepresentationById(userId);
        verify(operatorUserRegistrationMapper, times(1))
            .toUserRepresentation(userRegistrationDTO, userRepresentation.getEmail(), userRepresentation.getId());
        verify(authService, times(1)).updateUser(userRepresentation);
        verify(operatorUserMapper, times(1)).toOperatorUserDTO(userRepresentation);
    }

    @Test
    void updatePendingOperatorUserToRegistered_user_not_in_pending_status() {
        String userId = "userId";
        String email = "email";

        UserRepresentation userRepresentation = createUserRepresentation(userId, email, email);
        userRepresentation.singleAttribute(KeycloakUserAttributes.USER_STATUS.getName(), AuthenticationStatus.REGISTERED.name());
        OperatorUserRegistrationDTO userRegistrationDTO = OperatorUserRegistrationDTO.builder().build();

        when(authService.getUserRepresentationById(userId)).thenReturn(userRepresentation);

        //invoke
        BusinessException exception = assertThrows(BusinessException.class,
            () -> service.updatePendingOperatorUserToRegistered(userRegistrationDTO, userId));

        // Verify
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.AUTHORITY_USER_UPDATE_NON_PENDING_AUTHORITY_NOT_ALLOWED);
        verify(authService, times(1)).getUserRepresentationById(userId);
        verify(operatorUserRegistrationMapper, never()).toUserRepresentation(any(),anyString(), anyString());
        verify(authService, never()).updateUser(any());
        verify(operatorUserMapper, never()).toOperatorUserDTO(any());
    }
    @Test
    void enableRegisteredOperatorInvitedUser() {
        String userId = "userId";
        String email = "email";
        String password = "password";

        UserRepresentation userRepresentation = createUserRepresentation(userId, email, email);

        when(authService.enableRegisteredUser(userId, password)).thenReturn(userRepresentation);

	    service.updateRegisteredOperatorUserToEnabled(userId, password);

	    verify(authService, times(1)).enableRegisteredUser(userId, password);
	    verify(operatorUserMapper, times(1)).toOperatorUserDTO(userRepresentation);
    }

   @Test
    void hasOperatorUserPassword_true() {
	    String userId = "userId";

        CredentialRepresentation credentials = new CredentialRepresentation();
        credentials.setType(CredentialRepresentation.PASSWORD);
        credentials.setValue("password");

        when(authService.getUserCredentials(userId)).thenReturn(List.of(credentials));

	    assertTrue(service.hasOperatorUserPassword(userId));
    }

    @Test
    void hasOperatorUserPassword_false() {
        String userId = "userId";

        CredentialRepresentation credentials = new CredentialRepresentation();
        credentials.setType(CredentialRepresentation.SECRET);
        credentials.setValue("password");

        when(authService.getUserCredentials(userId)).thenReturn(List.of(credentials));

        assertFalse(service.hasOperatorUserPassword(userId));
    }

    @Test
    void hasOperatorUserPassword_no_credentials() {
        String userId = "userId";

        when(authService.getUserCredentials(userId)).thenReturn(Collections.emptyList());

        assertFalse(service.hasOperatorUserPassword(userId));
    }

    @Test
    void updateOperatorUserToPending() {
        String userId = "userId";
        String email = "email";
        String firstName = "firstName";
        String lastName = "lastName";
        OperatorUserInvitationDTO operatorUserInvitation = OperatorUserInvitationDTO.builder()
            .email(email)
            .firstName(firstName)
            .lastName(lastName)
            .build();
        UserRepresentation userRepresentation = new UserRepresentation();

        when(operatorUserMapper.toUserRepresentation(email, firstName, lastName)).thenReturn(userRepresentation);

        service.updateOperatorUserToPending(userId, operatorUserInvitation);

        verify(operatorUserMapper, times(1)).toUserRepresentation(email, firstName, lastName);
        verify(authService, times(1)).updateUserAndSetStatusAsPending(userId, userRepresentation);
    }

	private UserRepresentation createUserRepresentation(String id, String email, String username) {
		UserRepresentation user = new UserRepresentation();
		user.setId(id);
		user.setEmail(email);
		user.setUsername(username);
		user.setEnabled(false);
		return user;
	}

}
