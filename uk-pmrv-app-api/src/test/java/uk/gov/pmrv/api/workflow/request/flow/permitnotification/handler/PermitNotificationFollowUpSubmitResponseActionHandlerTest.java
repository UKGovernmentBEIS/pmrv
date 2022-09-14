package uk.gov.pmrv.api.workflow.request.flow.permitnotification.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestTaskActionEmptyPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationFollowUpRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationFollowUpResponseSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.service.PermitNotificationValidatorService;

@ExtendWith(MockitoExtension.class)
class PermitNotificationFollowUpSubmitResponseActionHandlerTest {

    @InjectMocks
    private PermitNotificationFollowUpSubmitResponseActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private RequestService requestService;

    @Mock
    private PermitNotificationValidatorService validatorService;

    @Mock
    private WorkflowService workflowService;

    @Test
    void process() {

        final PmrvUser pmrvUser = PmrvUser.builder().userId("userId").build();
        final String processTaskId = "processTaskId";
        final UUID file = UUID.randomUUID();
        final PermitNotificationFollowUpRequestTaskPayload taskPayload =
            PermitNotificationFollowUpRequestTaskPayload.builder()
                .followUpResponse("the response")
                .followUpFiles(Set.of(file))
                .followUpAttachments(Map.of(file, "filename"))
                .build();
        final String requestId = "requestId";
        final Request request =
            Request.builder().id(requestId).payload(PermitNotificationRequestPayload.builder()
                    .operatorAssignee("operator")
                    .build())
                .build();
        final RequestTask requestTask = RequestTask.builder()
            .id(1L)
            .processTaskId(processTaskId)
            .payload(taskPayload)
            .request(request)
            .build();
        final PermitNotificationFollowUpResponseSubmittedRequestActionPayload actionPayload =
            PermitNotificationFollowUpResponseSubmittedRequestActionPayload.builder()
                .payloadType(RequestActionPayloadType.PERMIT_NOTIFICATION_FOLLOW_UP_RESPONSE_SUBMITTED_PAYLOAD)
                .response("the response")
                .responseFiles(Set.of(file))
                .responseAttachments(Map.of(file, "filename"))
                .build();

        when(requestTaskService.findTaskById(1L)).thenReturn(requestTask);

        // Invoke
        handler.process(requestTask.getId(),
            RequestTaskActionType.PERMIT_NOTIFICATION_NOTIFY_OPERATOR_FOR_DECISION,
            pmrvUser,
            RequestTaskActionEmptyPayload.builder().build());

        // Verify
        verify(requestTaskService, times(1)).findTaskById(requestTask.getId());
        verify(validatorService, times(1)).validateFollowUpResponse(taskPayload);
        verify(requestService, times(1)).addActionToRequest(request, 
            actionPayload,
            RequestActionType.PERMIT_NOTIFICATION_FOLLOW_UP_RESPONSE_SUBMITTED, 
            "operator");
        verify(workflowService, times(1)).completeTask(processTaskId,
            Map.of(BpmnProcessConstants.REQUEST_TYPE, RequestType.PERMIT_NOTIFICATION_FOLLOW_UP.name()));

        final PermitNotificationRequestPayload requestPayload =
            (PermitNotificationRequestPayload) requestTask.getRequest().getPayload();
        assertThat(requestPayload.getFollowUpResponse()).isEqualTo("the response");
        assertThat(requestPayload.getFollowUpResponseFiles()).isEqualTo(Set.of(file));
        assertThat(requestPayload.getFollowUpResponseAttachments()).isEqualTo(Map.of(file, "filename"));
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes()).containsExactly(
            RequestTaskActionType.PERMIT_NOTIFICATION_FOLLOW_UP_SUBMIT_RESPONSE);
    }
}
