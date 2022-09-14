package uk.gov.pmrv.api.workflow.request.flow.payment.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.payment.domain.enumeration.FeeMethodType;
import uk.gov.pmrv.api.workflow.payment.service.FeePaymentService;
import uk.gov.pmrv.api.workflow.payment.service.PaymentFeeMethodService;
import uk.gov.pmrv.api.workflow.payment.service.StandardFeePaymentService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.common.domain.PermitIssuanceRequestPayload;

@ExtendWith(MockitoExtension.class)
class PaymentAmountServiceTest {

    private PaymentAmountService paymentAmountService;

    @Mock
    private RequestService requestService;

    @Mock
    private PaymentFeeMethodService paymentFeeService;

    @Mock
    private StandardFeePaymentService standardFeePaymentService;

    @BeforeEach
    void setUp() {
        List<FeePaymentService> feePaymentServices = List.of(standardFeePaymentService);
        paymentAmountService = new PaymentAmountService(requestService, paymentFeeService,
            feePaymentServices);
    }

    @Test
    void getAmount() {
        String requestId = "1";
        CompetentAuthority competentAuthority = CompetentAuthority.WALES;
        RequestType requestType = RequestType.PERMIT_ISSUANCE;
        Request request = Request.builder()
            .id(requestId)
            .competentAuthority(competentAuthority)
            .type(requestType)
            .payload(PermitIssuanceRequestPayload.builder().payloadType(RequestPayloadType.PERMIT_ISSUANCE_REQUEST_PAYLOAD).build())
            .build();
        FeeMethodType feeMethodType = FeeMethodType.STANDARD;
        BigDecimal expectedAmount = BigDecimal.valueOf(234.5);

        when(paymentFeeService.getFeeMethodType(competentAuthority, requestType)).thenReturn(Optional.of(feeMethodType));
        when(standardFeePaymentService.getFeeMethodType()).thenReturn(FeeMethodType.STANDARD);
        when(standardFeePaymentService.getAmount(request)).thenReturn(expectedAmount);

        BigDecimal actualAmount = paymentAmountService.getAmount(request);

        ArgumentCaptor<Request> requestCaptor = ArgumentCaptor.forClass(Request.class);
        verify(requestService, times(1)).saveRequest(requestCaptor.capture());
        RequestPayload updatedRequestPayload = requestCaptor.getValue().getPayload();

        assertEquals(expectedAmount, updatedRequestPayload.getPaymentAmount());
        assertEquals(expectedAmount, actualAmount);

        verify(paymentFeeService, times(1)).getFeeMethodType(competentAuthority, requestType);
        verify(standardFeePaymentService, times(1)).getFeeMethodType();
        verify(standardFeePaymentService, times(1)).getAmount(request);
        verifyNoMoreInteractions(standardFeePaymentService);
    }

    @Test
    void getAmount_no_feeMethodType_found() {
        String requestId = "1";
        CompetentAuthority competentAuthority = CompetentAuthority.WALES;
        RequestType requestType = RequestType.PERMIT_TRANSFER;
        Request request = Request.builder()
            .id(requestId)
            .competentAuthority(competentAuthority)
            .type(requestType)
            .build();

        when(paymentFeeService.getFeeMethodType(competentAuthority, requestType)).thenReturn(Optional.empty());

        BigDecimal actualAmount = paymentAmountService.getAmount(request);

        assertEquals(BigDecimal.ZERO, actualAmount);
        verify(paymentFeeService, times(1)).getFeeMethodType(competentAuthority, requestType);
        verifyNoMoreInteractions(requestService, standardFeePaymentService);
    }

    @Test
    void getAmount_no_service_found() {
        String requestId = "1";
        CompetentAuthority competentAuthority = CompetentAuthority.WALES;
        RequestType requestType = RequestType.PERMIT_TRANSFER;
        Request request = Request.builder()
            .id(requestId)
            .competentAuthority(competentAuthority)
            .type(requestType)
            .build();
        FeeMethodType feeMethodType = FeeMethodType.INSTALLATION_CATEGORY_BASED;

        when(paymentFeeService.getFeeMethodType(competentAuthority, requestType)).thenReturn(Optional.of(feeMethodType));
        when(standardFeePaymentService.getFeeMethodType()).thenReturn(FeeMethodType.STANDARD);

        BusinessException be = assertThrows(BusinessException.class, () -> paymentAmountService.getAmount(request));

        assertEquals(ErrorCode.FEE_CONFIGURATION_NOT_EXIST, be.getErrorCode());

        verify(paymentFeeService, times(1)).getFeeMethodType(competentAuthority, requestType);
        verify(standardFeePaymentService, times(1)).getFeeMethodType();
        verifyNoMoreInteractions(requestService, standardFeePaymentService);
    }
}