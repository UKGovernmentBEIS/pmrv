package uk.gov.pmrv.api.workflow.bpmn.handler.permitrevocation;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.permitrevocation.service.PermitRevokedService;

@Service
@RequiredArgsConstructor
public class PermitRevocationEffectiveDateReachedHandler implements JavaDelegate {

    private final PermitRevokedService service;

    @Override
    public void execute(final DelegateExecution execution) throws Exception {
        service.executePermitRevokedPostActions((String) execution.getVariable(BpmnProcessConstants.REQUEST_ID));
    }
}
