package uk.gov.pmrv.api.workflow.payment.client.domain.enumeration;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import uk.gov.pmrv.api.common.domain.provider.RestEndPoint;
import uk.gov.pmrv.api.workflow.payment.client.domain.PaymentResponse;

/**
 * The GOV UK Pay rest points enum.
 */
@Getter
@AllArgsConstructor
public enum RestEndPointEnum implements RestEndPoint {

    /** Return information about the payment. The Authorisation token needs to be specified in the 'authorization' header as 'authorization: Bearer YOUR_API_KEY_HERE'. */
    GOV_UK_GET_PAYMENT("/v1/payments/{paymentId}", HttpMethod.GET, new ParameterizedTypeReference<PaymentResponse>() {}, List.of("paymentId")),

    /** Create a new payment for the account associated to the Authorisation token. */
    GOV_UK_CREATE_PAYMENT("/v1/payments", HttpMethod.POST, new ParameterizedTypeReference<PaymentResponse>() {}, null);

    /** The url. */
    private final String endPoint;

    /** The {@link HttpMethod}. */
    private final HttpMethod method;

    /** The {@link ParameterizedTypeReference}. */
    private final ParameterizedTypeReference<?> parameterizedTypeReference;

    /** The List of parameters or path variable values. */
    private final List<String> parameters;
}
