package uk.gov.pmrv.api.common.domain.provider;

import lombok.Builder;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;

/**
 * A generic Rest API for external rest calls.
 */
@SuppressWarnings("unchecked")
@Setter
@Builder
@Log4j2
public class PMRVRestApi {

    /** The API base URL. */
    private String baseUrl;

    /** The {@link UriComponents}. */
    private UriComponents uri;

    /** The {@link RestEndPoint}. */
    private RestEndPoint restEndPoint;

    /** The {@link HttpHeaders}. */
    private HttpHeaders headers;

    /** The {@link RestTemplate} */
    private RestTemplate restTemplate;

    /** The request parameters */
    private List<Object> requestParams;

    /** The body */
    private Object body;

    /**
     * Creates the API call and returns appropriate response.
     *
     * @param <T> The generic modifier
     * @return @link ResponseEntity}
     */
    public <T> ResponseEntity<T> performApiCall() {

        /* Create Header */
        HttpEntity<Object> header = this.body != null ? new HttpEntity<>(this.body, this.headers) : new HttpEntity<>(this.headers);

        /* Create URI */
        String restPoint = this.uri != null ? uri.toString() : this.baseUrl + this.restEndPoint.getEndPoint();

        /* Create Parameters */
        Map<String, Object> parameters = new HashMap<>();
        Optional.ofNullable(this.requestParams).ifPresent(params ->
            IntStream.range(0, params.size()).forEach(index ->
                    parameters.put(this.restEndPoint.getParameters().get(index), params.get(index)))
        );

        /* Create Request and get Response */
        try {
            return this.restTemplate.exchange(restPoint, this.restEndPoint.getMethod(),
                    header, this.restEndPoint.getParameterizedTypeReference(), parameters);
        }
        catch (Exception ex){
            /* If an exception is raised log the calling request */
            log.error("Failed to invoke External API Rest with url '{}' and params '{}'", () -> restPoint, () -> parameters);
            throw ex;
        }
    }
}
