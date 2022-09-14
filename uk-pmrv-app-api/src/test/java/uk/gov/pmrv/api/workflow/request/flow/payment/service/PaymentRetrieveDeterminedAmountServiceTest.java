package uk.gov.pmrv.api.workflow.request.flow.payment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.permitrevocation.domain.PermitRevocationRequestPayload;

class PaymentRetrieveDeterminedAmountServiceTest {

    private final PaymentRetrieveDeterminedAmountService paymentRetrieveDeterminedAmountService =
        new PaymentRetrieveDeterminedAmountService();

    @Test
    void determineAmount() {
        BigDecimal amount = BigDecimal.valueOf(3625.0);
        PermitRevocationRequestPayload requestPayload = PermitRevocationRequestPayload.builder()
            .payloadType(RequestPayloadType.PERMIT_REVOCATION_REQUEST_PAYLOAD)
            .paymentAmount(amount)
            .build();
        Request request = Request.builder().payload(requestPayload).build();

        assertEquals(amount, paymentRetrieveDeterminedAmountService.determineAmount(request));
    }

    @Test
    void determineAmount_when_no_amount_in_request_payload() {
        PermitRevocationRequestPayload requestPayload = PermitRevocationRequestPayload.builder()
            .payloadType(RequestPayloadType.PERMIT_REVOCATION_REQUEST_PAYLOAD)
            .build();
        Request request = Request.builder().payload(requestPayload).build();

        assertEquals(BigDecimal.ZERO, paymentRetrieveDeterminedAmountService.determineAmount(request));
    }

    @Test
    void getRequestTypes() {
        assertThat(paymentRetrieveDeterminedAmountService.getRequestTypes()).containsOnly(RequestType.PERMIT_REVOCATION);
    }
}