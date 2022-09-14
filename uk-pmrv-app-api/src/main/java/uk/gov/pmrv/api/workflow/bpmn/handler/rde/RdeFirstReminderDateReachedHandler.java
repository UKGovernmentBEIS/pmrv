package uk.gov.pmrv.api.workflow.bpmn.handler.rde;

import java.util.Date;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.rde.service.RdeSendReminderNotificationService;

@Service
@RequiredArgsConstructor
public class RdeFirstReminderDateReachedHandler implements JavaDelegate {
    
    private final RdeSendReminderNotificationService rdeSendReminderNotificationService;
    
    @Override
    public void execute(DelegateExecution delegateExecution) {
        rdeSendReminderNotificationService.sendFirstReminderNotification(
                (String) delegateExecution.getVariable(BpmnProcessConstants.REQUEST_ID),
                (Date) delegateExecution.getVariable(BpmnProcessConstants.RDE_EXPIRATION_DATE));
    }
}
