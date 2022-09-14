package uk.gov.pmrv.api.user.core.domain.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import uk.gov.pmrv.api.common.domain.provider.RestEndPoint;
import uk.gov.pmrv.api.user.core.domain.model.keycloak.KeycloakUserDetails;
import uk.gov.pmrv.api.user.core.domain.model.keycloak.KeycloakUserInfo;
import uk.gov.pmrv.api.user.core.domain.model.keycloak.KeycloakSignature;

import java.util.ArrayList;
import java.util.List;

/**
 * The Keycloak client rest points enum.
 */
@Getter
@AllArgsConstructor
public enum KeycloakRestEndPointEnum implements RestEndPoint {

    /** Return users registered in Keycloak. */
    KEYCLOAK_GET_USERS("/users", HttpMethod.POST, new ParameterizedTypeReference<List<KeycloakUserInfo>>() {}, new ArrayList<>()),
    KEYCLOAK_GET_USER_DETAILS("/users/user/details", HttpMethod.GET, new ParameterizedTypeReference<KeycloakUserDetails>() {}, null),
    KEYCLOAK_POST_USER_DETAILS("/users/user/details", HttpMethod.POST, new ParameterizedTypeReference<Void>() {}, null),
    KEYCLOAK_GET_USER_SIGNATURE("/users/user/signature", HttpMethod.GET, new ParameterizedTypeReference<KeycloakSignature>() {}, null),
    KEYCLOAK_VALIDATE_OTP("/users/otp/validation", HttpMethod.POST, new ParameterizedTypeReference<Void>() {}, null)
    ;

    /** The url. */
    private final String endPoint;

    /** The {@link HttpMethod}. */
    private final HttpMethod method;

    /** The {@link ParameterizedTypeReference}. */
    private final ParameterizedTypeReference<?> parameterizedTypeReference;

    /** The List of parameters or path variable values. */
    private final List<String> parameters;
}
