package uk.gov.pmrv.api.workflow.payment.client.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A structure representing the current state of the payment in its lifecycle of GOV.UK Pay.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentState {

    /** Current progress of the payment in its lifecycle. */
    @JsonProperty("status")
    private String status;

    /** Whether the payment has finished. */
    @JsonProperty("finished")
    private Boolean finished;

    /** What went wrong with the Payment if it finished with an error - English message. */
    @JsonProperty("message")
    private String message;

    /** What went wrong with the Payment if it finished with an error - error code. */
    @JsonProperty("code")
    private String code;
}
