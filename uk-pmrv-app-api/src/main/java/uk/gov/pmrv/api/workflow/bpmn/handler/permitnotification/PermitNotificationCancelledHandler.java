package uk.gov.pmrv.api.workflow.bpmn.handler.permitnotification;

import lombok.RequiredArgsConstructor;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import org.springframework.stereotype.Service;

import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.service.PermitNotificationCancelledService;

@Service
@RequiredArgsConstructor
public class PermitNotificationCancelledHandler implements JavaDelegate {

    private final PermitNotificationCancelledService service;

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        service.cancel((String) delegateExecution.getVariable(BpmnProcessConstants.REQUEST_ID));
    }
}
