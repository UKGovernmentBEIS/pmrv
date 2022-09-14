package uk.gov.pmrv.api.workflow.request.flow.permitnotification.handler;

import java.util.Set;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationFollowUpApplicationAmendsSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationFollowUpReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.mapper.PermitNotificationMapper;

@Service
public class PermitNotificationFollowUpApplicationAmendsSubmitInitializer implements InitializeRequestTaskHandler {
    
    private static final PermitNotificationMapper PERMIT_NOTIFICATION_MAPPER = Mappers.getMapper(PermitNotificationMapper.class);
    
    @Override
    public RequestTaskPayload initializePayload(final Request request) {

        final PermitNotificationRequestPayload requestPayload = (PermitNotificationRequestPayload) request.getPayload();
        final PermitNotificationFollowUpReviewDecision reviewDecision =
            PERMIT_NOTIFICATION_MAPPER.cloneFollowUpReviewDecisionIgnoreNotes(requestPayload.getFollowUpReviewDecision());

        return PermitNotificationFollowUpApplicationAmendsSubmitRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.PERMIT_NOTIFICATION_FOLLOW_UP_APPLICATION_AMENDS_SUBMIT_PAYLOAD)
            .followUpRequest(requestPayload.getReviewDecision().getFollowUp().getFollowUpRequest())
            .followUpResponseExpirationDate(requestPayload.getReviewDecision().getFollowUp().getFollowUpResponseExpirationDate())
            .followUpResponse(requestPayload.getFollowUpResponse())
            .followUpFiles(requestPayload.getFollowUpResponseFiles())
            .followUpAttachments(requestPayload.getFollowUpResponseAttachments())
            .permitNotificationType(requestPayload.getPermitNotification().getType())
            .submissionDate(requestPayload.getFollowUpResponseSubmissionDate())
            .reviewDecision(reviewDecision)
            .reviewSectionsCompleted(requestPayload.getFollowUpReviewSectionsCompleted())
            .build();
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.PERMIT_NOTIFICATION_FOLLOW_UP_APPLICATION_AMENDS_SUBMIT);
    }
}
