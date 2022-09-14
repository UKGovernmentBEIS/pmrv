package uk.gov.pmrv.api.workflow.bpmn.listener;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@ExtendWith(MockitoExtension.class)
class ProcessInstanceEndListenerTest {

    @InjectMocks
    private ProcessInstanceEndListener handler;

    @Mock
    private RequestService requestService;

    @Mock
    private DelegateExecution execution;

    @Test
    void onProcessInstanceEndEventTest() {
        final String requestId = "1";
        final String processId = "1";
        when(execution.hasVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(true);
        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        when(execution.getProcessInstanceId()).thenReturn(processId);

        // Invoke
        handler.onProcessInstanceEndEvent(execution);

        // Verify
        verify(execution, times(1)).hasVariable(BpmnProcessConstants.REQUEST_ID);
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(execution, times(1)).getProcessInstanceId();
        verify(requestService, times(1)).terminateRequest(requestId, processId);
    }

    @Test
    void onProcessInstanceEndEventTest_no_requestId() {
        when(execution.hasVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(false);

        //invoke
        handler.onProcessInstanceEndEvent(execution);

        //verify
        verify(execution, times(1)).hasVariable(BpmnProcessConstants.REQUEST_ID);
        verify(execution, never()).getVariable(anyString());
        verify(execution, never()).getProcessInstanceId();
        verify(requestService, never()).terminateRequest(anyString(), anyString());
    }
}
