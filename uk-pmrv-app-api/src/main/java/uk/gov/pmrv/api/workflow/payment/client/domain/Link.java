package uk.gov.pmrv.api.workflow.payment.client.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.*;
import lombok.experimental.SuperBuilder;

import org.springframework.http.HttpMethod;

/**
 * The link related to a payment of GOV.UK Pay.
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Link {

    /** The href. */
    @JsonProperty("href")
    private String href;

    /** The {@link HttpMethod}. */
    @JsonProperty("method")
    private HttpMethod method;
}
