package uk.gov.pmrv.api.workflow.bpmn.handler.rfi;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.rfi.service.RfiTerminatedService;

@ExtendWith(MockitoExtension.class)
class RfiTerminatedHandlerTest {

    @InjectMocks
    private RfiTerminatedHandler handler;
    @Mock
    private RfiTerminatedService service;
    
    @Test
    void execute() {
        final DelegateExecution execution = spy(DelegateExecution.class);
        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn("1");
        
        handler.execute(execution);
        
        verify(execution, times(1)).removeVariable(BpmnProcessConstants.RFI_OUTCOME);
        verify(execution, times(1)).removeVariable(BpmnProcessConstants.RFI_START_TIME);
        verify(execution, times(1)).removeVariable(BpmnProcessConstants.RFI_EXPIRATION_DATE);
        verify(execution, times(1)).removeVariable(BpmnProcessConstants.RFI_FIRST_REMINDER_DATE);
        verify(execution, times(1)).removeVariable(BpmnProcessConstants.RFI_SECOND_REMINDER_DATE);
        verify(service, times(1)).terminate("1");        
    }
}