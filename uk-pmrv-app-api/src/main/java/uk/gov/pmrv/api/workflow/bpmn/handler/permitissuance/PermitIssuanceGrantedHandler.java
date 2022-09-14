package uk.gov.pmrv.api.workflow.bpmn.handler.permitissuance;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.service.PermitIssuanceGrantedService;

@Service
@RequiredArgsConstructor
public class PermitIssuanceGrantedHandler implements JavaDelegate {

    private final PermitIssuanceGrantedService service;

    @Override
    public void execute(DelegateExecution execution) {
        
        service.grant((String) execution.getVariable(BpmnProcessConstants.REQUEST_ID));
    }
}
