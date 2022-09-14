package uk.gov.pmrv.api.workflow.request.flow.payment.service;

import java.math.BigDecimal;
import java.util.Set;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;

public interface PaymentDetermineAmountService {

    BigDecimal determineAmount(Request request);

    Set<RequestType> getRequestTypes();
}
