package uk.gov.pmrv.api.workflow.bpmn.handler.permitissuance;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.service.PermitIssuanceRejectedService;

@Service
@RequiredArgsConstructor
public class PermitIssuanceRejectedHandler implements JavaDelegate {
    
    private final PermitIssuanceRejectedService service;
    
    @Override
    public void execute(DelegateExecution execution) {

        service.reject((String) execution.getVariable(BpmnProcessConstants.REQUEST_ID));
    }
}
