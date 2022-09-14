package uk.gov.pmrv.api.workflow.bpmn.listener;

import org.camunda.bpm.engine.delegate.DelegateTask;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.workflow.request.application.taskcompleted.RequestTaskCompleteService;

/**
 * Camunda listener that listens to completion of a user task  
 *
 */
@Component
@RequiredArgsConstructor
public class UserTaskCompletedListener {

	private final RequestTaskCompleteService requestTaskCompleteService;
    
	@EventListener(condition = "#taskDelegate.eventName=='complete'")
	public void onTaskCompletedEvent(DelegateTask taskDelegate) {
	    requestTaskCompleteService.complete(taskDelegate.getId());
	}

	
}
