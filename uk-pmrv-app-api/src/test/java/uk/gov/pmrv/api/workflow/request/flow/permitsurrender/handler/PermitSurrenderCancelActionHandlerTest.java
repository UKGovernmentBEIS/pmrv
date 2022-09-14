package uk.gov.pmrv.api.workflow.request.flow.permitsurrender.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestTaskActionEmptyPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderOutcome;

@ExtendWith(MockitoExtension.class)
class PermitSurrenderCancelActionHandlerTest {

    @InjectMocks
    private PermitSurrenderCancelActionHandler handler;
    
    @Mock
    private WorkflowService workflowService;
    
    @Mock
    private RequestTaskService requestTaskService;

    @Test
    void process() {
        
        final RequestTaskActionEmptyPayload cancelPayload = RequestTaskActionEmptyPayload.builder().payloadType(RequestTaskActionPayloadType.EMPTY_PAYLOAD).build();
        final PmrvUser pmrvUser = PmrvUser.builder().build();
        final String processTaskId = "processTaskId";
        final RequestTask requestTask = RequestTask.builder().id(1L).processTaskId(processTaskId).build();

        when(requestTaskService.findTaskById(1L)).thenReturn(requestTask);
        
        handler.process(requestTask.getId(), RequestTaskActionType.PERMIT_SURRENDER_CANCEL_APPLICATION, pmrvUser, cancelPayload);

        verify(requestTaskService, times(1)).findTaskById(requestTask.getId());
        verify(workflowService, times(1)).completeTask(processTaskId,
            Map.of(BpmnProcessConstants.SURRENDER_OUTCOME, PermitSurrenderOutcome.CANCELLED));
    }
    
    @Test
    void getTypes() {
        assertThat(handler.getTypes()).containsExactly(RequestTaskActionType.PERMIT_SURRENDER_CANCEL_APPLICATION);
    }
}