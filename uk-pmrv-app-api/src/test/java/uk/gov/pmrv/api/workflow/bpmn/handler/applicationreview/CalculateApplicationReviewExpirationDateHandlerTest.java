package uk.gov.pmrv.api.workflow.bpmn.handler.applicationreview;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.SubRequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestExpirationVarsBuilder;

import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CalculateApplicationReviewExpirationDateHandlerTest {

    @InjectMocks
    private CalculateApplicationReviewExpirationDateHandler handler;

    @Mock
    private RequestExpirationVarsBuilder requestExpirationVarsBuilder;
    
    @Test
    void execute() {
        final DelegateExecution execution = mock(DelegateExecution.class);
        final RequestType requestType = RequestType.PERMIT_NOTIFICATION;
        
        final Map<String, Object> vars = Map.of(
                "var1", "val1"
                );
        
        when(execution.getVariable(BpmnProcessConstants.REQUEST_TYPE)).thenReturn(requestType.name());
        when(requestExpirationVarsBuilder.buildExpirationVars(requestType, SubRequestType.APPLICATION_REVIEW))
            .thenReturn(vars);
        
        handler.execute(execution);
        
        verify(requestExpirationVarsBuilder, times(1)).buildExpirationVars(requestType, SubRequestType.APPLICATION_REVIEW);
        verify(execution, times(1)).setVariables(vars);
    }

}
