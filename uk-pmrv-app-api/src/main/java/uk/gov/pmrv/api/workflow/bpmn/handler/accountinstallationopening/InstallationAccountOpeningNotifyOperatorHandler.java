package uk.gov.pmrv.api.workflow.bpmn.handler.accountinstallationopening;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.workflow.request.flow.accountinstallationopening.handler.InstallationAccountOpeningNotifyOperatorService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

/**
 * Handler for the Notify Operator service task of the installation account opening BPMN process.
 */
@Component
@RequiredArgsConstructor
public class InstallationAccountOpeningNotifyOperatorHandler implements JavaDelegate {
    private final InstallationAccountOpeningNotifyOperatorService installationAccountOpeningNotifyOperatorService;

    @Override
    public void execute(DelegateExecution execution) {
        installationAccountOpeningNotifyOperatorService.execute((String) execution.getVariable(BpmnProcessConstants.REQUEST_ID));
    }
}
