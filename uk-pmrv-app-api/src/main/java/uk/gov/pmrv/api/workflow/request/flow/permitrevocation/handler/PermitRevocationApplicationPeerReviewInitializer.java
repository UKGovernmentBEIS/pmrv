package uk.gov.pmrv.api.workflow.request.flow.permitrevocation.handler;

import java.util.Set;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.permitrevocation.domain.PermitRevocationApplicationPeerReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitrevocation.domain.PermitRevocationRequestPayload;

@Service
public class PermitRevocationApplicationPeerReviewInitializer implements InitializeRequestTaskHandler {

    @Override
    public RequestTaskPayload initializePayload(Request request) {

        final PermitRevocationRequestPayload requestPayload = (PermitRevocationRequestPayload) request.getPayload();
        return PermitRevocationApplicationPeerReviewRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.PERMIT_REVOCATION_APPLICATION_PEER_REVIEW_PAYLOAD)
            .permitRevocation(requestPayload.getPermitRevocation())
            .sectionsCompleted(requestPayload.getSectionsCompleted())
            .build();
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.PERMIT_REVOCATION_APPLICATION_PEER_REVIEW);
    }
}
