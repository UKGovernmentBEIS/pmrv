package uk.gov.pmrv.api.workflow.payment.service;

import java.math.BigDecimal;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.payment.domain.PaymentFeeMethod;
import uk.gov.pmrv.api.workflow.payment.domain.enumeration.FeeType;
import uk.gov.pmrv.api.workflow.payment.repository.PaymentFeeMethodRepository;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;

@Service
@RequiredArgsConstructor
public abstract class PaymentService implements FeePaymentService {

    private final PaymentFeeMethodRepository paymentFeeMethodRepository;

    protected abstract FeeType resolveFeeType(Request request);

    @Override
    public BigDecimal getAmount(Request request) {
        PaymentFeeMethod paymentFeeMethod = paymentFeeMethodRepository
            .findByCompetentAuthorityAndRequestTypeAndType(request.getCompetentAuthority(), request.getType(), this.getFeeMethodType())
            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

        Map<FeeType, BigDecimal> fees = paymentFeeMethod.getFees();
        FeeType feeType = resolveFeeType(request);

        if(!fees.containsKey(feeType)) {
            throw new BusinessException(ErrorCode.FEE_CONFIGURATION_NOT_EXIST, request.getCompetentAuthority(), request.getType(), feeType);
        }

        return fees.get(feeType);
    }
}
