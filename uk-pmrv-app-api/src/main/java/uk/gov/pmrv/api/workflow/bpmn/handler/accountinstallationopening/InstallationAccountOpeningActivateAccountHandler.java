package uk.gov.pmrv.api.workflow.bpmn.handler.accountinstallationopening;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.workflow.request.flow.accountinstallationopening.handler.InstallationAccountOpeningActivateAccountService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Component
@RequiredArgsConstructor
public class InstallationAccountOpeningActivateAccountHandler implements JavaDelegate {
    private final InstallationAccountOpeningActivateAccountService installationAccountOpeningActivateAccountService;

    @Override
    public void execute(DelegateExecution execution) {
        installationAccountOpeningActivateAccountService.execute((String) execution.getVariable(BpmnProcessConstants.REQUEST_ID));
    }
}
