package uk.gov.pmrv.api.common.domain.provider;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;

import java.util.List;

/**
 * The rest end point interface for creating external API references.
 */
public interface RestEndPoint {

    /**
     * The rest end point (url).
     *
     * @return Rest end point
     */
    String getEndPoint();

    /**
     * The Http method of external request.
     *
     * @return {@link HttpMethod}
     */
    HttpMethod getMethod();

    /**
     * The List of parameters or path variable values.
     *
     * @return List of parameters
     */
    List<String> getParameters();

    /**
     * The expected type of the response body.
     *
     * @return {@link ParameterizedTypeReference}
     */
    ParameterizedTypeReference getParameterizedTypeReference();
}
