package uk.gov.pmrv.api.workflow.payment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.workflow.payment.domain.PaymentFeeMethod;
import uk.gov.pmrv.api.workflow.payment.domain.enumeration.FeeMethodType;
import uk.gov.pmrv.api.workflow.payment.repository.PaymentFeeMethodRepository;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentFeeMethodService {

    private final PaymentFeeMethodRepository paymentFeeMethodRepository;

    public Optional<FeeMethodType> getFeeMethodType(CompetentAuthority competentAuthority, RequestType requestType) {
        return paymentFeeMethodRepository.findByCompetentAuthorityAndRequestType(competentAuthority, requestType)
                .map(PaymentFeeMethod::getType)
                .or(Optional::empty);
    }
}
