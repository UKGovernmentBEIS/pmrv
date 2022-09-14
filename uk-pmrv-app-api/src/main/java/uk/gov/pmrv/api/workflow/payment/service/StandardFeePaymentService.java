package uk.gov.pmrv.api.workflow.payment.service;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.payment.domain.enumeration.FeeMethodType;
import uk.gov.pmrv.api.workflow.payment.domain.enumeration.FeeType;
import uk.gov.pmrv.api.workflow.payment.repository.PaymentFeeMethodRepository;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;

@Service
public class StandardFeePaymentService extends PaymentService {

    public StandardFeePaymentService(
        PaymentFeeMethodRepository paymentFeeMethodRepository) {
        super(paymentFeeMethodRepository);
    }

    @Override
    public FeeMethodType getFeeMethodType() {
        return FeeMethodType.STANDARD;
    }

    @Override
    protected FeeType resolveFeeType(Request request) {
        return FeeType.FIXED;
    }
}
