package uk.gov.pmrv.api.workflow.bpmn.handler.permitsurrender;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.service.PermitSurrenderReviewDeemedWithdrawnService;

@Service
@RequiredArgsConstructor
public class PermitSurrenderDeemedWithdrawnHandler implements JavaDelegate {
    
    private final PermitSurrenderReviewDeemedWithdrawnService service;
    
    @Override
    public void execute(DelegateExecution execution) {
        service.executeDeemedWithdrawnPostActions((String) execution.getVariable(BpmnProcessConstants.REQUEST_ID));
    }
}
