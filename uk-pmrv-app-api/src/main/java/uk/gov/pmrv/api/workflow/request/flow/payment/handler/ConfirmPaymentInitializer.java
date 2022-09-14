package uk.gov.pmrv.api.workflow.request.flow.payment.handler;

import java.util.Set;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.payment.domain.RequestPayloadPayable;
import uk.gov.pmrv.api.workflow.request.flow.payment.transform.PaymentPayloadMapper;

@Service
public class ConfirmPaymentInitializer implements InitializeRequestTaskHandler {

    private static final PaymentPayloadMapper PAYMENT_PAYLOAD_MAPPER = Mappers.getMapper(PaymentPayloadMapper.class);

    @Override
    public RequestTaskPayload initializePayload(Request request) {
        RequestPayloadPayable requestPayloadPayable = (RequestPayloadPayable) request.getPayload();
        return PAYMENT_PAYLOAD_MAPPER.toConfirmPaymentRequestTaskPayload(request.getId(), requestPayloadPayable.getRequestPaymentInfo());
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return RequestTaskType.getConfirmPaymentTypes();
    }
}
