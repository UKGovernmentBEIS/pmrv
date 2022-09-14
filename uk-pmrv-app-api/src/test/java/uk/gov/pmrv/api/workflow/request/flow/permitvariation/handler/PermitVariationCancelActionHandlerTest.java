package uk.gov.pmrv.api.workflow.request.flow.permitvariation.handler;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestTaskActionEmptyPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitvariation.domain.PermitVariationSubmitOutcome;

@ExtendWith(MockitoExtension.class)
class PermitVariationCancelActionHandlerTest {


    @InjectMocks
    private PermitVariationCancelActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private WorkflowService workflowService;

    @Test
    void process() {

        final PmrvUser pmrvUser = PmrvUser.builder().userId("userId").roleType(RoleType.OPERATOR).build();
        final String processTaskId = "processTaskId";
        final Request request = Request.builder()
            .id("requestId")
            .build();
        final RequestTask requestTask = RequestTask.builder()
            .id(1L)
            .request(request)
            .processTaskId(processTaskId)
            .build();

        when(requestTaskService.findTaskById(1L)).thenReturn(requestTask);

        //invoke
        handler.process(requestTask.getId(),
            RequestTaskActionType.PERMIT_VARIATION_CANCEL_APPLICATION,
            pmrvUser,
            RequestTaskActionEmptyPayload.builder().build());

        verify(requestTaskService, times(1)).findTaskById(requestTask.getId());

        verify(workflowService, times(1)).completeTask(processTaskId,
            Map.of(BpmnProcessConstants.PERMIT_VARIATION_SUBMIT_OUTCOME, PermitVariationSubmitOutcome.CANCELLED,
                   BpmnProcessConstants.REQUEST_INITIATOR_ROLE_TYPE, RoleType.OPERATOR));
    }
}
