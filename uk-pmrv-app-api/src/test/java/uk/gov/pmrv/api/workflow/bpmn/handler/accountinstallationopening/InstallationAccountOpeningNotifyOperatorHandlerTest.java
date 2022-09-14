package uk.gov.pmrv.api.workflow.bpmn.handler.accountinstallationopening;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.flow.accountinstallationopening.handler.InstallationAccountOpeningNotifyOperatorService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@ExtendWith(MockitoExtension.class)
class InstallationAccountOpeningNotifyOperatorHandlerTest {

	@InjectMocks
	private InstallationAccountOpeningNotifyOperatorHandler handler;
	
	@Mock
	private InstallationAccountOpeningNotifyOperatorService installationAccountOpeningNotifyOperatorService;
	
	@Mock
	private DelegateExecution execution;
	
	@Test
	void execute_application_accepted() {
		//prepare data
		final String requestId = "1";

		when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);

		//invoke
		handler.execute(execution);
		
		//verify
		verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
		verify(installationAccountOpeningNotifyOperatorService, times(1)).execute(requestId);
	}
	
	@Test
	void execute_application_rejected() {
		//prepare data
		final String requestId = "1";

		when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);

		//invoke
		handler.execute(execution);
		
		//verify
		verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
		verify(installationAccountOpeningNotifyOperatorService, times(1)).execute(requestId);
	}
	
}
