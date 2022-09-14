package uk.gov.pmrv.api.workflow.bpmn.handler.permitrevocation;

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
import uk.gov.pmrv.api.workflow.request.flow.permitrevocation.service.PermitRevocationEffectiveDateReminderService;

@ExtendWith(MockitoExtension.class)
class PermitRevocationEffectiveDateReminderReachedHandlerTest {

	@InjectMocks
    private PermitRevocationEffectiveDateReminderReachedHandler handler;
    
    @Mock
    private PermitRevocationEffectiveDateReminderService permitRevocationEffectiveDateReminderService;

    @Mock
    private DelegateExecution execution;

    @Test
    void execute() throws Exception {
    	final String requestId = "1";
        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);

        handler.execute(execution);
        
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(permitRevocationEffectiveDateReminderService, times(1)).sendEffectiveDateReminder(requestId);
    }
}
