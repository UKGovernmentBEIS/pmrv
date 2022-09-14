package uk.gov.pmrv.api.user.core.service.auth;

import static uk.gov.pmrv.api.user.core.domain.enumeration.AuthenticationStatus.PENDING;
import static uk.gov.pmrv.api.user.core.domain.enumeration.AuthenticationStatus.REGISTERED;
import static uk.gov.pmrv.api.user.core.domain.enumeration.KeycloakUserAttributes.USER_STATUS;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.keycloak.adapters.springboot.KeycloakSpringBootProperties;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.files.common.domain.dto.FileDTO;
import uk.gov.pmrv.api.user.core.config.JwtProperties;
import uk.gov.pmrv.api.user.core.domain.enumeration.AuthenticationStatus;
import uk.gov.pmrv.api.user.core.domain.enumeration.KeycloakUserAttributes;
import uk.gov.pmrv.api.user.core.domain.model.UserDetails;
import uk.gov.pmrv.api.user.core.domain.model.UserDetailsRequest;
import uk.gov.pmrv.api.user.core.domain.model.UserInfo;

/**
 * Gateway to Keycloak related services.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final Keycloak keycloakAdminClient;
    private final KeycloakCustomClient keycloakCustomClient;
    private final KeycloakSpringBootProperties keycloakProperties;
    private final JwtProperties jwtProperties;
    private final Clock clock;

    public UserRepresentation getUserRepresentationById(String userId) {
        return getUsersResource().get(userId).toRepresentation();
    }
    
    public List<CredentialRepresentation> getUserCredentials(String userId) {
        return getUsersResource().get(userId).credentials();
    }

    /**
     * Finds the user in the Keycloak db with the provided username.
     * For our use case we have made the assumption that email is the same as username in keycloak
     * and we are searching by username because keycloak supports exact matches instead of fuzzy
     * searches when searching by email.
     * @param username the username based on which the search will be done
     * @return an Optional of {@link UserRepresentation}
     * @since v.0.1.0
     */
    public Optional<UserRepresentation> getByUsername(String username) {
        List<UserRepresentation> users = getUsersResource().search(username, true);
        return users.isEmpty() ? Optional.empty() : Optional.ofNullable(users.get(0));
    }
    
    /**
     * Finds the users in Keycloak db with the provided user ids.
     * @param userIds the user ids based on which the search will be done
     * @return list of UserInfo
     */
    public List<UserInfo> getUsers(List<String> userIds) {
        return keycloakCustomClient.getUsers(userIds);
    }

    /**
     * Finds the users in Keycloak db with the provided user ids.
     * @param userIds the user ids based on which the search will be done
     * @param attributesClazz Representation Object
     * @return List of users
     */
    public <T> List<T> getUsersWithAttributes(List<String> userIds, Class<T> attributesClazz) {
        return keycloakCustomClient.getUsersWithAttributes(userIds, attributesClazz);
    }
    
    public Optional<UserDetails> getUserDetails(String userId) {
        return keycloakCustomClient.getUserDetails(userId);
    }
    
    public Optional<FileDTO> getUserSignature(String signatureUuid) {
        return keycloakCustomClient.getUserSignature(signatureUuid);
    }

    /**
     * Generates a encrypted web token (JWT) using the provided subject and claim.
     * @param subject the value to be used as jwt subject
     * @param claimName the claim name to be added to the jwt
     * @param claimValue the claim value to be added to the jwt
     * @param expirationInterval the amount of time (in minutes) that the jwt will be valid
     * @return the generated web-token
     */
    public String generateToken(String subject, String claimName, String claimValue, long expirationInterval) {
        Algorithm algorithm = Algorithm.HMAC256((String) keycloakProperties.getCredentials().get("secret"));

        ZonedDateTime now = ZonedDateTime.now(clock);
        Date issued = Date.from(now.toInstant());
        Date expires = Date.from(now.plusMinutes(expirationInterval).toInstant());

        return JWT.create()
            .withIssuer(keycloakProperties.getAuthServerUrl())
            .withIssuedAt(issued)
            .withSubject(subject)
            .withClaim(claimName, claimValue)
            .withAudience(jwtProperties.getClaim().getAudience())
            .withExpiresAt(expires)
            .sign(algorithm);
    }

    /**
     * Verifies the token provided.
     * @param token the token to be verified
     * @return the decoded web-token
     * @throws BusinessException exception
     */
    public DecodedJWT verifyToken(String token, String subject) {
        Algorithm algorithm = Algorithm.HMAC256((String)keycloakProperties.getCredentials().get("secret"));
        JWTVerifier verifier = JWT.require(algorithm)
            .withIssuer(keycloakProperties.getAuthServerUrl())
            .withSubject(subject)
            .build();

        try {
            return verifier.verify(token);
        } catch (TokenExpiredException e) {
            throw new BusinessException(ErrorCode.VERIFICATION_LINK_EXPIRED);
        } catch (JWTVerificationException e) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }
    }

    /**
     * Edit provided user's profile using the provided terms version.
     * @param userId the provided user id
     * @param newTermsVersion the terms version
     */
    public void updateUserTerms(String userId, Short newTermsVersion) {
        UserRepresentation userRepresentation = getUserRepresentationById(userId);
        userRepresentation.singleAttribute(KeycloakUserAttributes.TERMS_VERSION.getName(), newTermsVersion.toString());
        updateUser(userRepresentation);
    }

    /**
     * Updates the user with the provided UserRepresentation.
     *
     * @param userRepresentation {@link UserRepresentation}
     */
    public void updateUser(UserRepresentation userRepresentation) {
    	getUsersResource().get(userRepresentation.getId()).update(userRepresentation);
    }
    
    /**
     * Updates the keycloak user identified by {@code userID} with the {@code userRepresentation}
     * and set the status to {@link AuthenticationStatus#PENDING}.
     * @param userId the keycloak user id
     * @param userRepresentation {@link UserRepresentation}
     */
    public void updateUserAndSetStatusAsPending(String userId, UserRepresentation userRepresentation) {
        setUserAsPending(userRepresentation);
        updateUser(userId, userRepresentation);
    }
    
    public void updateUserDetails(UserDetailsRequest userDetails) throws UserDetailsSaveException {
        try{
            keycloakCustomClient.saveUserDetails(userDetails);
        } catch (Exception e) {
            throw new UserDetailsSaveException(e);
        }
    }
    
    public void deleteUser(String userId) {
        getUsersResource().get(userId).remove();
    }

    public void disableUser(String userId) {
        UserRepresentation keycloakUser = getUserRepresentationById(userId);
        keycloakUser.setEnabled(false);
        keycloakUser.singleAttribute(KeycloakUserAttributes.USER_STATUS.getName(), AuthenticationStatus.DELETED.name());
        updateUser(keycloakUser);
    }

    /**
     * Enable PENDING user in keycloak and set REGISTERED status and password.
     *
     * @param userId the user id
     * @param userPassword the user password
     */
    public void enablePendingUser(String userId, String userPassword){
        setPasswordAndEnableUserBasedOnExpectedStatus(userId, userPassword, PENDING);
    }

    /**
     * Enable REGISTERED user in keycloak and set password.
     * @param userId the user id
     * @param userPassword the user password
     */
    public UserRepresentation enableRegisteredUser(String userId, String userPassword){
        return setPasswordAndEnableUserBasedOnExpectedStatus(userId, userPassword, REGISTERED);
    }

    /**
     * Registers a user in keycloak db.
     * @param userRepresentation {@link UserRepresentation}
     * @return the  registered user id
     */
    public String registerUser(UserRepresentation userRepresentation) {
        try (Response res = getUsersResource().create(userRepresentation)) {
            if (HttpStatus.valueOf(res.getStatus()) == HttpStatus.CREATED) {
                return CreatedResponseUtil.getCreatedId(res);
            } else {
                throw new BusinessException(ErrorCode.USER_REGISTRATION_FAILED_500);
            }
        }
    }

    /**
     * Registers a user in keycloak db with status {@link AuthenticationStatus#PENDING}.
     * @param userRepresentation userRepresentation {@link UserRepresentation}
     * @return the  registered user id
     */
    public String registerUserWithStatusPending(UserRepresentation userRepresentation) {
        setUserAsPending(userRepresentation);
        return registerUser(userRepresentation);
    }

    public void validateAuthenticatedUserOtp(String otp, String accessToken) {
        keycloakCustomClient.validateAuthenticatedUserOtp(otp, accessToken);
    }

    public void deleteOtpCredentials(String userId) {
        getUserCredentials(userId).stream()
                .filter(credentialRepresentation -> credentialRepresentation.getType().equals("otp")).findFirst()
                .ifPresent(optCredential -> getUsersResource().get(userId).removeCredential(optCredential.getId()));
    }

    private UserRepresentation setPasswordAndEnableUserBasedOnExpectedStatus(String userId, String password,
                                                               AuthenticationStatus expectedUserAuthenticationStatus) {
        UserRepresentation userRepresentation = getUserRepresentationById(userId);
        checkUserStatus(userRepresentation, expectedUserAuthenticationStatus);
        setUserPassword(userRepresentation, password);

        if (PENDING.equals(expectedUserAuthenticationStatus)) {
            setUserAsRegisteredAndEnabled(userRepresentation);
        } else if (REGISTERED.equals(expectedUserAuthenticationStatus)) {
            setUserAsEnabled(userRepresentation);
        }

        updateUser(userRepresentation);
        return userRepresentation;
    }

    private void checkUserStatus(UserRepresentation userRepresentation, AuthenticationStatus authenticationStatus) {
        if (!ObjectUtils.isEmpty(userRepresentation.getAttributes())
            && userRepresentation.getAttributes().containsKey(KeycloakUserAttributes.USER_STATUS.getName())
            && !userRepresentation.getAttributes().get(KeycloakUserAttributes.USER_STATUS.getName()).get(0)
            .equals(authenticationStatus.name())) {
            throw new BusinessException(ErrorCode.USER_INVALID_STATUS);
        }
    }

    /**
     * Set user's password in keycloak.
     * @param userRepresentation userRepresentation {@link UserRepresentation}
     * @param password the password
     */
    private void setUserPassword(UserRepresentation userRepresentation, String password) {
        CredentialRepresentation credentials = new CredentialRepresentation();
        credentials.setTemporary(false);
        credentials.setType(CredentialRepresentation.PASSWORD);
        credentials.setValue(password);
        userRepresentation.setCredentials(Collections.singletonList(credentials));
    }

    private void setUserAsPending(UserRepresentation userRepresentation) {
        userRepresentation.singleAttribute(USER_STATUS.getName(), PENDING.name());
        userRepresentation.setEnabled(false);
    }

    private void setUserAsRegisteredAndEnabled(UserRepresentation userRepresentation) {
        userRepresentation.singleAttribute(USER_STATUS.getName(), REGISTERED.name());
        userRepresentation.setEmailVerified(true);
        userRepresentation.setCreatedTimestamp(ZonedDateTime.now(clock).toInstant().toEpochMilli());
        setUserAsEnabled(userRepresentation);
    }

    private void setUserAsEnabled(UserRepresentation userRepresentation) {
        userRepresentation.setEnabled(true);
    }

    private UsersResource getUsersResource() {
    	return keycloakAdminClient.realm(keycloakProperties.getRealm()).users();
    }

    private void updateUser(String userId, UserRepresentation userRepresentation) {
        getUsersResource().get(userId).update(userRepresentation);
    }
}
