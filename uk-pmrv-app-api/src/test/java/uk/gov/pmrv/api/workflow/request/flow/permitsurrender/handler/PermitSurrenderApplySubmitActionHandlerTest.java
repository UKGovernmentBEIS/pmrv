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
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestTaskActionEmptyPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderOutcome;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.service.RequestPermitSurrenderService;

@ExtendWith(MockitoExtension.class)
class PermitSurrenderApplySubmitActionHandlerTest {

    @InjectMocks
    private PermitSurrenderApplySubmitActionHandler handler;

    @Mock
    private RequestPermitSurrenderService requestPermitSurrenderService;
    
    @Mock
    private WorkflowService workflowService;
    
    @Mock
    private RequestTaskService requestTaskService;

    @Test
    void process() {
        RequestTaskActionEmptyPayload submitPayload = RequestTaskActionEmptyPayload.builder().payloadType(RequestTaskActionPayloadType.EMPTY_PAYLOAD).build();
        PmrvUser pmrvUser = PmrvUser.builder().build();
        String processTaskId = "processTaskId";
        Request request = Request.builder().id("1").build();
        RequestTask requestTask = RequestTask.builder().id(1L).request(request).processTaskId(processTaskId).build();

        when(requestTaskService.findTaskById(1L)).thenReturn(requestTask);
        
        //invoke
        handler.process(requestTask.getId(), RequestTaskActionType.PERMIT_SURRENDER_SUBMIT_APPLICATION, pmrvUser, submitPayload);

        assertThat(request.getSubmissionDate()).isNotNull();
        verify(requestTaskService, times(1)).findTaskById(requestTask.getId());
        verify(requestPermitSurrenderService, times(1)).applySubmitPayload(requestTask, pmrvUser);
        verify(workflowService, times(1)).completeTask(processTaskId,
            Map.of(BpmnProcessConstants.SURRENDER_OUTCOME, PermitSurrenderOutcome.SUBMITTED));
    }
    
    @Test
    void getTypes() {
        assertThat(handler.getTypes()).containsExactly(RequestTaskActionType.PERMIT_SURRENDER_SUBMIT_APPLICATION);
    }
}