package uk.gov.pmrv.api.user.core.domain.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import uk.gov.pmrv.api.common.domain.provider.RestEndPoint;

import java.util.List;

/**
 * The Pwned passwords rest points enum.
 */
@Getter
@AllArgsConstructor
public enum RestEndPointEnum implements RestEndPoint {

    /** Protect the value of the source password being searched for. */
    PWNED_PASSWORDS("/range/{passwordHash}", HttpMethod.GET, new ParameterizedTypeReference<String>() {}, List.of("passwordHash"));

    /** The url. */
    private final String endPoint;

    /** The {@link HttpMethod}. */
    private final HttpMethod method;

    /** The {@link ParameterizedTypeReference}. */
    private final ParameterizedTypeReference<?> parameterizedTypeReference;

    /** The List of parameters or path variable values. */
    private final List<String> parameters;
}
