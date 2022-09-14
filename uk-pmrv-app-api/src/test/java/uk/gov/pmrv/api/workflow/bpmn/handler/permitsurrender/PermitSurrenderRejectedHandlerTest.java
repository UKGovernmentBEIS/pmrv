package uk.gov.pmrv.api.workflow.bpmn.handler.permitsurrender;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.service.PermitSurrenderReviewRejectedService;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PermitSurrenderRejectedHandlerTest {

    @InjectMocks
    private PermitSurrenderRejectedHandler handler;

    @Mock
    private PermitSurrenderReviewRejectedService service;

    @Mock
    private DelegateExecution execution;

    @Test
    void execute() {
        final String requestId = "1";
        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);

        handler.execute(execution);
        
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(service, times(1)).executeRejectedPostActions(requestId);
    }
}
