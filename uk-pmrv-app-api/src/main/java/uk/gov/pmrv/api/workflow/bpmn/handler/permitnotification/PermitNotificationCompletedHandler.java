package uk.gov.pmrv.api.workflow.bpmn.handler.permitnotification;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.service.PermitNotificationReviewSubmittedService;

@Service
@RequiredArgsConstructor
public class PermitNotificationCompletedHandler implements JavaDelegate {

    private final PermitNotificationReviewSubmittedService reviewSubmittedService;

    @Override
    public void execute(DelegateExecution execution) {

        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        reviewSubmittedService.executeCompletedPostActions(requestId);
    }
}
