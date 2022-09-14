package uk.gov.pmrv.api.workflow.bpmn.handler.permitissuance;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.service.PermitIssuanceDeemedWithdrawnService;

@Service
@RequiredArgsConstructor
public class PermitIssuanceDeemedWithdrawnHandler implements JavaDelegate {
    
    private final PermitIssuanceDeemedWithdrawnService service;
    
    @Override
    public void execute(DelegateExecution execution) {

        service.deemWithdrawn((String) execution.getVariable(BpmnProcessConstants.REQUEST_ID));
    }
}
