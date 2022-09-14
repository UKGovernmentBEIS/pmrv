package uk.gov.pmrv.api.workflow.payment.client.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Payment Create Object of GOV.UK Pay.
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class PaymentData {

    /** The amount of payment multiplied with 100. */
    @JsonProperty("amount")
    private Integer amount;

    /** Payment service description. */
    @JsonProperty("description")
    private String description;

    /** The payment reference identifier. */
    @JsonProperty("reference")
    private String reference;

    /** The return url. */
    @JsonProperty("return_url")
    private String returnUrl;

}
