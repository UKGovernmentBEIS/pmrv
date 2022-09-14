package uk.gov.pmrv.api.workflow.bpmn.listener;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.workflow.bpmn.handler.dynamicusertask.DynamicUserTaskCreatedHandlerMapper;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskCreateService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

/**
 * Camunda listener that listens to creation of a user task 
 * 
 */
@Component
@RequiredArgsConstructor
public class UserTaskCreatedListener {

	private final DynamicUserTaskCreatedHandlerMapper dynamicUserTaskCreatedHandlerMapper;

	private final RequestTaskCreateService requestTaskCreateService;

	@Transactional
	@EventListener(condition = "#taskDelegate.eventName=='create'")
	public void onTaskCreatedEvent(DelegateTask taskDelegate) {

		final String requestId = (String) taskDelegate.getVariable(BpmnProcessConstants.REQUEST_ID);
		final String processTaskId = taskDelegate.getId();
		final String taskDefinitionKey = taskDelegate.getTaskDefinitionKey();
		
		dynamicUserTaskCreatedHandlerMapper.get(taskDefinitionKey).ifPresentOrElse(
			handler -> {
				final Map<String, Object> variables = taskDelegate.getVariables();
				handler.create(requestId, processTaskId, variables);
			},
			() -> {
				final RequestTaskType requestTaskType = RequestTaskType.valueOf(taskDefinitionKey);
				this.requestTaskCreateService.create(requestId, processTaskId, requestTaskType);
			}
		);
	}
}
