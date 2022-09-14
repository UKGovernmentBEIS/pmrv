package uk.gov.pmrv.api.workflow.bpmn.handler.accountinstallationopening;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.workflow.request.flow.accountinstallationopening.handler.InstallationAccountPermitIssuanceService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

/**
 * Handler for starting PERMIT_ISSUANCE workflow process.
 */
@Component
@RequiredArgsConstructor
public class InstallationAccountPermitIssuanceHandler implements JavaDelegate {
    private final InstallationAccountPermitIssuanceService installationAccountPermitIssuanceService;

    @Override
    public void execute(DelegateExecution delegateExecution) {
        installationAccountPermitIssuanceService.execute((String) delegateExecution.getVariable(BpmnProcessConstants.REQUEST_ID));
    }
}
