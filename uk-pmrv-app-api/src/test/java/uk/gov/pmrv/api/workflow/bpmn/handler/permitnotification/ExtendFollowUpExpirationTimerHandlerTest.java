package uk.gov.pmrv.api.workflow.bpmn.handler.permitnotification;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.ReviewOutcome;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.service.ExtendFollowUpExpirationTimerService;

@ExtendWith(MockitoExtension.class)
class ExtendFollowUpExpirationTimerHandlerTest {

    @InjectMocks
    private ExtendFollowUpExpirationTimerHandler handler;

    @Mock
    private ExtendFollowUpExpirationTimerService service;

    @Test
    void execute_during_follow_up() {

        final DelegateExecution execution = mock(DelegateExecution.class);
        final String requestId = "1";

        final Date expirationDate = new GregorianCalendar(2023, Calendar.FEBRUARY, 1).getTime();

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        when(execution.getVariable(BpmnProcessConstants.FOLLOW_UP_RESPONSE_EXPIRATION_DATE)).thenReturn(expirationDate);

        // Invoke
        handler.execute(execution);

        // Verify
        verify(service, times(1))
            .extendTimer(requestId, expirationDate, BpmnProcessConstants.FOLLOW_UP_RESPONSE_EXPIRATION_DATE);
    }
    
    @Test
    void execute_during_amends_follow_up() {

        final DelegateExecution execution = mock(DelegateExecution.class);
        final String requestId = "1";

        final Date expirationDate = new GregorianCalendar(2023, Calendar.FEBRUARY, 1).getTime();

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        when(execution.getVariable(BpmnProcessConstants.FOLLOW_UP_RESPONSE_EXPIRATION_DATE)).thenReturn(expirationDate);
        when(execution.getVariable(BpmnProcessConstants.REVIEW_OUTCOME)).thenReturn(ReviewOutcome.AMENDS_NEEDED);

        // Invoke
        handler.execute(execution);

        // Verify
        verify(service, times(1))
            .extendTimer(requestId, expirationDate, BpmnProcessConstants.APPLICATION_REVIEW_EXPIRATION_DATE);
    }

}
