package uk.gov.pmrv.api.workflow.request.flow.payment.handler;

import java.time.LocalDate;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.workflow.payment.domain.enumeration.PaymentMethodType;
import uk.gov.pmrv.api.workflow.payment.service.BankAccountDetailsService;
import uk.gov.pmrv.api.workflow.payment.service.PaymentMethodService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.payment.domain.PaymentMakeRequestTaskPayload;

@Service
@RequiredArgsConstructor
public class MakePaymentInitializer implements InitializeRequestTaskHandler {

    private final PaymentMethodService paymentMethodService;
    private final BankAccountDetailsService bankAccountDetailsService;

    @Override
    public RequestTaskPayload initializePayload(Request request) {
        CompetentAuthority competentAuthority = request.getCompetentAuthority();
        Set<PaymentMethodType> paymentMethodTypes = paymentMethodService.getPaymentMethodTypesByCa(competentAuthority);

        PaymentMakeRequestTaskPayload paymentMakeRequestTaskPayload = PaymentMakeRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.PAYMENT_MAKE_PAYLOAD)
            .amount(request.getPayload().getPaymentAmount())
            .paymentRefNum(request.getId())
            .creationDate(LocalDate.now())
            .paymentMethodTypes(paymentMethodTypes)
            .build();
        
        if(paymentMethodTypes.contains(PaymentMethodType.BANK_TRANSFER)) {
            paymentMakeRequestTaskPayload.setBankAccountDetails(bankAccountDetailsService.getBankAccountDetailsByCa(competentAuthority));
        }

        return paymentMakeRequestTaskPayload;
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return RequestTaskType.getMakePaymentTypes();
    }
}
