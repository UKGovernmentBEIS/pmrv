package uk.gov.pmrv.api.workflow.bpmn.handler.permitnotification;

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
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.service.PermitNotificationReviewSubmittedService;

@ExtendWith(MockitoExtension.class)
class PermitNotificationCompletedHandlerTest {

    @InjectMocks
    private PermitNotificationCompletedHandler handler;

    @Mock
    private PermitNotificationReviewSubmittedService reviewSubmittedService;

    @Test
    void execute() {

        final DelegateExecution execution = mock(DelegateExecution.class);
        final String requestId = "1";

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);

        // invoke
        handler.execute(execution);

        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(reviewSubmittedService, times(1)).executeCompletedPostActions(requestId);
    }
}
