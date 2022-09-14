package uk.gov.pmrv.api.workflow.request.flow.permitnotification.handler;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationFollowUpApplicationAmendsSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationFollowUpSubmitApplicationAmendRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationRequestPayload;

@Service
@RequiredArgsConstructor
public class PermitNotificationFollowUpApplicationAmendSubmitActionHandler implements
    RequestTaskActionHandler<PermitNotificationFollowUpSubmitApplicationAmendRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final RequestService requestService;
    private final WorkflowService workflowService;

    @Override
    public void process(final Long requestTaskId,
                        final RequestTaskActionType requestTaskActionType,
                        final PmrvUser pmrvUser,
                        final PermitNotificationFollowUpSubmitApplicationAmendRequestTaskActionPayload actionPayload) {

        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        final PermitNotificationFollowUpApplicationAmendsSubmitRequestTaskPayload taskPayload =
            (PermitNotificationFollowUpApplicationAmendsSubmitRequestTaskPayload) requestTask.getPayload();
        
        // update task payload
        taskPayload.setFollowUpSectionsCompleted(actionPayload.getFollowUpSectionsCompleted());
        
        // update request payload
        final Request request = requestTask.getRequest();
        final PermitNotificationRequestPayload requestPayload = (PermitNotificationRequestPayload) request.getPayload();
        requestPayload.setFollowUpResponse(taskPayload.getFollowUpResponse());
        requestPayload.setFollowUpResponseFiles(taskPayload.getFollowUpFiles());
        requestPayload.setFollowUpResponseAttachments(taskPayload.getFollowUpAttachments());
        requestPayload.setFollowUpReviewSectionsCompleted(taskPayload.getReviewSectionsCompleted());
        requestPayload.setFollowUpSectionsCompleted(taskPayload.getFollowUpSectionsCompleted());

        // create timeline action
        requestService.addActionToRequest(
            requestTask.getRequest(),
            null,
            RequestActionType.PERMIT_NOTIFICATION_FOLLOW_UP_APPLICATION_AMENDS_SUBMITTED,
            pmrvUser.getUserId());
            
        // complete task
        workflowService.completeTask(requestTask.getProcessTaskId());
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.PERMIT_NOTIFICATION_FOLLOW_UP_SUBMIT_APPLICATION_AMEND);
    }
}
