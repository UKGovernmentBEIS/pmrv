package uk.gov.pmrv.api.workflow.payment.client.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.*;

/**
 * The links for payment of GOV.UK Pay.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentLinks {

    /** The next url link. */
    @JsonProperty("next_url")
    private Link nextUrl;
}
