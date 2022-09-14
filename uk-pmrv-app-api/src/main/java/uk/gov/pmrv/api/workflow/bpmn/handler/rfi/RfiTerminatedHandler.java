package uk.gov.pmrv.api.workflow.bpmn.handler.rfi;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.rfi.service.RfiTerminatedService;

@Service
@RequiredArgsConstructor
public class RfiTerminatedHandler implements JavaDelegate {

    private final RfiTerminatedService service;

    @Override
    public void execute(DelegateExecution execution) {

        execution.removeVariable(BpmnProcessConstants.RFI_OUTCOME);
        execution.removeVariable(BpmnProcessConstants.RFI_START_TIME);
        execution.removeVariable(BpmnProcessConstants.RFI_EXPIRATION_DATE);
        execution.removeVariable(BpmnProcessConstants.RFI_FIRST_REMINDER_DATE);
        execution.removeVariable(BpmnProcessConstants.RFI_SECOND_REMINDER_DATE);

        service.terminate((String) execution.getVariable(BpmnProcessConstants.REQUEST_ID));
    }
}
