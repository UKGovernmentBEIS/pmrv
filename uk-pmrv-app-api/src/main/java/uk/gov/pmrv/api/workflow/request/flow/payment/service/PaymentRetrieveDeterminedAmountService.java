package uk.gov.pmrv.api.workflow.request.flow.payment.service;

import java.math.BigDecimal;
import java.util.Set;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;

@Service
public class PaymentRetrieveDeterminedAmountService implements PaymentDetermineAmountService {

    @Override
    public BigDecimal determineAmount(Request request) {
        RequestPayload requestPayload = request.getPayload();
        BigDecimal paymentAmount = requestPayload.getPaymentAmount();
        return paymentAmount != null ? paymentAmount : BigDecimal.ZERO;
    }

    @Override
    public Set<RequestType> getRequestTypes() {
        return Set.of(RequestType.PERMIT_REVOCATION);
    }
}
