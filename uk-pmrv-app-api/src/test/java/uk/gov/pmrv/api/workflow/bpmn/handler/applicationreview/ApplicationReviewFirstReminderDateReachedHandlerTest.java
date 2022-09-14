package uk.gov.pmrv.api.workflow.bpmn.handler.applicationreview;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.service.ApplicationReviewSendReminderNotificationService;

@ExtendWith(MockitoExtension.class)
class ApplicationReviewFirstReminderDateReachedHandlerTest {

    @InjectMocks
    private ApplicationReviewFirstReminderDateReachedHandler handler;

    @Mock
    private ApplicationReviewSendReminderNotificationService sendReminderNotificationService;

    @Test
    void execute() {
        final DelegateExecution delegateExecution = mock(DelegateExecution.class);
        String requestId = "1";
        Date expirationDate = new Date();
        
        when(delegateExecution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        when(delegateExecution.getVariable(BpmnProcessConstants.APPLICATION_REVIEW_EXPIRATION_DATE)).thenReturn(expirationDate);
        
        handler.execute(delegateExecution);
        
        verify(sendReminderNotificationService, times(1)).sendFirstReminderNotification(requestId, expirationDate);
    }
}
