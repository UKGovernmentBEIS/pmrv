package uk.gov.pmrv.api.workflow.payment.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.payment.domain.PaymentFeeMethod;
import uk.gov.pmrv.api.workflow.payment.domain.enumeration.FeeMethodType;
import uk.gov.pmrv.api.workflow.payment.repository.PaymentFeeMethodRepository;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;

@ExtendWith(MockitoExtension.class)
class PaymentFeeMethodServiceTest {

    @InjectMocks
    private PaymentFeeMethodService paymentFeeService;

    @Mock
    private PaymentFeeMethodRepository paymentFeeMethodRepository;

    @Test
    void getMethodFeeType() {
        CompetentAuthority competentAuthority = CompetentAuthority.ENGLAND;
        RequestType requestType = RequestType.PERMIT_ISSUANCE;
        PaymentFeeMethod paymentFeeMethod = PaymentFeeMethod.builder()
            .competentAuthority(competentAuthority)
            .requestType(requestType)
            .type(FeeMethodType.STANDARD)
            .build();

        when(paymentFeeMethodRepository.findByCompetentAuthorityAndRequestType(competentAuthority, requestType))
            .thenReturn(Optional.of(paymentFeeMethod));

        Optional<FeeMethodType> feeMethodType =
            paymentFeeService.getFeeMethodType(competentAuthority, requestType);

        assertEquals(Optional.of(FeeMethodType.STANDARD), feeMethodType);

    }

    @Test
    void getMethodFeeType_not_found() {
        CompetentAuthority competentAuthority = CompetentAuthority.ENGLAND;
        RequestType requestType = RequestType.PERMIT_ISSUANCE;

        when(paymentFeeMethodRepository.findByCompetentAuthorityAndRequestType(competentAuthority, requestType))
            .thenReturn(Optional.empty());

        Optional<FeeMethodType> feeMethodType =
                paymentFeeService.getFeeMethodType(competentAuthority, requestType);

        assertTrue(feeMethodType.isEmpty());
    }
}