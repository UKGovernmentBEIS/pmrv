package uk.gov.pmrv.api.workflow.payment.service;

import java.math.BigDecimal;
import uk.gov.pmrv.api.workflow.payment.domain.enumeration.FeeMethodType;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;

public interface FeePaymentService {

    BigDecimal getAmount(Request request);
    FeeMethodType getFeeMethodType();
}
