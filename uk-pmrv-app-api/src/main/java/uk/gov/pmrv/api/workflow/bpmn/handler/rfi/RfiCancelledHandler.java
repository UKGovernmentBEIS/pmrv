package uk.gov.pmrv.api.workflow.bpmn.handler.rfi;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.rfi.service.RfiCancelledService;

@Service
@RequiredArgsConstructor
public class RfiCancelledHandler implements JavaDelegate {

    private final RfiCancelledService service;

    @Override
    public void execute(DelegateExecution execution) {
        service.cancel((String) execution.getVariable(BpmnProcessConstants.REQUEST_ID));
    }
}
