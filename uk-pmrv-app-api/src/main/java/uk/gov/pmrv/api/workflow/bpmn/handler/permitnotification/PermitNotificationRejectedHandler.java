package uk.gov.pmrv.api.workflow.bpmn.handler.permitnotification;

import lombok.RequiredArgsConstructor;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import org.springframework.stereotype.Service;

import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.service.PermitNotificationReviewSubmittedService;

@Service
@RequiredArgsConstructor
public class PermitNotificationRejectedHandler implements JavaDelegate {

    private final PermitNotificationReviewSubmittedService service;

    @Override
    public void execute(DelegateExecution execution) {
        service.executeRejectedPostActions((String) execution.getVariable(BpmnProcessConstants.REQUEST_ID));
    }
}
