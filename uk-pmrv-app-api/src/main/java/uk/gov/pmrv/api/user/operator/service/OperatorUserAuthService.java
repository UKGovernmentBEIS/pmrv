package uk.gov.pmrv.api.user.operator.service;

import static org.keycloak.representations.idm.CredentialRepresentation.PASSWORD;
import static uk.gov.pmrv.api.user.core.domain.enumeration.KeycloakUserAttributes.USER_STATUS;

import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;
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

@Service
@RequiredArgsConstructor
public class OperatorUserAuthService {

	private final AuthService authService;
    private final OperatorUserMapper operatorUserMapper;
    private final OperatorUserRegistrationMapper operatorUserRegistrationMapper;
    private final Clock clock;

    public OperatorUserDTO getOperatorUserById(String userId) {
        return operatorUserMapper.toOperatorUserDTO(authService.getUserRepresentationById(userId));
    }

    /**
     * Registers a new user in keycloak with status {@link AuthenticationStatus#REGISTERED}
     * and {@link UserRepresentation#setEnabled} true.
     * @param operatorUserRegistrationWithCredentialsDTO {@link OperatorUserRegistrationWithCredentialsDTO}
     * @param email the user email
     * @return {@link OperatorUserDTO}
     */
    public OperatorUserDTO registerOperatorUser(OperatorUserRegistrationWithCredentialsDTO operatorUserRegistrationWithCredentialsDTO,
                                                String email) {
    	UserRepresentation userRepresentation =
            operatorUserRegistrationMapper.toUserRepresentation(operatorUserRegistrationWithCredentialsDTO, email);

    	setUserAsRegisteredAndEnabled(userRepresentation);
    	authService.registerUser(userRepresentation);
    	return operatorUserMapper.toOperatorUserDTO(userRepresentation);
    }

    /**
     * Registers a new user in keycloak with status {@link AuthenticationStatus#PENDING}
     * and {@link UserRepresentation#setEnabled} false.
     * @param email the user email
     * @param firstName the user first name
     * @param lastName the user last name
     * @return the  user id (from keycloak)
     */
    public String registerOperatorUserAsPending(String email, String firstName, String lastName) {
        UserRepresentation userRepresentation = operatorUserMapper.toUserRepresentation(email, firstName, lastName);
        return authService.registerUserWithStatusPending(userRepresentation);
    }

    /**
     * Updates an existing keycloak user with attributes of {@code updatedOperatorUserDTO}.
     * @param userId the user id
     * @param updatedOperatorUserDTO {@link OperatorUserDTO}
     */
    public void updateOperatorUser(String userId, OperatorUserDTO updatedOperatorUserDTO) {
        UserRepresentation registeredUser = authService.getUserRepresentationById(userId);
        UserRepresentation updatedUser = operatorUserMapper.toUserRepresentation(updatedOperatorUserDTO, userId,
                registeredUser.getUsername(), registeredUser.getEmail(), registeredUser.getAttributes());

        authService.updateUser(updatedUser);
    }

    /**
     * Updates the status of an existing {@link AuthenticationStatus#PENDING} keycloak user
     * to {@link AuthenticationStatus#REGISTERED} and {@link UserRepresentation#setEnabled} to true.
     * Moreover updates the user attributes using the {@code operatorUserRegistrationDTO}.
     * @param operatorUserRegistrationWithCredentialsDTO {@link OperatorUserRegistrationWithCredentialsDTO}
     * @param userId the user id
     * @return {@link OperatorUserDTO}
     */
    public OperatorUserDTO updatePendingOperatorUserToRegisteredAndEnabled(
        OperatorUserRegistrationWithCredentialsDTO operatorUserRegistrationWithCredentialsDTO, String userId) {

        UserRepresentation keycloakUser = authService.getUserRepresentationById(userId);

        // Validate if registered user can be enabled
        checkOperatorUserStatusIsPending(keycloakUser);

        UserRepresentation userRepresentation = operatorUserRegistrationMapper
                .toUserRepresentation(operatorUserRegistrationWithCredentialsDTO, keycloakUser.getEmail(), keycloakUser.getId());
        setUserAsRegisteredAndEnabled(userRepresentation);

        // Update user in keycloak
        authService.updateUser(userRepresentation);
        return operatorUserMapper.toOperatorUserDTO(userRepresentation);
    }

    /**
     * Updates the status of an existing {@link AuthenticationStatus#PENDING} keycloak user
     * to {@link AuthenticationStatus#REGISTERED} while {@link UserRepresentation#setEnabled} remains false.
     * Moreover updates the user attributes using the {@code operatorUserRegistrationDTO}.
     * @param operatorUserRegistrationDTO {@link OperatorUserRegistrationDTO}
     * @param userId the user id
     * @return {@link OperatorUserDTO}
     */
    public OperatorUserDTO updatePendingOperatorUserToRegistered(
        OperatorUserRegistrationDTO operatorUserRegistrationDTO, String userId) {

        UserRepresentation keycloakUser = authService.getUserRepresentationById(userId);

        // Validate if registered user can be enabled
        checkOperatorUserStatusIsPending(keycloakUser);

        UserRepresentation userRepresentation = operatorUserRegistrationMapper
            .toUserRepresentation(operatorUserRegistrationDTO, keycloakUser.getEmail(), keycloakUser.getId());

        setUserAsRegistered(userRepresentation);
        authService.updateUser(userRepresentation);
        return operatorUserMapper.toOperatorUserDTO(userRepresentation);
    }

    /**
     * Enables an existing {@link AuthenticationStatus#REGISTERED} keycloak user and sets the ({@code password}).
     * @param userId the user id
     * @param password the user password
     * @return {@link OperatorUserDTO}
     */
    public OperatorUserDTO updateRegisteredOperatorUserToEnabled(String userId, String password) {
        UserRepresentation userRepresentation = authService.enableRegisteredUser(userId, password);
        return operatorUserMapper.toOperatorUserDTO(userRepresentation);
    }

    /**
     * Updates the keycloak user identified by the {@code userId} using the properties
     * provided in {@code operatorUserInvitation}
     * and set the status to {@link AuthenticationStatus#PENDING}.
     * @param userId the user id
     * @param operatorUserInvitation {@link OperatorUserInvitationDTO}
     */
    public void updateOperatorUserToPending(String userId, OperatorUserInvitationDTO operatorUserInvitation) {
        UserRepresentation userRepresentation = operatorUserMapper.toUserRepresentation(operatorUserInvitation.getEmail(),
            operatorUserInvitation.getFirstName(), operatorUserInvitation.getLastName());
        authService.updateUserAndSetStatusAsPending(userId, userRepresentation);
    }

    /**
     * Returns whether a password has been set in keycloak for the {@code userId}.
     * @param userId the user id
     * @return true/false
     */
    public boolean hasOperatorUserPassword(String userId) {
        List<CredentialRepresentation> userCredentials = authService.getUserCredentials(userId);
        return !userCredentials.isEmpty() && userCredentials.stream().anyMatch(cr -> PASSWORD.equals(cr.getType()));
    }
    
    private void setUserAsRegisteredAndEnabled(UserRepresentation userRepresentation) {
        setUserAsRegistered(userRepresentation);
        userRepresentation.setEnabled(true);
    }

    private void setUserAsRegistered(UserRepresentation userRepresentation) {
        userRepresentation.singleAttribute(USER_STATUS.getName(), AuthenticationStatus.REGISTERED.name());
        userRepresentation.setEmailVerified(true);
        userRepresentation.setCreatedTimestamp(ZonedDateTime.now(clock).toInstant().toEpochMilli());
    }

    private void checkOperatorUserStatusIsPending(UserRepresentation keycloakUser) {
        if(keycloakUser.getAttributes().containsKey(KeycloakUserAttributes.USER_STATUS.getName())
            && !keycloakUser.getAttributes().get(KeycloakUserAttributes.USER_STATUS.getName()).get(0)
            .equals(AuthenticationStatus.PENDING.name())){
            throw new BusinessException(ErrorCode.AUTHORITY_USER_UPDATE_NON_PENDING_AUTHORITY_NOT_ALLOWED);
        }
    }
    
}
