package uk.gov.pmrv.api.workflow.request.flow.payment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.payment.domain.enumeration.FeeMethodType;
import uk.gov.pmrv.api.workflow.payment.service.FeePaymentService;
import uk.gov.pmrv.api.workflow.payment.service.PaymentFeeMethodService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestPayload;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentAmountService {

    private final RequestService requestService;
    private final PaymentFeeMethodService paymentFeeMethodService;
    private final List<FeePaymentService> feePaymentServices;

    public BigDecimal getAmount(Request request) {
        final Optional<FeeMethodType> feeMethodType = paymentFeeMethodService.getFeeMethodType(request.getCompetentAuthority(), request.getType());
        return feeMethodType.map(type -> {
                    BigDecimal amount = getFeeAmountService(type)
                            .map(service -> service.getAmount(request))
                            .orElseThrow(() -> new BusinessException(ErrorCode.FEE_CONFIGURATION_NOT_EXIST));

                    setPaymentAmountToRequestPayload(request, amount);

                    return amount;
                })
                .orElse(BigDecimal.ZERO);
    }

    private Optional<FeePaymentService> getFeeAmountService(FeeMethodType feeMethodType) {
        return feePaymentServices.stream()
                .filter(service -> feeMethodType.equals(service.getFeeMethodType()))
                .findAny();
    }

    private void setPaymentAmountToRequestPayload(Request request, BigDecimal amount) {
        RequestPayload requestPayload = request.getPayload();
        requestPayload.setPaymentAmount(amount);
        requestService.saveRequest(request);
    }
}
