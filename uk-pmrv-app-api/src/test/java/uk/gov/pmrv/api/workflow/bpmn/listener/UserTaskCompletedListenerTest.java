package uk.gov.pmrv.api.workflow.bpmn.listener;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.camunda.bpm.engine.delegate.DelegateTask;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.request.application.taskcompleted.RequestTaskCompleteService;

@ExtendWith(MockitoExtension.class)
class UserTaskCompletedListenerTest {

	@InjectMocks
	private UserTaskCompletedListener userTaskCompletedListener;
	
	@Mock
	private RequestTaskCompleteService requestTaskCompleteService;
	
	@Mock
	private DelegateTask taskDelegate;
	
	@Test
	void onTaskCompletedEvent() {
		final String processTaskId ="taskid";
		when(taskDelegate.getId()).thenReturn(processTaskId);
		
		//invoke
		userTaskCompletedListener.onTaskCompletedEvent(taskDelegate);
		
		//verify
		verify(taskDelegate, times(1)).getId();
		verify(requestTaskCompleteService, times(1)).complete(processTaskId);
	}
}
