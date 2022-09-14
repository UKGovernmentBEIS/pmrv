package uk.gov.pmrv.api.workflow.bpmn.handler.accountinstallationopening;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.workflow.request.flow.accountinstallationopening.handler.InstallationAccountOpeningRejectAccountService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Component
@RequiredArgsConstructor
public class InstallationAccountOpeningRejectAccountHandler implements JavaDelegate {
    private final InstallationAccountOpeningRejectAccountService installationAccountOpeningRejectAccountService;

    @Override
    public void execute(DelegateExecution execution) {
        installationAccountOpeningRejectAccountService.execute((String) execution.getVariable(BpmnProcessConstants.REQUEST_ID));
    }
}
