package uk.gov.pmrv.api.workflow.bpmn.handler.permitrevocation;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.permitrevocation.service.PermitRevocationSubmittedService;

@Service
@RequiredArgsConstructor
public class PermitRevocationSubmittedHandler implements JavaDelegate {

    private final PermitRevocationSubmittedService service;

    @Override
    public void execute(final DelegateExecution execution) {
        service.submit((String) execution.getVariable(BpmnProcessConstants.REQUEST_ID));
    }
}
