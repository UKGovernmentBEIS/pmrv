package uk.gov.pmrv.api.workflow.bpmn.listener;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.request.flow.notificationsystemmessage.handler.SystemMessageNotificationCreatedHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.taskhandler.ApplicationReviewCreatedHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.taskhandler.DynamicUserTaskDefinitionKey;
import uk.gov.pmrv.api.workflow.bpmn.handler.dynamicusertask.DynamicUserTaskCreatedHandlerMapper;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskCreateService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@ExtendWith(MockitoExtension.class)
class UserTaskCreatedListenerTest {

	@InjectMocks
	private UserTaskCreatedListener userTaskCreatedListener;
	
	@Mock
	private RequestTaskCreateService requestTaskCreateService;
	
	@Mock
	private DynamicUserTaskCreatedHandlerMapper dynamicUserTaskCreatedHandlerMapper;

	@Mock
	private SystemMessageNotificationCreatedHandler systemMessageNotificationCreatedHandler;

	@Mock
	private ApplicationReviewCreatedHandler applicationReviewCreatedHandler;
	
	@Mock
	private DelegateTask taskDelegate;


	@Test
	void onTaskCreatedEvent_whenReviewEvent_thenReviewHandler() {
		final String requestId = "1";
		final String processTaskId ="taskid";
		when(taskDelegate.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
		when(taskDelegate.getId()).thenReturn(processTaskId);
		when(taskDelegate.getTaskDefinitionKey()).thenReturn(DynamicUserTaskDefinitionKey.APPLICATION_REVIEW.getId());
		when(dynamicUserTaskCreatedHandlerMapper.get(DynamicUserTaskDefinitionKey.APPLICATION_REVIEW.getId()))
			.thenReturn(Optional.of(applicationReviewCreatedHandler));

		// Invoke
		userTaskCreatedListener.onTaskCreatedEvent(taskDelegate);

		// Verify
		verify(taskDelegate, times(1)).getTaskDefinitionKey();
		verify(applicationReviewCreatedHandler, times(1)).create(requestId, processTaskId, taskDelegate.getVariables());
	}

	@Test
	void onTaskCreatedEvent_whenSystemMessageEvent_thenSystemMessageHandler() {
		final String requestId = "1";
		final String processTaskId ="taskid";
		when(taskDelegate.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
		when(taskDelegate.getId()).thenReturn(processTaskId);
		when(taskDelegate.getTaskDefinitionKey()).thenReturn(DynamicUserTaskDefinitionKey.SYSTEM_MESSAGE_NOTIFICATION_TASK.getId());
		when(dynamicUserTaskCreatedHandlerMapper.get(DynamicUserTaskDefinitionKey.SYSTEM_MESSAGE_NOTIFICATION_TASK.getId()))
			.thenReturn(Optional.of(systemMessageNotificationCreatedHandler));

		// Invoke
		userTaskCreatedListener.onTaskCreatedEvent(taskDelegate);

		// Verify
		verify(taskDelegate, times(1)).getTaskDefinitionKey();
		verify(systemMessageNotificationCreatedHandler, times(1)).create(requestId, processTaskId, taskDelegate.getVariables());
	}

	@Test
	void onTaskCreatedEvent_whenPermitApplicationSubmitEvent_thenDefaultTaskCreate() {
		final String requestId = "1";
		final String processTaskId ="taskid";
		final RequestTaskType taskDefinitionKey = RequestTaskType.PERMIT_ISSUANCE_APPLICATION_SUBMIT;
		when(taskDelegate.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
		when(taskDelegate.getId()).thenReturn(processTaskId);
		when(taskDelegate.getTaskDefinitionKey()).thenReturn(taskDefinitionKey.name());
		when(dynamicUserTaskCreatedHandlerMapper.get(taskDefinitionKey.name())).thenReturn(Optional.empty());

		// Invoke
		userTaskCreatedListener.onTaskCreatedEvent(taskDelegate);

		// Verify
		verify(taskDelegate, times(1)).getTaskDefinitionKey();
		verify(requestTaskCreateService, times(1)).create(requestId, processTaskId, taskDefinitionKey);
	}
}
