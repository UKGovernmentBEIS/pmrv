package uk.gov.pmrv.api.workflow.request.flow.permitnotification.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestTaskTimeManagementService;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationFollowUpApplicationAmendsSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationFollowUpRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationFollowUpWaitForAmendsRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationWaitForFollowUpRequestTaskPayload;

@Service
@RequiredArgsConstructor
public class ExtendFollowUpExpirationTimerService {

    private final RequestService requestService;
    private final RequestTaskTimeManagementService requestTaskTimeManagementService;

    public void extendTimer(final String requestId,
                            final Date expirationDate,
                            final String expirationDateKey) {

        final Request request = requestService.findRequestById(requestId);
        final LocalDate dueDate = expirationDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        final List<RequestTask> requestTasks = 
            requestTaskTimeManagementService.setDueDateToTasks(requestId, expirationDateKey, dueDate);
        this.updateTaskPayloads(dueDate, requestTasks);
        this.updateRequestPayload(request, dueDate);
        
        requestService.addActionToRequest(request,
            null,
            RequestActionType.PERMIT_NOTIFICATION_FOLLOW_UP_DATE_EXTENDED,
            request.getPayload().getRegulatorAssignee());
    }

    private void updateRequestPayload(final Request request, final LocalDate dueDate) {

        // if this is called during the PERMIT_NOTIFICATION_FOLLOW_UP_WAIT_FOR_AMENDS task then we need to update
        // the due date of the follow-up review decision in the request payload.
        // if this is called during the PERMIT_NOTIFICATION_WAIT_FOR_FOLLOW_UP task then we need to update
        // the response expiration date of the follow-up in the request payload.
        final PermitNotificationRequestPayload requestPayload = (PermitNotificationRequestPayload) request.getPayload();
        if (requestPayload.getFollowUpReviewDecision() != null) {
            requestPayload.getFollowUpReviewDecision().setDueDate(dueDate);
        } else {
            requestPayload.getReviewDecision().getFollowUp().setFollowUpResponseExpirationDate(dueDate);    
        }
    }

    private void updateTaskPayloads(final LocalDate dueDate, final List<RequestTask> requestTasks) {
        
        requestTasks.stream().map(RequestTask::getPayload)
            .filter(p -> p.getPayloadType().equals(RequestTaskPayloadType.PERMIT_NOTIFICATION_WAIT_FOR_FOLLOW_UP_PAYLOAD))
            .forEach(p -> ((PermitNotificationWaitForFollowUpRequestTaskPayload)p).setFollowUpResponseExpirationDate(dueDate));

        requestTasks.stream().map(RequestTask::getPayload)
            .filter(p -> p.getPayloadType().equals(RequestTaskPayloadType.PERMIT_NOTIFICATION_FOLLOW_UP_PAYLOAD))
            .forEach(p -> ((PermitNotificationFollowUpRequestTaskPayload)p).setFollowUpResponseExpirationDate(dueDate));

        requestTasks.stream().map(RequestTask::getPayload)
            .filter(p -> p.getPayloadType().equals(RequestTaskPayloadType.PERMIT_NOTIFICATION_FOLLOW_UP_WAIT_FOR_AMENDS_PAYLOAD))
            .forEach(p -> ((PermitNotificationFollowUpWaitForAmendsRequestTaskPayload)p).getReviewDecision().setDueDate(dueDate));
        
        requestTasks.stream().map(RequestTask::getPayload)
            .filter(p -> p.getPayloadType().equals(RequestTaskPayloadType.PERMIT_NOTIFICATION_FOLLOW_UP_APPLICATION_AMENDS_SUBMIT_PAYLOAD))
            .forEach(p -> ((PermitNotificationFollowUpApplicationAmendsSubmitRequestTaskPayload)p).getReviewDecision().setDueDate(dueDate));
    }
}
