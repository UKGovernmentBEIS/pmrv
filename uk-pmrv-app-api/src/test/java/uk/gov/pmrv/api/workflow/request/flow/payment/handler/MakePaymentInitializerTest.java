package uk.gov.pmrv.api.workflow.request.flow.payment.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.workflow.payment.domain.dto.BankAccountDetailsDTO;
import uk.gov.pmrv.api.workflow.payment.domain.enumeration.PaymentMethodType;
import uk.gov.pmrv.api.workflow.payment.service.BankAccountDetailsService;
import uk.gov.pmrv.api.workflow.payment.service.PaymentMethodService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.flow.payment.domain.PaymentMakeRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.common.domain.PermitIssuanceRequestPayload;

@ExtendWith(MockitoExtension.class)
class MakePaymentInitializerTest {

    @InjectMocks
    private MakePaymentInitializer makePaymentInitializer;

    @Mock
    private PaymentMethodService paymentMethodService;

    @Mock
    private BankAccountDetailsService bankAccountDetailsService;

    @Test
    void initializePayload() {
        CompetentAuthority competentAuthority = CompetentAuthority.WALES;
        BigDecimal paymentAmount = BigDecimal.valueOf(1500.25);
        PermitIssuanceRequestPayload requestPayload = PermitIssuanceRequestPayload.builder()
            .paymentAmount(paymentAmount)
            .build();
        Request request = Request.builder()
            .id("request-id-1")
            .competentAuthority(competentAuthority)
            .payload(requestPayload)
            .build();
        Set<PaymentMethodType> paymentMethodTypes = Set.of(PaymentMethodType.BANK_TRANSFER, PaymentMethodType.CREDIT_OR_DEBIT_CARD);
        BankAccountDetailsDTO bankAccountDetails = BankAccountDetailsDTO.builder()
            .accountName("accountName")
            .sortCode("sortCode")
            .accountNumber("accountNumber")
            .iban("iban")
            .swiftCode("swiftCode")
            .build();

        when(paymentMethodService.getPaymentMethodTypesByCa(request.getCompetentAuthority())).thenReturn(paymentMethodTypes);
        when(bankAccountDetailsService.getBankAccountDetailsByCa(request.getCompetentAuthority())).thenReturn(bankAccountDetails);

        RequestTaskPayload requestTaskPayload = makePaymentInitializer.initializePayload(request);
        assertThat(requestTaskPayload.getPayloadType()).isEqualTo(RequestTaskPayloadType.PAYMENT_MAKE_PAYLOAD);
        assertThat(requestTaskPayload).isInstanceOf(PaymentMakeRequestTaskPayload.class);

        PaymentMakeRequestTaskPayload paymentMakeRequestTaskPayload = (PaymentMakeRequestTaskPayload) requestTaskPayload;

        assertEquals(request.getId(), paymentMakeRequestTaskPayload.getPaymentRefNum());
        assertEquals(paymentAmount, paymentMakeRequestTaskPayload.getAmount());
        assertEquals(bankAccountDetails, paymentMakeRequestTaskPayload.getBankAccountDetails());
        assertThat(paymentMakeRequestTaskPayload.getPaymentMethodTypes()).containsAll(paymentMethodTypes);

        verify(paymentMethodService, times(1)).getPaymentMethodTypesByCa(competentAuthority);
        verify(bankAccountDetailsService, times(1)).getBankAccountDetailsByCa(competentAuthority);
    }

    @Test
    void initializePayload_no_bank_transfer_available() {
        CompetentAuthority competentAuthority = CompetentAuthority.WALES;
        BigDecimal paymentAmount = BigDecimal.valueOf(1500.25);
        PermitIssuanceRequestPayload requestPayload = PermitIssuanceRequestPayload.builder()
            .paymentAmount(paymentAmount)
            .build();
        Request request = Request.builder()
            .id("request-id-1")
            .competentAuthority(competentAuthority)
            .payload(requestPayload)
            .build();
        Set<PaymentMethodType> paymentMethodTypes = Set.of(PaymentMethodType.CREDIT_OR_DEBIT_CARD);

        when(paymentMethodService.getPaymentMethodTypesByCa(request.getCompetentAuthority())).thenReturn(paymentMethodTypes);

        RequestTaskPayload requestTaskPayload = makePaymentInitializer.initializePayload(request);
        assertThat(requestTaskPayload.getPayloadType()).isEqualTo(RequestTaskPayloadType.PAYMENT_MAKE_PAYLOAD);
        assertThat(requestTaskPayload).isInstanceOf(PaymentMakeRequestTaskPayload.class);

        PaymentMakeRequestTaskPayload paymentMakeRequestTaskPayload = (PaymentMakeRequestTaskPayload) requestTaskPayload;

        assertEquals(request.getId(), paymentMakeRequestTaskPayload.getPaymentRefNum());
        assertEquals(paymentAmount, paymentMakeRequestTaskPayload.getAmount());
        assertThat(paymentMakeRequestTaskPayload.getPaymentMethodTypes()).containsAll(paymentMethodTypes);
        assertNull(paymentMakeRequestTaskPayload.getBankAccountDetails());

        verify(paymentMethodService, times(1)).getPaymentMethodTypesByCa(competentAuthority);
        verifyNoInteractions(bankAccountDetailsService);
    }

    @Test
    void getRequestTaskTypes() {
        assertThat(makePaymentInitializer.getRequestTaskTypes()).containsExactlyElementsOf(RequestTaskType.getMakePaymentTypes());
    }
}