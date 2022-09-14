package uk.gov.pmrv.api.workflow.bpmn.handler.accountinstallationopening;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.flow.accountinstallationopening.handler.InstallationAccountOpeningActivateAccountService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InstallationAccountOpeningActivateAccountHandlerTest {

    @InjectMocks
    private InstallationAccountOpeningActivateAccountHandler handler;

    @Mock
    private InstallationAccountOpeningActivateAccountService installationAccountOpeningActivateAccountService;


    @Mock
    private DelegateExecution execution;

    @Test
    void execute() throws Exception {
        //prepare data
        final String requestId = "1";

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);

        //invoke
        handler.execute(execution);

        //verify
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(installationAccountOpeningActivateAccountService, times(1)).execute(requestId);
    }
}
