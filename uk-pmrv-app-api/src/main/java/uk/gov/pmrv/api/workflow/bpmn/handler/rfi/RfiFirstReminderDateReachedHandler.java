package uk.gov.pmrv.api.workflow.bpmn.handler.rfi;

import java.util.Date;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.rfi.service.RfiSendReminderNotificationService;

@Service
@RequiredArgsConstructor
public class RfiFirstReminderDateReachedHandler implements JavaDelegate {
    
    private final RfiSendReminderNotificationService rfiSendReminderNotificationService;
    
    @Override
    public void execute(DelegateExecution delegateExecution) {
        rfiSendReminderNotificationService.sendFirstReminderNotification(
                (String) delegateExecution.getVariable(BpmnProcessConstants.REQUEST_ID),
                (Date) delegateExecution.getVariable(BpmnProcessConstants.RFI_EXPIRATION_DATE));
    }
}