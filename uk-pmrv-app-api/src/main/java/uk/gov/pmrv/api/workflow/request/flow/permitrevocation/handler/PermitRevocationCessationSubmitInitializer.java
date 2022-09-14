package uk.gov.pmrv.api.workflow.request.flow.permitrevocation.handler;

import java.util.Set;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.permit.cessation.PermitCessationContainer;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.permit.cessation.PermitCessationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitrevocation.domain.PermitRevocationRequestPayload;

@Service
public class PermitRevocationCessationSubmitInitializer implements InitializeRequestTaskHandler {

    @Override
    public RequestTaskPayload initializePayload(final Request request) {
        
        final PermitRevocationRequestPayload requestPayload = 
            (PermitRevocationRequestPayload) request.getPayload();

        final PermitCessationContainer cessationContainer = PermitCessationContainer.builder()
            .allowancesSurrenderRequired(requestPayload.getPermitRevocation().getSurrenderRequired())
            .build();

        return PermitCessationSubmitRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.PERMIT_REVOCATION_CESSATION_SUBMIT_PAYLOAD)
            .cessationContainer(cessationContainer)
            .build();
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.PERMIT_REVOCATION_CESSATION_SUBMIT);
    }
}
