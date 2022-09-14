package uk.gov.pmrv.api.workflow.request.flow.permitnotification.handler;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestTaskActionEmptyPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationFollowUpRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationFollowUpResponseSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.service.PermitNotificationValidatorService;

@RequiredArgsConstructor
@Component
public class PermitNotificationFollowUpSubmitResponseActionHandler
    implements RequestTaskActionHandler<RequestTaskActionEmptyPayload> {

    private final RequestTaskService requestTaskService;
    private final PermitNotificationValidatorService validatorService;
    private final RequestService requestService;
    private final WorkflowService workflowService;

    @Override
    public void process(final Long requestTaskId,
                        final RequestTaskActionType requestTaskActionType,
                        final PmrvUser pmrvUser,
                        final RequestTaskActionEmptyPayload taskActionPayload) {
        
        // validate task payload
        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        final PermitNotificationFollowUpRequestTaskPayload taskPayload =
            (PermitNotificationFollowUpRequestTaskPayload) requestTask.getPayload();
        validatorService.validateFollowUpResponse(taskPayload);
        
        // update request payload
        final Request request = requestTask.getRequest();
        final PermitNotificationRequestPayload requestPayload = (PermitNotificationRequestPayload) request.getPayload();
        requestPayload.setFollowUpResponse(taskPayload.getFollowUpResponse());
        requestPayload.setFollowUpResponseFiles(taskPayload.getFollowUpFiles());
        requestPayload.setFollowUpResponseAttachments(taskPayload.getFollowUpAttachments());
        
        // create timeline action
        final PermitNotificationFollowUpResponseSubmittedRequestActionPayload actionPayload =
            PermitNotificationFollowUpResponseSubmittedRequestActionPayload.builder()
                .payloadType(RequestActionPayloadType.PERMIT_NOTIFICATION_FOLLOW_UP_RESPONSE_SUBMITTED_PAYLOAD)
                .request(taskPayload.getFollowUpRequest())
                .response(taskPayload.getFollowUpResponse())
                .responseFiles(taskPayload.getFollowUpFiles())
                .responseAttachments(taskPayload.getFollowUpAttachments())
                .build();
        
        requestService.addActionToRequest(request,
            actionPayload,
            RequestActionType.PERMIT_NOTIFICATION_FOLLOW_UP_RESPONSE_SUBMITTED,
            requestPayload.getOperatorAssignee());
        
        // complete task and change request type so that the amend tasks that will be generated will be follow-up related  
        workflowService.completeTask(requestTask.getProcessTaskId(),
            Map.of(BpmnProcessConstants.REQUEST_TYPE, RequestType.PERMIT_NOTIFICATION_FOLLOW_UP.name()));
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.PERMIT_NOTIFICATION_FOLLOW_UP_SUBMIT_RESPONSE);
    }
}
