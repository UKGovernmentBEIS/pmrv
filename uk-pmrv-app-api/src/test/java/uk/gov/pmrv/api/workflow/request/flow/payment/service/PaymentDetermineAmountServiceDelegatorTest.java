package uk.gov.pmrv.api.workflow.request.flow.payment.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;

@ExtendWith(MockitoExtension.class)
class PaymentDetermineAmountServiceDelegatorTest {

    private PaymentDetermineAmountServiceDelegator paymentDetermineAmountServiceDelegator;

    private PaymentRetrieveDeterminedAmountService paymentRetrieveDeterminedAmountService;
    private RequestService requestService;
    private PaymentAmountService paymentAmountService;

    @BeforeEach
    void setUp() {
        paymentRetrieveDeterminedAmountService = mock(PaymentRetrieveDeterminedAmountService.class);
        requestService = mock(RequestService.class);
        paymentAmountService = mock(PaymentAmountService.class);
        paymentDetermineAmountServiceDelegator = new PaymentDetermineAmountServiceDelegator(
            requestService,
            paymentAmountService,
            List.of(paymentRetrieveDeterminedAmountService));

        when(paymentRetrieveDeterminedAmountService.getRequestTypes()).thenReturn(Set.of(RequestType.PERMIT_REVOCATION));
    }

    @Test
    void determineAmount() {
        String requestId = "AEM-123-4";
        Request request = Request.builder().id(requestId).type(RequestType.PERMIT_ISSUANCE).build();
        BigDecimal amount = BigDecimal.valueOf(500);

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(paymentAmountService.getAmount(request)).thenReturn(amount);

        assertEquals(amount, paymentDetermineAmountServiceDelegator.determineAmount(requestId));

        verify(paymentRetrieveDeterminedAmountService, never()).determineAmount(any());
    }

    @Test
    void determineAmount_retrieve() {
        String requestId = "AEM-123-4";
        Request request = Request.builder().id(requestId).type(RequestType.PERMIT_REVOCATION).build();
        BigDecimal amount = BigDecimal.valueOf(500);

        when(requestService.findRequestById(requestId)).thenReturn(request);

        when(paymentRetrieveDeterminedAmountService.determineAmount(request)).thenReturn(amount);

        assertEquals(amount, paymentDetermineAmountServiceDelegator.determineAmount(requestId));

        verify(paymentAmountService, never()).getAmount(any());
    }
}