package uk.gov.pmrv.api.workflow.bpmn.handler.permitsurrender;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.service.PermitSurrenderCancelledService;

@Service
@RequiredArgsConstructor
public class PermitSurrenderCancelledHandler implements JavaDelegate {

    private final PermitSurrenderCancelledService service;
    
    @Override
    public void execute(DelegateExecution execution) {
        
        service.cancel((String) execution.getVariable(BpmnProcessConstants.REQUEST_ID));
    }
}
