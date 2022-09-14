package uk.gov.pmrv.api.workflow.bpmn.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Map;

import org.camunda.bpm.engine.TaskService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.bpmn.CamundaWorkflowService;

@ExtendWith(MockitoExtension.class)
class CamundaWorkflowServiceTest {
	
	@InjectMocks
    private CamundaWorkflowService workflowService;
	
	@Mock
    private TaskService taskService;
	
	@Test
	void completeTask() {
		final String processTaskId = "1";
		final Map<String, Object> vars = Map.of("test1", "val1");
		
		//invoke
	    workflowService.completeTask(processTaskId, vars);
	    
	    //verify
	    verify(taskService, times(1)).complete(processTaskId, vars);
	}

}
