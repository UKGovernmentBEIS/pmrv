package uk.gov.pmrv.api.workflow.request.flow.permitnotification.handler;

import org.mapstruct.factory.Mappers;

import org.springframework.stereotype.Service;

import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.mapper.PermitNotificationMapper;

import java.util.Set;

@Service
public class PermitNotificationApplicationReviewInitializer implements InitializeRequestTaskHandler {

    private static final PermitNotificationMapper permitNotificationMapper = Mappers.getMapper(PermitNotificationMapper.class);

    @Override
    public RequestTaskPayload initializePayload(Request request) {
        return permitNotificationMapper.toApplicationReviewRequestTaskPayload(
                (PermitNotificationRequestPayload) request.getPayload(),
                RequestTaskPayloadType.PERMIT_NOTIFICATION_APPLICATION_REVIEW_PAYLOAD
        );
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.PERMIT_NOTIFICATION_APPLICATION_REVIEW);
    }
}
