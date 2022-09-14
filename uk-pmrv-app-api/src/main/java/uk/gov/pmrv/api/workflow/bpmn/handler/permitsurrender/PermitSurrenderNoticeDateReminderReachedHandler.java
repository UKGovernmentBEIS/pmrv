package uk.gov.pmrv.api.workflow.bpmn.handler.permitsurrender;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.service.PermitSurrenderNoticeDateReminderService;

@Service
@RequiredArgsConstructor
public class PermitSurrenderNoticeDateReminderReachedHandler implements JavaDelegate {
	
	private final PermitSurrenderNoticeDateReminderService permitSurrenderNoticeDateReminderService;

	@Override
	public void execute(DelegateExecution execution) {
		final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
		permitSurrenderNoticeDateReminderService.sendNoticeDateReminder(requestId);
	}

}
