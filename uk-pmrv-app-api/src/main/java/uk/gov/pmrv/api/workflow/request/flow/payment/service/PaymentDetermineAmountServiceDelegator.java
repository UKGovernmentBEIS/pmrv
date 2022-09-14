package uk.gov.pmrv.api.workflow.request.flow.payment.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;

@Service
@RequiredArgsConstructor
public class PaymentDetermineAmountServiceDelegator {

    private final RequestService requestService;
    private final PaymentAmountService paymentAmountService;
    private final List<PaymentDetermineAmountService> paymentDetermineAmountServices;

    public BigDecimal determineAmount(String requestId) {
        Request request = requestService.findRequestById(requestId);
        return getDeterminePaymentAmountService(request.getType())
            .map(service -> service.determineAmount(request))
            .orElseGet(() -> paymentAmountService.getAmount(request));
    }

    private Optional<PaymentDetermineAmountService> getDeterminePaymentAmountService(RequestType requestType) {
        return paymentDetermineAmountServices.stream()
            .filter(service -> service.getRequestTypes().contains(requestType))
            .findAny();
    }
}
