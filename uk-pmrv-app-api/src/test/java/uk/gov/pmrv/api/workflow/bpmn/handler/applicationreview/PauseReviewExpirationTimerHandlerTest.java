package uk.gov.pmrv.api.workflow.bpmn.handler.applicationreview;

import static org.mockito.Mockito.mock;
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
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestTaskTimeManagementService;

@ExtendWith(MockitoExtension.class)
class PauseReviewExpirationTimerHandlerTest {

    @InjectMocks
    private PauseReviewExpirationTimerHandler handler;

    @Mock
    private RequestTaskTimeManagementService requestTaskTimeManagementService;

    @Test
    void execute() {
        final DelegateExecution delegateExecution = mock(DelegateExecution.class);
        String requestId = "1";
        
        when(delegateExecution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        
        handler.execute(delegateExecution);
        
        verify(requestTaskTimeManagementService, times(1)).pauseTasks(requestId,
                BpmnProcessConstants.APPLICATION_REVIEW_EXPIRATION_DATE);
    }

}
