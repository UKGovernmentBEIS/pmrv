package uk.gov.pmrv.api.workflow.request.flow.permitnotification.handler;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationFollowUpWaitForAmendsRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationRequestPayload;

@Service
public class PermitNotificationFollowUpWaitForAmendsInitializer implements InitializeRequestTaskHandler {

    @Override
    public RequestTaskPayload initializePayload(final Request request) {

        final PermitNotificationRequestPayload requestPayload = (PermitNotificationRequestPayload) request.getPayload();
        final Set<UUID> followUpResponseFiles = requestPayload.getFollowUpResponseFiles();
        final Set<UUID> amendFiles = requestPayload.getFollowUpReviewDecision().getFiles();
        final Map<UUID, String> followUpResponseAttachments = requestPayload.getFollowUpResponseAttachments()
            .entrySet().stream()
            .filter(e -> amendFiles.contains(e.getKey()) || followUpResponseFiles.contains(e.getKey()))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        
        return PermitNotificationFollowUpWaitForAmendsRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.PERMIT_NOTIFICATION_FOLLOW_UP_WAIT_FOR_AMENDS_PAYLOAD)
            .followUpRequest(requestPayload.getReviewDecision().getFollowUp().getFollowUpRequest())
            .followUpResponse(requestPayload.getFollowUpResponse())
            .followUpFiles(followUpResponseFiles)
            .reviewDecision(requestPayload.getFollowUpReviewDecision())
            .followUpResponseAttachments(followUpResponseAttachments)
            .build();
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.PERMIT_NOTIFICATION_FOLLOW_UP_WAIT_FOR_AMENDS);
    }
}
