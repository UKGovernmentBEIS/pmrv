package uk.gov.pmrv.api.workflow.bpmn.handler.rfi;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.rfi.domain.RfiOutcome;
import uk.gov.pmrv.api.workflow.request.flow.rfi.service.RfiExpiredService;

@Service
@RequiredArgsConstructor
public class RfiExpiredHandler implements JavaDelegate {

    private final RfiExpiredService service;
    
    @Override
    public void execute(DelegateExecution execution) {
        execution.setVariable(BpmnProcessConstants.RFI_OUTCOME, RfiOutcome.EXPIRED);
        service.expire((String) execution.getVariable(BpmnProcessConstants.REQUEST_ID));
    }
}
