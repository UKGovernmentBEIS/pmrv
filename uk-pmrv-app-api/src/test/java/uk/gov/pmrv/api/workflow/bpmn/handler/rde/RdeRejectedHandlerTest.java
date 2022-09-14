package uk.gov.pmrv.api.workflow.bpmn.handler.rde;

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
import uk.gov.pmrv.api.workflow.request.flow.rde.service.RdeRespondedService;

@ExtendWith(MockitoExtension.class)
class RdeRejectedHandlerTest {

    @InjectMocks
    private RdeRejectedHandler handler;

    @Mock
    private RdeRespondedService service;

    @Test
    void execute() {

        final DelegateExecution delegateExecution = spy(DelegateExecution.class);

        when(delegateExecution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn("1");

        handler.execute(delegateExecution);

        verify(service, times(1)).respond("1");
    }
}
