package uk.gov.pmrv.api.workflow.bpmn.listener;

import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.workflow.bpmn.handler.dynamicusertask.DynamicUserTaskDeletedHandlerMapper;
import uk.gov.pmrv.api.workflow.request.application.taskdeleted.RequestTaskDeleteService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.taskhandler.DynamicUserTaskDeletedHandler;

@RequiredArgsConstructor
@Component
public class UserTaskDeletedListener {

    private final DynamicUserTaskDeletedHandlerMapper dynamicUserTaskDeletedHandlerMapper;

    private final RequestTaskDeleteService requestTaskDeleteService;

    @EventListener(condition = "#taskDelegate.eventName=='delete'")
    public void onTaskDeletedEvent(DelegateTask taskDelegate) {

        final String taskDefinitionKey = taskDelegate.getTaskDefinitionKey();
        final String processTaskId = taskDelegate.getId();
        final Optional<DynamicUserTaskDeletedHandler> handler = dynamicUserTaskDeletedHandlerMapper.get(taskDefinitionKey);
        if (handler.isPresent()) {
            final String requestId = (String) taskDelegate.getVariable(BpmnProcessConstants.REQUEST_ID);
            final Map<String, Object> variables = taskDelegate.getVariables();
            handler.get().process(requestId, variables);
        }
        requestTaskDeleteService.delete(processTaskId);
    }

}
