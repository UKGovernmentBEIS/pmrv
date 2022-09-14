package uk.gov.pmrv.api.workflow.bpmn.handler.permitsurrender;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.service.PermitSurrenderReviewRejectedService;

@Service
@RequiredArgsConstructor
public class PermitSurrenderRejectedHandler implements JavaDelegate {
    
    private final PermitSurrenderReviewRejectedService service;
    
    @Override
    public void execute(DelegateExecution execution) {
        service.executeRejectedPostActions((String) execution.getVariable(BpmnProcessConstants.REQUEST_ID));
    }
}
