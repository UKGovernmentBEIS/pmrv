package uk.gov.pmrv.api.workflow.bpmn.handler.permitrevocation;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.permitrevocation.service.PermitRevocationWithdrawnService;

@Service
@RequiredArgsConstructor
public class PermitRevocationWithdrawnHandler implements JavaDelegate {

    private final PermitRevocationWithdrawnService service;

    @Override
    public void execute(final DelegateExecution execution) throws Exception {
        service.withdraw((String) execution.getVariable(BpmnProcessConstants.REQUEST_ID));
    }
}
