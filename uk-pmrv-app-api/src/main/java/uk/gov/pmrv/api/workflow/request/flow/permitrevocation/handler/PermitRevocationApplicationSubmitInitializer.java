package uk.gov.pmrv.api.workflow.request.flow.permitrevocation.handler;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.payment.service.PaymentAmountService;
import uk.gov.pmrv.api.workflow.request.flow.permitrevocation.domain.PermitRevocationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitrevocation.mapper.PermitRevocationMapper;

@Service
@RequiredArgsConstructor
public class PermitRevocationApplicationSubmitInitializer implements InitializeRequestTaskHandler {

    private static final PermitRevocationMapper PERMIT_REVOCATION_MAPPER = Mappers.getMapper(PermitRevocationMapper.class);
    
    private final PaymentAmountService paymentAmountService;

    @Override
    public RequestTaskPayload initializePayload(Request request) {

        return PERMIT_REVOCATION_MAPPER.toApplicationSubmitRequestTaskPayload(
            (PermitRevocationRequestPayload)request.getPayload(),
            paymentAmountService.getAmount(request)
        );
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.PERMIT_REVOCATION_APPLICATION_SUBMIT);
    }
}
