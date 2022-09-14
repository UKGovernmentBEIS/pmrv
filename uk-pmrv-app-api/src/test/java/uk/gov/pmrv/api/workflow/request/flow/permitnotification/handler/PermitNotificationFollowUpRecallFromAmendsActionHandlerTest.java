package uk.gov.pmrv.api.workflow.request.flow.permitnotification.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestTaskActionEmptyPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationRequestPayload;

@ExtendWith(MockitoExtension.class)
class PermitNotificationFollowUpRecallFromAmendsActionHandlerTest {

    @InjectMocks
    private PermitNotificationFollowUpRecallFromAmendsActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private RequestService requestService;

    @Mock
    private WorkflowService workflowService;

    @Test
    void process() {

        final PmrvUser pmrvUser = PmrvUser.builder().userId("userId").build();
        final String processTaskId = "processTaskId";
        final Request request =
            Request.builder().id("requestId").payload(PermitNotificationRequestPayload.builder().build()).build();
        final RequestTask requestTask = RequestTask.builder()
            .id(1L)
            .processTaskId(processTaskId)
            .request(request)
            .build();

        when(requestTaskService.findTaskById(1L)).thenReturn(requestTask);

        handler.process(requestTask.getId(),
            RequestTaskActionType.PERMIT_NOTIFICATION_FOLLOW_UP_RECALL_FROM_AMENDS,
            pmrvUser,
            RequestTaskActionEmptyPayload.builder().build());

        // Verify
        verify(requestTaskService, times(1)).findTaskById(requestTask.getId());
        verify(requestService, times(1)).addActionToRequest(request,
            null,
            RequestActionType.PERMIT_NOTIFICATION_FOLLOW_UP_RECALLED_FROM_AMENDS,
            "userId");
        verify(workflowService, times(1)).completeTask("processTaskId");
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes()).containsExactly(RequestTaskActionType.PERMIT_NOTIFICATION_FOLLOW_UP_RECALL_FROM_AMENDS);
    }
}
