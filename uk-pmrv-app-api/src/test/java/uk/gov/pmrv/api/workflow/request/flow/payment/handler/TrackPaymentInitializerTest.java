package uk.gov.pmrv.api.workflow.request.flow.payment.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.flow.payment.domain.PaymentTrackRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.common.domain.PermitIssuanceRequestPayload;

class TrackPaymentInitializerTest {

    private final TrackPaymentInitializer trackPaymentInitializer = new TrackPaymentInitializer();

    @Test
    void initializePayload() {
        BigDecimal paymentAmount = BigDecimal.valueOf(5230.11);
        PermitIssuanceRequestPayload requestPayload = PermitIssuanceRequestPayload.builder()
            .paymentAmount(paymentAmount)
            .build();
        Request request = Request.builder()
            .id("request-id-1")
            .payload(requestPayload)
            .build();

        RequestTaskPayload requestTaskPayload = trackPaymentInitializer.initializePayload(request);
        assertThat(requestTaskPayload.getPayloadType()).isEqualTo(RequestTaskPayloadType.PAYMENT_TRACK_PAYLOAD);
        assertThat(requestTaskPayload).isInstanceOf(PaymentTrackRequestTaskPayload.class);

        PaymentTrackRequestTaskPayload paymentTrackRequestTaskPayload = (PaymentTrackRequestTaskPayload) requestTaskPayload;

        assertEquals(request.getId(), paymentTrackRequestTaskPayload.getPaymentRefNum());
        assertEquals(paymentAmount, paymentTrackRequestTaskPayload.getAmount());
    }

    @Test
    void getRequestTaskTypes() {
        assertThat(trackPaymentInitializer.getRequestTaskTypes()).containsExactlyElementsOf(RequestTaskType.getTrackPaymentTypes());
    }
}