package uk.gov.pmrv.api.workflow.bpmn.handler.permitnotification;

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
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.service.PermitNotificationFollowUpSendReminderNotificationService;

@ExtendWith(MockitoExtension.class)
class FollowUpResponseSecondReminderDateReachedHandlerTest {

    @InjectMocks
    private FollowUpResponseSecondReminderDateReachedHandler handler;

    @Mock
    private PermitNotificationFollowUpSendReminderNotificationService sendReminderNotificationService;

    @Test
    void execute() {
        
        final DelegateExecution delegateExecution = mock(DelegateExecution.class);
        final String requestId = "1";
        final Date expirationDate = new Date();
        
        when(delegateExecution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        when(delegateExecution.getVariable(BpmnProcessConstants.FOLLOW_UP_RESPONSE_EXPIRATION_DATE)).thenReturn(expirationDate);
        
        handler.execute(delegateExecution);
        
        verify(sendReminderNotificationService, times(1)).sendSecondReminderNotification(requestId, expirationDate);
    }
}
