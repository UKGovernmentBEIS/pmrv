package uk.gov.pmrv.api.workflow.request.flow.permitsurrender.handler;

import java.util.Set;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.mapper.PermitSurrenderMapper;

@Service
public class PermitSurrenderWaitForPeerReviewInitializer implements InitializeRequestTaskHandler {

    private static final PermitSurrenderMapper PERMIT_SURRENDER_MAPPER = Mappers.getMapper(PermitSurrenderMapper.class);

    @Override
    public RequestTaskPayload initializePayload(Request request) {
        return PERMIT_SURRENDER_MAPPER.toApplicationReviewRequestTaskPayload(
            (PermitSurrenderRequestPayload) request.getPayload(),
            RequestTaskPayloadType.PERMIT_SURRENDER_WAIT_FOR_PEER_REVIEW_PAYLOAD
        );
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.PERMIT_SURRENDER_WAIT_FOR_PEER_REVIEW);
    }
}
