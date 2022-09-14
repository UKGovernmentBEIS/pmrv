package uk.gov.pmrv.api.workflow.request.flow.rde.handler;

import org.mapstruct.factory.Mappers;

import org.springframework.stereotype.Service;

import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.rde.domain.RdeResponsePayload;
import uk.gov.pmrv.api.workflow.request.flow.rde.domain.RdeResponseRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.rde.domain.RequestPayloadRdeable;
import uk.gov.pmrv.api.workflow.request.flow.rde.mapper.RdeMapper;

import java.util.Set;

@Service
public class RdeResponseSubmitInitializer implements InitializeRequestTaskHandler {

    private static final RdeMapper rdeMapper = Mappers.getMapper(RdeMapper.class);

    @Override
    public RequestTaskPayload initializePayload(final Request request) {
        final RequestPayloadRdeable requestPayload = (RequestPayloadRdeable) request.getPayload();
		final RdeResponsePayload rdeResponsePayload = rdeMapper.toRdeResponsePayload(
				requestPayload.getRdeData().getCurrentDueDate(),
				requestPayload.getRdeData().getRdePayload().getExtensionDate());

        return RdeResponseRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.RDE_RESPONSE_SUBMIT_PAYLOAD)
                .rdeResponsePayload(rdeResponsePayload)
                .build();
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return RequestTaskType.getRdeResponseTypes();
    }
}
