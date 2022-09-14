package uk.gov.pmrv.api.workflow.request.flow.payment.transform;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.gov.pmrv.api.workflow.payment.domain.enumeration.PaymentMethodType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.payment.domain.PaymentConfirmRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.payment.domain.PaymentCancelledRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.payment.domain.PaymentProcessedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.payment.domain.PaymentStatus;
import uk.gov.pmrv.api.workflow.request.flow.payment.domain.RequestPaymentInfo;

class PaymentPayloadMapperTest {

    private final PaymentPayloadMapper paymentPayloadMapper = Mappers.getMapper(PaymentPayloadMapper.class);

    @Test
    void toPaymentProcessedRequestActionPayload() {
        String paidById = "userId";
        String paidByFullName = "userFullName";
        BigDecimal amount = BigDecimal.valueOf(20320.52);
        PaymentStatus paymentStatus = PaymentStatus.MARK_AS_PAID;
        PaymentMethodType paymentMethod = PaymentMethodType.BANK_TRANSFER;
        LocalDate paymentDate = LocalDate.now().minusDays(2);
        String paymentRefNum = "EAM-123-4";
        RequestPaymentInfo requestPaymentInfo = RequestPaymentInfo.builder()
            .paymentDate(paymentDate)
            .paidById(paidById)
            .paidByFullName(paidByFullName)
            .amount(amount)
            .status(paymentStatus)
            .paymentMethod(paymentMethod)
            .build();

        PaymentProcessedRequestActionPayload requestActionPayload =
            paymentPayloadMapper
                .toPaymentProcessedRequestActionPayload(paymentRefNum, requestPaymentInfo, RequestActionPayloadType.PAYMENT_MARKED_AS_PAID_PAYLOAD);

        assertEquals(RequestActionPayloadType.PAYMENT_MARKED_AS_PAID_PAYLOAD, requestActionPayload.getPayloadType());
        assertEquals(paymentRefNum, requestActionPayload.getPaymentRefNum());
        assertEquals(paidByFullName, requestActionPayload.getPaidByFullName());
        assertEquals(paymentDate, requestActionPayload.getPaymentDate());
        assertEquals(amount, requestActionPayload.getAmount());
        assertEquals(paymentStatus, requestActionPayload.getStatus());
        assertEquals(paymentMethod, requestActionPayload.getPaymentMethod());
    }

    @Test
    void toPaymentCancelledRequestActionPayload() {
        PaymentStatus paymentStatus = PaymentStatus.CANCELLED;
        String cancellationReason = "cancellationReason";
        RequestPaymentInfo requestPaymentInfo = RequestPaymentInfo.builder()
            .status(paymentStatus)
            .cancellationReason(cancellationReason)
            .build();

        PaymentCancelledRequestActionPayload requestActionPayload = paymentPayloadMapper.toPaymentCancelledRequestActionPayload(requestPaymentInfo);

        assertEquals(RequestActionPayloadType.PAYMENT_CANCELLED_PAYLOAD, requestActionPayload.getPayloadType());
        assertEquals(paymentStatus, requestActionPayload.getStatus());
        assertEquals(cancellationReason, requestActionPayload.getCancellationReason());
    }

    @Test
    void toConfirmPaymentRequestTaskPayload() {
        String paidByFullName = "userFullName";
        BigDecimal amount = BigDecimal.valueOf(20320.52);
        PaymentStatus paymentStatus = PaymentStatus.MARK_AS_PAID;
        PaymentMethodType paymentMethod = PaymentMethodType.BANK_TRANSFER;
        LocalDate paymentDate = LocalDate.now().minusDays(4);
        String paymentRefNum = "EAM-123-4";
        RequestPaymentInfo requestPaymentInfo = RequestPaymentInfo.builder()
            .paymentDate(paymentDate)
            .paidByFullName(paidByFullName)
            .amount(amount)
            .status(paymentStatus)
            .paymentMethod(paymentMethod)
            .build();

        PaymentConfirmRequestTaskPayload paymentConfirmRequestTaskPayload =
            paymentPayloadMapper.toConfirmPaymentRequestTaskPayload(paymentRefNum, requestPaymentInfo);

        assertEquals(RequestTaskPayloadType.PAYMENT_CONFIRM_PAYLOAD, paymentConfirmRequestTaskPayload.getPayloadType());
        assertEquals(paymentRefNum, paymentConfirmRequestTaskPayload.getPaymentRefNum());
        assertEquals(paymentDate, paymentConfirmRequestTaskPayload.getPaymentDate());
        assertEquals(paidByFullName, paymentConfirmRequestTaskPayload.getPaidByFullName());
        assertEquals(amount, paymentConfirmRequestTaskPayload.getAmount());
        assertEquals(paymentStatus, paymentConfirmRequestTaskPayload.getStatus());
        assertEquals(paymentMethod, paymentConfirmRequestTaskPayload.getPaymentMethod());

    }
}