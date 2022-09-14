package uk.gov.pmrv.api.workflow.payment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.workflow.payment.domain.PaymentMethod;
import uk.gov.pmrv.api.workflow.payment.domain.enumeration.PaymentMethodType;
import uk.gov.pmrv.api.workflow.payment.repository.PaymentMethodRepository;

@ExtendWith(MockitoExtension.class)
class PaymentMethodServiceTest {

    @InjectMocks
    private PaymentMethodService paymentMethodService;

    @Mock
    private PaymentMethodRepository paymentMethodRepository;

    @Test
    void getPaymentMethodTypesByCa() {
        CompetentAuthority competentAuthority = CompetentAuthority.ENGLAND;
        PaymentMethod bankTransferPaymentMethod = PaymentMethod.builder()
            .competentAuthority(competentAuthority)
            .type(PaymentMethodType.BANK_TRANSFER)
            .build();
        PaymentMethod creditOrDebitCardPaymentMethodEngland = PaymentMethod.builder()
            .competentAuthority(competentAuthority)
            .type(PaymentMethodType.CREDIT_OR_DEBIT_CARD)
            .build();

        when(paymentMethodRepository.findByCompetentAuthority(competentAuthority))
            .thenReturn(List.of(bankTransferPaymentMethod, creditOrDebitCardPaymentMethodEngland));

        Set<PaymentMethodType> paymentMethodTypes =
            paymentMethodService.getPaymentMethodTypesByCa(competentAuthority);

        assertThat(paymentMethodTypes).isNotEmpty();
        assertThat(paymentMethodTypes)
            .containsExactlyInAnyOrder(PaymentMethodType.BANK_TRANSFER, PaymentMethodType.CREDIT_OR_DEBIT_CARD);

    }
}