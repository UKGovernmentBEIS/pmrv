package uk.gov.pmrv.api.workflow.payment.transform;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.http.HttpMethod;
import uk.gov.pmrv.api.workflow.payment.client.domain.Link;
import uk.gov.pmrv.api.workflow.payment.client.domain.PaymentLinks;
import uk.gov.pmrv.api.workflow.payment.client.domain.PaymentResponse;
import uk.gov.pmrv.api.workflow.payment.client.domain.PaymentState;
import uk.gov.pmrv.api.workflow.payment.domain.dto.PaymentCreateResult;
import uk.gov.pmrv.api.workflow.payment.domain.dto.PaymentGetResult;
import uk.gov.pmrv.api.workflow.payment.domain.dto.PaymentStateInfo;

class PaymentMapperTest {

    private final PaymentMapper paymentMapper = Mappers.getMapper(PaymentMapper.class);

    @Test
    void toPaymentGetResult() {
        String paymentId = "n4brhul26f2hn1lt992ejj10ht";
        String status = "fail";
        String code = "P0020";
        String message = "Payment Expired";
        String next_url = "next_url";
        PaymentState paymentState = PaymentState.builder()
            .status(status)
            .code(code)
            .message(message)
            .build();
        PaymentLinks paymentLinks = PaymentLinks.builder()
            .nextUrl(Link.builder().href(next_url).method(HttpMethod.GET).build())
            .build();
        PaymentResponse paymentResponse = PaymentResponse.builder()
            .paymentId(paymentId)
            .state(paymentState)
            .links(paymentLinks)
            .build();

        PaymentGetResult paymentGetResult = paymentMapper.toPaymentGetResult(paymentResponse);

        assertNotNull(paymentGetResult);
        assertEquals(paymentId, paymentGetResult.getPaymentId());
        assertEquals(next_url, paymentGetResult.getNextUrl());

        PaymentStateInfo paymentStateInfo = paymentGetResult.getState();
        assertNotNull(paymentStateInfo);
        assertEquals(status, paymentStateInfo.getStatus());
        assertEquals(code, paymentStateInfo.getCode());
        assertEquals(message, paymentStateInfo.getMessage());

    }

    @Test
    void toPaymentCreateResult() {
        String paymentId = "n4brhul26f2hn1lt992ejj10ht";
        String next_url = "next_url";
        PaymentLinks paymentLinks = PaymentLinks.builder()
            .nextUrl(Link.builder().href(next_url).method(HttpMethod.GET).build())
            .build();
        PaymentResponse paymentResponse = PaymentResponse.builder()
            .paymentId(paymentId)
            .state(PaymentState.builder().build())
            .links(paymentLinks)
            .build();

        PaymentCreateResult paymentCreateResult = paymentMapper.toPaymentCreateResult(paymentResponse);

        assertNotNull(paymentCreateResult);
        assertEquals(paymentId, paymentCreateResult.getPaymentId());
        assertEquals(next_url, paymentCreateResult.getNextUrl());
    }
}