package uk.gov.pmrv.api.workflow.request.flow.payment.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.workflow.payment.domain.enumeration.PaymentMethodType;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.payment.domain.PaymentCancelledRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.payment.domain.PaymentProcessedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.payment.domain.PaymentStatus;
import uk.gov.pmrv.api.workflow.request.flow.payment.domain.RequestPaymentInfo;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.common.domain.PermitIssuanceRequestPayload;

@ExtendWith(MockitoExtension.class)
class PaymentCompleteRequestActionServiceTest {

    @InjectMocks
    private PaymentCompleteRequestActionService paymentCompleteRequestActionService;

    @Mock
    private RequestService requestService;

    @Test
    void markAsPaid() {
        String requestId = "req-1";
        String paidById = "userId";
        String paidByFullName = "userFullName";
        BigDecimal amount = BigDecimal.valueOf(545.56);
        LocalDate paymentDate = LocalDate.now().minusDays(8);
        PaymentStatus paymentStatus = PaymentStatus.MARK_AS_PAID;
        PaymentMethodType paymentMethod = PaymentMethodType.BANK_TRANSFER;
        RequestPaymentInfo requestPaymentInfo = RequestPaymentInfo.builder()
            .paymentDate(paymentDate)
            .paidById(paidById)
            .paidByFullName(paidByFullName)
            .amount(amount)
            .status(paymentStatus)
            .paymentMethod(paymentMethod)
            .build();
        PermitIssuanceRequestPayload requestPayload = PermitIssuanceRequestPayload.builder()
            .payloadType(RequestPayloadType.PERMIT_ISSUANCE_REQUEST_PAYLOAD)
            .requestPaymentInfo(requestPaymentInfo)
            .operatorAssignee("operatorAssignee")
            .regulatorAssignee("regulatorAssignee")
            .build();
        Request request = Request.builder()
            .id(requestId)
            .competentAuthority(CompetentAuthority.ENGLAND)
            .type(RequestType.PERMIT_ISSUANCE)
            .payload(requestPayload)
            .build();
        PaymentProcessedRequestActionPayload requestActionPayload = PaymentProcessedRequestActionPayload.builder()
            .payloadType(RequestActionPayloadType.PAYMENT_MARKED_AS_PAID_PAYLOAD)
            .paymentRefNum(requestId)
            .paymentDate(paymentDate)
            .paidByFullName(paidByFullName)
            .amount(amount)
            .status(paymentStatus)
            .paymentMethod(paymentMethod)
            .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);

        paymentCompleteRequestActionService.markAsPaid(requestId);

        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestService, times(1))
            .addActionToRequest(request, requestActionPayload, RequestActionType.PAYMENT_MARKED_AS_PAID, requestPaymentInfo.getPaidById());
    }

    @Test
    void markAsReceived() {
        String requestId = "req-1";
        String paidById = "userId";
        String paidByFullName = "userFullName";
        BigDecimal amount = BigDecimal.valueOf(545.56);
        LocalDate paymentDate = LocalDate.now().minusDays(8);
        LocalDate receivedDate = LocalDate.now().minusDays(10);
        PaymentStatus paymentStatus = PaymentStatus.MARK_AS_RECEIVED;
        PaymentMethodType paymentMethod = PaymentMethodType.BANK_TRANSFER;
        RequestPaymentInfo requestPaymentInfo = RequestPaymentInfo.builder()
            .paymentDate(paymentDate)
            .paidById(paidById)
            .paidByFullName(paidByFullName)
            .amount(amount)
            .status(paymentStatus)
            .paymentMethod(paymentMethod)
            .receivedDate(receivedDate)
            .build();
        PermitIssuanceRequestPayload requestPayload = PermitIssuanceRequestPayload.builder()
            .payloadType(RequestPayloadType.PERMIT_ISSUANCE_REQUEST_PAYLOAD)
            .requestPaymentInfo(requestPaymentInfo)
            .operatorAssignee("operatorAssignee")
            .regulatorAssignee("regulatorAssignee")
            .build();
        Request request = Request.builder()
            .id(requestId)
            .competentAuthority(CompetentAuthority.ENGLAND)
            .type(RequestType.PERMIT_ISSUANCE)
            .payload(requestPayload)
            .build();
        PaymentProcessedRequestActionPayload requestActionPayload = PaymentProcessedRequestActionPayload.builder()
            .payloadType(RequestActionPayloadType.PAYMENT_MARKED_AS_RECEIVED_PAYLOAD)
            .paymentRefNum(requestId)
            .paymentDate(paymentDate)
            .paidByFullName(paidByFullName)
            .amount(amount)
            .status(paymentStatus)
            .paymentMethod(paymentMethod)
            .receivedDate(receivedDate)
            .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);

        paymentCompleteRequestActionService.markAsReceived(requestId);

        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestService, times(1))
            .addActionToRequest(request, requestActionPayload, RequestActionType.PAYMENT_MARKED_AS_RECEIVED, requestPayload.getRegulatorAssignee());
    }

    @Test
    void cancelled() {
        String requestId = "req-1";
        String cancellationReason = "cancellationReason";
        RequestPaymentInfo requestPaymentInfo = RequestPaymentInfo.builder()
            .paymentDate(LocalDate.now())
            .paidByFullName("userId")
            .amount(BigDecimal.valueOf(545.56))
            .status(PaymentStatus.CANCELLED)
            .paymentMethod(PaymentMethodType.BANK_TRANSFER)
            .cancellationReason(cancellationReason)
            .build();
        PermitIssuanceRequestPayload requestPayload = PermitIssuanceRequestPayload.builder()
            .payloadType(RequestPayloadType.PERMIT_ISSUANCE_REQUEST_PAYLOAD)
            .requestPaymentInfo(requestPaymentInfo)
            .operatorAssignee("operatorAssignee")
            .regulatorAssignee("regulatorAssignee")
            .build();
        Request request = Request.builder()
            .id(requestId)
            .competentAuthority(CompetentAuthority.ENGLAND)
            .type(RequestType.PERMIT_ISSUANCE)
            .payload(requestPayload)
            .build();
        PaymentCancelledRequestActionPayload requestActionPayload = PaymentCancelledRequestActionPayload.builder()
            .payloadType(RequestActionPayloadType.PAYMENT_CANCELLED_PAYLOAD)
            .status(PaymentStatus.CANCELLED)
            .cancellationReason(cancellationReason)
            .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);

        paymentCompleteRequestActionService.cancel(requestId);

        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestService, times(1))
            .addActionToRequest(request, requestActionPayload, RequestActionType.PAYMENT_CANCELLED, requestPayload.getRegulatorAssignee());
    }

    @Test
    void complete() {
        String requestId = "req-1";
        String paidById = "userId";
        String paidByFullName = "userFullName";
        BigDecimal amount = BigDecimal.valueOf(545.56);
        LocalDate paymentDate = LocalDate.now().minusDays(2);
        PaymentStatus paymentStatus = PaymentStatus.COMPLETED;
        PaymentMethodType paymentMethod = PaymentMethodType.CREDIT_OR_DEBIT_CARD;
        RequestPaymentInfo requestPaymentInfo = RequestPaymentInfo.builder()
            .paymentDate(paymentDate)
            .paidById(paidById)
            .paidByFullName(paidByFullName)
            .amount(amount)
            .status(paymentStatus)
            .paymentMethod(paymentMethod)
            .build();
        PermitIssuanceRequestPayload requestPayload = PermitIssuanceRequestPayload.builder()
            .payloadType(RequestPayloadType.PERMIT_ISSUANCE_REQUEST_PAYLOAD)
            .requestPaymentInfo(requestPaymentInfo)
            .operatorAssignee("operatorAssignee")
            .regulatorAssignee("regulatorAssignee")
            .build();
        Request request = Request.builder()
            .id(requestId)
            .competentAuthority(CompetentAuthority.ENGLAND)
            .type(RequestType.PERMIT_ISSUANCE)
            .payload(requestPayload)
            .build();
        PaymentProcessedRequestActionPayload requestActionPayload = PaymentProcessedRequestActionPayload.builder()
            .payloadType(RequestActionPayloadType.PAYMENT_COMPLETED_PAYLOAD)
            .paymentRefNum(requestId)
            .paymentDate(paymentDate)
            .paidByFullName(paidByFullName)
            .amount(amount)
            .status(paymentStatus)
            .paymentMethod(paymentMethod)
            .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);

        paymentCompleteRequestActionService.complete(requestId);

        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestService, times(1))
            .addActionToRequest(request, requestActionPayload, RequestActionType.PAYMENT_COMPLETED, requestPaymentInfo.getPaidById());
    }
}