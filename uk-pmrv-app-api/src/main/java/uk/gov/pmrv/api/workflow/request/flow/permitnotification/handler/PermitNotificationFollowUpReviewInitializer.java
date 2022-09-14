package uk.gov.pmrv.api.workflow.request.flow.permitnotification.handler;

import java.time.LocalDate;
import java.util.Set;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.FollowUp;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationFollowUpApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationRequestPayload;

@Service
public class PermitNotificationFollowUpReviewInitializer implements InitializeRequestTaskHandler {

    @Override
    public RequestTaskPayload initializePayload(final Request request) {

        final PermitNotificationRequestPayload requestPayload = (PermitNotificationRequestPayload) request.getPayload();
        final FollowUp followUp = requestPayload.getReviewDecision().getFollowUp();

        return PermitNotificationFollowUpApplicationReviewRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.PERMIT_NOTIFICATION_FOLLOW_UP_APPLICATION_REVIEW_PAYLOAD)
            .permitNotificationType(requestPayload.getPermitNotification().getType())
            .submissionDate(LocalDate.now())
            .followUpRequest(followUp.getFollowUpRequest())
            .followUpResponseExpirationDate(followUp.getFollowUpResponseExpirationDate())
            .followUpResponse(requestPayload.getFollowUpResponse())
            .followUpFiles(requestPayload.getFollowUpResponseFiles())
            .followUpAttachments(requestPayload.getFollowUpResponseAttachments())
            .reviewDecision(requestPayload.getFollowUpReviewDecision())
            .reviewSectionsCompleted(requestPayload.getFollowUpReviewSectionsCompleted())
            .followUpSectionsCompleted(requestPayload.getFollowUpSectionsCompleted())
            .build();
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.PERMIT_NOTIFICATION_FOLLOW_UP_APPLICATION_REVIEW);
    }
}
