package uk.gov.pmrv.api.workflow.bpmn.handler.accountinstallationopening;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.flow.accountinstallationopening.handler.InstallationAccountPermitIssuanceService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InstallationAccountPermitIssuanceHandlerTest {

    @InjectMocks
    private InstallationAccountPermitIssuanceHandler handler;

    @Mock
    private InstallationAccountPermitIssuanceService installationAccountPermitIssuanceService;

    @Mock
    private DelegateExecution execution;

    @Test
    void executeTest() {
        // Mock data
        final String REQUEST_ID = "1";
        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(REQUEST_ID);

        // Invoke
        handler.execute(execution);

        // Verify
        verify(installationAccountPermitIssuanceService, times(1)).execute(REQUEST_ID);
    }

}
