package uk.gov.pmrv.api.workflow.bpmn.handler.permitissuance;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.service.PermitIssuanceGrantedService;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PermitIssuanceGrantedHandlerTest {

    @InjectMocks
    private PermitIssuanceGrantedHandler handler;

    @Mock
    private PermitIssuanceGrantedService permitIssuanceGrantedService;

    @Mock
    private DelegateExecution execution;

    @Test
    void execute() {
        //prepare data
        final String requestId = "1";

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);

        //invoke
        handler.execute(execution);

        //verify
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(permitIssuanceGrantedService, times(1)).grant(requestId);
    }
}
