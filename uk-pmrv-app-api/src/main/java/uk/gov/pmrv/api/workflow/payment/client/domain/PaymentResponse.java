package uk.gov.pmrv.api.workflow.payment.client.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentResponse extends PaymentData {

    /** A structure representing the current state of the payment in its lifecycle. */
    @JsonProperty("state")
    private PaymentState state;

    /** The unique payment id. */
    @JsonProperty("payment_id")
    private String paymentId;

    /** The payment created date. */
    @JsonProperty("created_date")
    private ZonedDateTime createdDate;

    /** The links for payment. */
    @JsonProperty("_links")
    private PaymentLinks links;
}
