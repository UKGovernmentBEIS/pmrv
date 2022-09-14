package uk.gov.pmrv.api.workflow.request.flow.payment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.payment.domain.enumeration.PaymentMethodType;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestActionUserInfoResolver;
import uk.gov.pmrv.api.workflow.request.flow.payment.domain.PaymentMakeRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.payment.domain.PaymentStatus;
import uk.gov.pmrv.api.workflow.request.flow.payment.domain.RequestPaymentInfo;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.common.domain.PermitIssuanceRequestPayload;

@ExtendWith(MockitoExtension.class)
class PaymentCompleteServiceTest {

    @InjectMocks
    private PaymentCompleteService paymentCompleteService;

    @Mock
    private RequestService requestService;

    @Mock
    private RequestActionUserInfoResolver requestActionUserInfoResolver;

    @Test
    void markAsPaid() {
        String userId = "userId";
        String userFullName = "userFullName";
        PmrvUser authUser = PmrvUser.builder().userId(userId).build();
        BigDecimal paymentAmount = BigDecimal.valueOf(545.56);
        PermitIssuanceRequestPayload requestPayload = PermitIssuanceRequestPayload.builder()
            .payloadType(RequestPayloadType.PERMIT_ISSUANCE_REQUEST_PAYLOAD)
            .paymentAmount(paymentAmount)
            .build();
        String requestId = "req-1";
        Request request = Request.builder()
            .id(requestId)
            .competentAuthority(CompetentAuthority.ENGLAND)
            .type(RequestType.PERMIT_ISSUANCE)
            .payload(requestPayload)
            .build();
        PaymentMakeRequestTaskPayload requestTaskPayload = PaymentMakeRequestTaskPayload.builder()
            .paymentMethodTypes(Set.of(PaymentMethodType.BANK_TRANSFER))
            .build();
        RequestTask requestTask = RequestTask.builder()
            .type(RequestTaskType.PERMIT_ISSUANCE_MAKE_PAYMENT)
            .payload(requestTaskPayload)
            .request(request)
            .build();
        RequestPaymentInfo requestPaymentInfo = RequestPaymentInfo.builder()
            .paymentDate(LocalDate.now())
            .paidById(userId)
            .paidByFullName(userFullName)
            .amount(paymentAmount)
            .status(PaymentStatus.MARK_AS_PAID)
            .paymentMethod(PaymentMethodType.BANK_TRANSFER)
            .build();

        when(requestActionUserInfoResolver.getUserFullName(authUser.getUserId())).thenReturn(userFullName);

        //invoke
        paymentCompleteService.markAsPaid(requestTask, authUser);

        //verify
        ArgumentCaptor<Request> requestCaptor = ArgumentCaptor.forClass(Request.class);
        verify(requestService, times(1)).saveRequest(requestCaptor.capture());

        Request updatedRequest = requestCaptor.getValue();
        assertThat(updatedRequest.getPayload()).isInstanceOf(PermitIssuanceRequestPayload.class);
        PermitIssuanceRequestPayload payloadSaved = (PermitIssuanceRequestPayload) updatedRequest.getPayload();
        assertEquals(requestPaymentInfo, payloadSaved.getRequestPaymentInfo());

        verify(requestActionUserInfoResolver, times(1)).getUserFullName(authUser.getUserId());
    }

    @Test
    void markAsPaid_bank_transfer_not_supported() {
        String userId = "userId";
        PmrvUser authUser = PmrvUser.builder().userId(userId).build();
        PaymentMakeRequestTaskPayload requestTaskPayload = PaymentMakeRequestTaskPayload.builder()
            .paymentMethodTypes(Set.of(PaymentMethodType.CREDIT_OR_DEBIT_CARD))
            .build();
        RequestTask requestTask = RequestTask.builder()
            .type(RequestTaskType.PERMIT_ISSUANCE_MAKE_PAYMENT)
            .payload(requestTaskPayload)
            .build();


        //invoke
        BusinessException businessException = assertThrows(BusinessException.class,
            () -> paymentCompleteService.markAsPaid(requestTask, authUser));

        assertEquals(ErrorCode.INVALID_PAYMENT_METHOD, businessException.getErrorCode());

        verifyNoInteractions(requestService);
    }

    @Test
    void markAsReceived() {
        LocalDate receivedDate = LocalDate.now().minusWeeks(1);
        RequestPaymentInfo requestPaymentInfo = RequestPaymentInfo.builder()
            .paymentDate(LocalDate.now().minusDays(2))
            .paidByFullName("userFullName")
            .amount(BigDecimal.TEN)
            .status(PaymentStatus.MARK_AS_PAID)
            .paymentMethod(PaymentMethodType.BANK_TRANSFER)
            .build();
        PermitIssuanceRequestPayload requestPayload = PermitIssuanceRequestPayload.builder()
            .payloadType(RequestPayloadType.PERMIT_ISSUANCE_REQUEST_PAYLOAD)
            .requestPaymentInfo(requestPaymentInfo)
            .operatorAssignee("operatorAssignee")
            .build();
        Request request = Request.builder()
            .competentAuthority(CompetentAuthority.ENGLAND)
            .type(RequestType.PERMIT_ISSUANCE)
            .payload(requestPayload)
            .build();

        //invoke
        paymentCompleteService.markAsReceived(request, receivedDate);

        //verify
        ArgumentCaptor<Request> requestCaptor = ArgumentCaptor.forClass(Request.class);
        verify(requestService, times(1)).saveRequest(requestCaptor.capture());

        Request updatedRequest = requestCaptor.getValue();
        assertThat(updatedRequest.getPayload()).isInstanceOf(PermitIssuanceRequestPayload.class);
        PermitIssuanceRequestPayload updatedRequestPayload = (PermitIssuanceRequestPayload) updatedRequest.getPayload();

        RequestPaymentInfo updatedRequestPaymentInfo = updatedRequestPayload.getRequestPaymentInfo();

        assertEquals(receivedDate, updatedRequestPaymentInfo.getReceivedDate());
        assertEquals(PaymentStatus.MARK_AS_RECEIVED, updatedRequestPaymentInfo.getStatus());
        assertEquals(requestPaymentInfo.getPaymentMethod(), updatedRequestPaymentInfo.getPaymentMethod());
        assertEquals(requestPaymentInfo.getPaymentDate(), updatedRequestPaymentInfo.getPaymentDate());
        assertEquals(requestPaymentInfo.getPaidByFullName(), updatedRequestPaymentInfo.getPaidByFullName());
        assertEquals(requestPaymentInfo.getAmount(), updatedRequestPaymentInfo.getAmount());
    }

    @Test
    void markAsReceived_before_payment_gets_paid() {
        String operatorUserId = "operatorUserId";
        String operatorUserName = "operatorUserName";
        LocalDate receivedDate = LocalDate.now().minusWeeks(1);
        PermitIssuanceRequestPayload requestPayload = PermitIssuanceRequestPayload.builder()
            .payloadType(RequestPayloadType.PERMIT_ISSUANCE_REQUEST_PAYLOAD)
            .operatorAssignee(operatorUserId)
            .paymentAmount(BigDecimal.valueOf(200))
            .build();
        Request request = Request.builder()
            .competentAuthority(CompetentAuthority.ENGLAND)
            .type(RequestType.PERMIT_ISSUANCE)
            .payload(requestPayload)
            .build();

        when(requestActionUserInfoResolver.getUserFullName(requestPayload.getOperatorAssignee())).thenReturn(operatorUserName);

        //invoke
        paymentCompleteService.markAsReceived(request, receivedDate);

        //verify
        ArgumentCaptor<Request> requestCaptor = ArgumentCaptor.forClass(Request.class);
        verify(requestService, times(1)).saveRequest(requestCaptor.capture());

        Request updatedRequest = requestCaptor.getValue();
        assertThat(updatedRequest.getPayload()).isInstanceOf(PermitIssuanceRequestPayload.class);
        PermitIssuanceRequestPayload updatedRequestPayload = (PermitIssuanceRequestPayload) updatedRequest.getPayload();

        RequestPaymentInfo updatedRequestPaymentInfo = updatedRequestPayload.getRequestPaymentInfo();

        assertEquals(receivedDate, updatedRequestPaymentInfo.getReceivedDate());
        assertEquals(PaymentStatus.MARK_AS_RECEIVED, updatedRequestPaymentInfo.getStatus());
        assertEquals(PaymentMethodType.BANK_TRANSFER, updatedRequestPaymentInfo.getPaymentMethod());
        assertEquals(requestPayload.getPaymentAmount(), updatedRequestPaymentInfo.getAmount());
        assertEquals(operatorUserName, updatedRequestPaymentInfo.getPaidByFullName());
        assertNotNull(updatedRequestPaymentInfo.getPaymentDate());

        verify(requestActionUserInfoResolver, times(1)).getUserFullName(requestPayload.getOperatorAssignee());
    }

    @Test
    void cancel() {
        String cancellationReason = "cancellationReason";
        RequestPaymentInfo requestPaymentInfo = RequestPaymentInfo.builder()
            .paymentDate(LocalDate.now().minusDays(5))
            .paidByFullName("userName")
            .amount(BigDecimal.TEN)
            .status(PaymentStatus.MARK_AS_PAID)
            .paymentMethod(PaymentMethodType.BANK_TRANSFER)
            .build();
        PermitIssuanceRequestPayload requestPayload = PermitIssuanceRequestPayload.builder()
            .payloadType(RequestPayloadType.PERMIT_ISSUANCE_REQUEST_PAYLOAD)
            .requestPaymentInfo(requestPaymentInfo)
            .build();
        Request request = Request.builder()
            .competentAuthority(CompetentAuthority.ENGLAND)
            .type(RequestType.PERMIT_ISSUANCE)
            .payload(requestPayload)
            .build();

        //invoke
        paymentCompleteService.cancel(request, cancellationReason);

        //verify
        ArgumentCaptor<Request> requestCaptor = ArgumentCaptor.forClass(Request.class);
        verify(requestService, times(1)).saveRequest(requestCaptor.capture());

        Request updatedRequest = requestCaptor.getValue();
        assertThat(updatedRequest.getPayload()).isInstanceOf(PermitIssuanceRequestPayload.class);
        PermitIssuanceRequestPayload updatedRequestPayload = (PermitIssuanceRequestPayload) updatedRequest.getPayload();

        RequestPaymentInfo updatedRequestPaymentInfo = updatedRequestPayload.getRequestPaymentInfo();

        assertEquals(cancellationReason, updatedRequestPaymentInfo.getCancellationReason());
        assertEquals(PaymentStatus.CANCELLED, updatedRequestPaymentInfo.getStatus());
        assertEquals(requestPaymentInfo.getPaymentMethod(), updatedRequestPaymentInfo.getPaymentMethod());
        assertEquals(requestPaymentInfo.getPaymentDate(), updatedRequestPaymentInfo.getPaymentDate());
        assertEquals(requestPaymentInfo.getPaidByFullName(), updatedRequestPaymentInfo.getPaidByFullName());
        assertEquals(requestPaymentInfo.getAmount(), updatedRequestPaymentInfo.getAmount());
    }

    @Test
    void cancel_before_payment_gets_paid() {
        String cancellationReason = "cancellationReason";
        PermitIssuanceRequestPayload requestPayload = PermitIssuanceRequestPayload.builder()
            .payloadType(RequestPayloadType.PERMIT_ISSUANCE_REQUEST_PAYLOAD)
            .operatorAssignee("operatorAssignee")
            .build();
        Request request = Request.builder()
            .competentAuthority(CompetentAuthority.ENGLAND)
            .type(RequestType.PERMIT_ISSUANCE)
            .payload(requestPayload)
            .build();

        //invoke
        paymentCompleteService.cancel(request, cancellationReason);

        //verify
        ArgumentCaptor<Request> requestCaptor = ArgumentCaptor.forClass(Request.class);
        verify(requestService, times(1)).saveRequest(requestCaptor.capture());

        Request updatedRequest = requestCaptor.getValue();
        assertThat(updatedRequest.getPayload()).isInstanceOf(PermitIssuanceRequestPayload.class);
        PermitIssuanceRequestPayload updatedRequestPayload = (PermitIssuanceRequestPayload) updatedRequest.getPayload();

        RequestPaymentInfo updatedRequestPaymentInfo = updatedRequestPayload.getRequestPaymentInfo();

        assertEquals(cancellationReason, updatedRequestPaymentInfo.getCancellationReason());
        assertEquals(PaymentStatus.CANCELLED, updatedRequestPaymentInfo.getStatus());
        assertNull(updatedRequestPaymentInfo.getPaymentMethod());
        assertNull(updatedRequestPaymentInfo.getPaymentDate());
        assertNull(updatedRequestPaymentInfo.getPaidByFullName());
        assertNull(updatedRequestPaymentInfo.getAmount());
    }

    @Test
    void complete() {
        String userId = "userId";
        String userName = "userName";
        PmrvUser authUser = PmrvUser.builder().userId(userId).build();
        BigDecimal paymentAmount = BigDecimal.valueOf(545.56);
        PermitIssuanceRequestPayload requestPayload = PermitIssuanceRequestPayload.builder()
            .payloadType(RequestPayloadType.PERMIT_ISSUANCE_REQUEST_PAYLOAD)
            .paymentAmount(paymentAmount)
            .build();
        String requestId = "req-1";
        Request request = Request.builder()
            .id(requestId)
            .competentAuthority(CompetentAuthority.ENGLAND)
            .type(RequestType.PERMIT_ISSUANCE)
            .payload(requestPayload)
            .build();
        RequestPaymentInfo requestPaymentInfo = RequestPaymentInfo.builder()
            .paymentDate(LocalDate.now())
            .paidById(userId)
            .paidByFullName(userName)
            .amount(paymentAmount)
            .status(PaymentStatus.COMPLETED)
            .paymentMethod(PaymentMethodType.CREDIT_OR_DEBIT_CARD)
            .build();

        when(requestActionUserInfoResolver.getUserFullName(authUser.getUserId())).thenReturn(userName);

        //invoke
        paymentCompleteService.complete(request, authUser);

        //verify
        ArgumentCaptor<Request> requestCaptor = ArgumentCaptor.forClass(Request.class);
        verify(requestService, times(1)).saveRequest(requestCaptor.capture());

        Request updatedRequest = requestCaptor.getValue();
        assertThat(updatedRequest.getPayload()).isInstanceOf(PermitIssuanceRequestPayload.class);
        PermitIssuanceRequestPayload payloadSaved = (PermitIssuanceRequestPayload) updatedRequest.getPayload();
        assertEquals(requestPaymentInfo, payloadSaved.getRequestPaymentInfo());
    }
}