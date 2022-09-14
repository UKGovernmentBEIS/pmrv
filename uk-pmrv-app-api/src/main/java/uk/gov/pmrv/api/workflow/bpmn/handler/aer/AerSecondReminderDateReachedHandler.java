package uk.gov.pmrv.api.workflow.bpmn.handler.aer;

import lombok.RequiredArgsConstructor;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import org.springframework.stereotype.Service;

import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class AerSecondReminderDateReachedHandler implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        final Date expirationDate = (Date) execution.getVariable(BpmnProcessConstants.AER_EXPIRATION_DATE);
        // TODO
    }
}
