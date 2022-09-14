package uk.gov.pmrv.api.workflow.bpmn.handler.accountinstallationopening;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.workflow.request.flow.accountinstallationopening.handler.InstallationAccountMessageAccountUsersSetupService;

import static uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants.REQUEST_ID;

@Component
@RequiredArgsConstructor
public class InstallationAccountMessageAccountUsersSetupHandler implements JavaDelegate {
    private final InstallationAccountMessageAccountUsersSetupService installationAccountMessageAccountUsersSetupService;

    @Override
    public void execute(DelegateExecution delegateExecution) {
        installationAccountMessageAccountUsersSetupService.execute((String) delegateExecution.getVariable(REQUEST_ID));
    }
}
