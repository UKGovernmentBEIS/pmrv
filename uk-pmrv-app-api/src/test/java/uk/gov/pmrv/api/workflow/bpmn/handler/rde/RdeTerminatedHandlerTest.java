package uk.gov.pmrv.api.workflow.bpmn.handler.rde;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.rde.service.RdeTerminatedService;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RdeTerminatedHandlerTest {

    @InjectMocks
    private RdeTerminatedHandler handler;
    
    @Mock
    private RdeTerminatedService service;
    
    @Test
    void execute() {

        final DelegateExecution delegateExecution = spy(DelegateExecution.class);
        when(delegateExecution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn("1");
        
        handler.execute(delegateExecution);
        
        verify(delegateExecution, times(1)).removeVariable(BpmnProcessConstants.RDE_EXPIRATION_DATE);
        verify(delegateExecution, times(1)).removeVariable(BpmnProcessConstants.RDE_FIRST_REMINDER_DATE);
        verify(delegateExecution, times(1)).removeVariable(BpmnProcessConstants.RDE_SECOND_REMINDER_DATE);
        verify(delegateExecution, times(1)).removeVariable(BpmnProcessConstants.RDE_OUTCOME);
        verify(service, times(1)).terminate("1");
    }
}