package uk.gov.pmrv.api.workflow.bpmn.handler.applicationreview;

import java.util.Date;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.service.ApplicationReviewSendReminderNotificationService;

@Service
@RequiredArgsConstructor
public class ApplicationReviewSecondReminderDateReachedHandler implements JavaDelegate {

    private final ApplicationReviewSendReminderNotificationService sendReminderNotificationService;
    
    @Override
    public void execute(DelegateExecution delegateExecution) {
        sendReminderNotificationService.sendSecondReminderNotification(
                (String) delegateExecution.getVariable(BpmnProcessConstants.REQUEST_ID),
                (Date) delegateExecution.getVariable(BpmnProcessConstants.APPLICATION_REVIEW_EXPIRATION_DATE));
    }
}
