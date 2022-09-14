package uk.gov.pmrv.api.user.core.service.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.pmrv.api.common.exception.ErrorCode.USER_ALREADY_REGISTERED;
import static uk.gov.pmrv.api.user.core.domain.enumeration.AuthenticationStatus.DELETED;
import static uk.gov.pmrv.api.user.core.domain.enumeration.AuthenticationStatus.PENDING;
import static uk.gov.pmrv.api.user.core.domain.enumeration.AuthenticationStatus.REGISTERED;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.user.core.domain.enumeration.KeycloakUserAttributes;

@ExtendWith(MockitoExtension.class)
class UserRegistrationServiceTest {

    @InjectMocks
    UserRegistrationService userRegistrationService;

    @Mock
    AuthService authService;

    @Test
    void registerInvitedUser_new_user() {
        final String createdUserId = "user";
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setEmail("email");

        when(authService.getByUsername("email")).thenReturn(Optional.empty());
        when(authService.registerUserWithStatusPending(userRepresentation)).thenReturn(createdUserId);

        String actualUserId = userRegistrationService.registerInvitedUser(userRepresentation);

        assertThat(actualUserId).isEqualTo(createdUserId);
        verify(authService, never()).updateUserAndSetStatusAsPending(anyString(), any());
    }

    @Test
    void registerInvitedUser_existing_registered_user() {
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setEmail("email");

        UserRepresentation existingUserRepresentation = new UserRepresentation();
        existingUserRepresentation.setEmail("email");
        existingUserRepresentation.singleAttribute(KeycloakUserAttributes.USER_STATUS.getName(), REGISTERED.name());

        when(authService.getByUsername("email")).thenReturn(Optional.of(existingUserRepresentation));

        BusinessException businessException = assertThrows(BusinessException.class,
            () -> userRegistrationService.registerInvitedUser(existingUserRepresentation));

        assertThat(businessException.getErrorCode()).isEqualTo(USER_ALREADY_REGISTERED);
        verify(authService, never()).updateUserAndSetStatusAsPending(anyString(), any());
    }

    @Test
    void registerInvitedUser_existing_pending_user() {
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setEmail("email");

        UserRepresentation existingUserRepresentation = new UserRepresentation();
        existingUserRepresentation.setId("id");
        existingUserRepresentation.setEmail("email");
        existingUserRepresentation.singleAttribute(KeycloakUserAttributes.USER_STATUS.getName(), PENDING.name());

        when(authService.getByUsername("email")).thenReturn(Optional.of(existingUserRepresentation));

        String actualUserId = userRegistrationService.registerInvitedUser(userRepresentation);

        assertThat(actualUserId).isEqualTo(existingUserRepresentation.getId());
        verify(authService, times(1))
            .updateUserAndSetStatusAsPending(existingUserRepresentation.getId(), userRepresentation);
    }

    @Test
    void registerInvitedUser_existing_deleted_user() {
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setEmail("email");

        UserRepresentation existingUserRepresentation = new UserRepresentation();
        existingUserRepresentation.setId("id");
        existingUserRepresentation.setEmail("email");
        existingUserRepresentation.singleAttribute(KeycloakUserAttributes.USER_STATUS.getName(), DELETED.name());

        when(authService.getByUsername("email")).thenReturn(Optional.of(existingUserRepresentation));

        String actualUserId = userRegistrationService.registerInvitedUser(userRepresentation);

        assertThat(actualUserId).isEqualTo(existingUserRepresentation.getId());
        verify(authService, times(1))
            .updateUserAndSetStatusAsPending(existingUserRepresentation.getId(), userRepresentation);
    }
}