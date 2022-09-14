package uk.gov.pmrv.api.workflow.bpmn.listener;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.Optional;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.bpmn.handler.dynamicusertask.DynamicUserTaskDeletedHandlerMapper;
import uk.gov.pmrv.api.workflow.request.application.taskdeleted.RequestTaskDeleteService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.taskhandler.DynamicUserTaskDefinitionKey;
import uk.gov.pmrv.api.workflow.request.flow.rfi.handler.RfiWaitForResponseDeletedHandler;

@ExtendWith(MockitoExtension.class)
class UserTaskDeletedListenerTest {

    @InjectMocks
    private UserTaskDeletedListener userTaskDeletedListener;
    
    @Mock
    private RequestTaskDeleteService requestTaskDeleteService;
    
    @Mock
    private DynamicUserTaskDeletedHandlerMapper dynamicUserTaskDeletedHandlerMapper;
    
    @Mock
    private RfiWaitForResponseDeletedHandler rfiWaitForResponseDeletedHandler;
    
    @Mock
    private DelegateTask taskDelegate;
    
    @Test
    void onTaskDeletedEvent_whenNoHandlerExists_thenDefaultDelete() {
        final String processTaskId ="taskid";
        when(taskDelegate.getId()).thenReturn(processTaskId);
        
        //invoke
        userTaskDeletedListener.onTaskDeletedEvent(taskDelegate);
        
        //verify
        verify(taskDelegate, times(1)).getId();
        verify(requestTaskDeleteService, times(1)).delete(processTaskId);
    }

    @Test
    void onTaskDeletedEvent_whenHandlerExists_thenHandlerDelete() {

        final String requestId = "1";
        final String processTaskId ="taskid";
        when(taskDelegate.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        when(taskDelegate.getId()).thenReturn(processTaskId);
        when(taskDelegate.getVariables()).thenReturn(Map.of());
        when(taskDelegate.getTaskDefinitionKey()).thenReturn(DynamicUserTaskDefinitionKey.WAIT_FOR_RFI_RESPONSE.getId());
        when(dynamicUserTaskDeletedHandlerMapper.get(DynamicUserTaskDefinitionKey.WAIT_FOR_RFI_RESPONSE.getId()))
            .thenReturn(Optional.of(rfiWaitForResponseDeletedHandler));

        // Invoke
        userTaskDeletedListener.onTaskDeletedEvent(taskDelegate);

        // Verify
        verify(taskDelegate, times(1)).getTaskDefinitionKey();
        verify(rfiWaitForResponseDeletedHandler, times(1)).process(requestId, Map.of());
        verify(requestTaskDeleteService, times(1)).delete(processTaskId);
    }
}
