package uk.gov.pmrv.api.workflow.bpmn.handler.permitnotification;

import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.ReviewOutcome;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.service.ExtendFollowUpExpirationTimerService;

@Service
@RequiredArgsConstructor
public class ExtendFollowUpExpirationTimerHandler implements JavaDelegate {

    private final ExtendFollowUpExpirationTimerService service;

    @Override
    public void execute(DelegateExecution execution) {

        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        final Date expirationDate = (Date) execution.getVariable(BpmnProcessConstants.FOLLOW_UP_RESPONSE_EXPIRATION_DATE);
        final Object reviewOutcome = execution.getVariable(BpmnProcessConstants.REVIEW_OUTCOME);
        // In case this is called during a PERMIT_NOTIFICATION_WAIT_FOR_FOLLOW_UP task the correct key is FOLLOW_UP_RESPONSE_EXPIRATION_DATE.
        // In case this is called during a PERMIT_NOTIFICATION_FOLLOW_UP_WAIT_FOR_AMENDS task the correct key is APPLICATION_REVIEW_EXPIRATION_DATE,
        // since amend tasks are dynamic and use APPLICATION_REVIEW_EXPIRATION_DATE
        final String expirationDateKey = ReviewOutcome.AMENDS_NEEDED.equals(reviewOutcome) ?
            BpmnProcessConstants.APPLICATION_REVIEW_EXPIRATION_DATE : 
            BpmnProcessConstants.FOLLOW_UP_RESPONSE_EXPIRATION_DATE;
        service.extendTimer(requestId, expirationDate, expirationDateKey);
    }
}
