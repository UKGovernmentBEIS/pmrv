package uk.gov.pmrv.api.workflow.request.flow.common.taskhandler;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskCreateService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Component
@RequiredArgsConstructor
public abstract class WorkflowDynamicNonExpirableUserTaskCreatedHandler implements DynamicUserTaskCreatedHandler {

    private final RequestTaskCreateService requestTaskCreateService;

    @Override
    public void create(final String requestId, final String processTaskId, final Map<String, Object> variables) {
        final Object requestType = variables.get(BpmnProcessConstants.REQUEST_TYPE);
        final String taskDefinitionKey = this.getTaskDefinition().getId();
        final String taskDefinition = requestType != null ? requestType + "_" + taskDefinitionKey : taskDefinitionKey;
        final RequestTaskType requestTaskType = RequestTaskType.valueOf(taskDefinition);

        requestTaskCreateService.create(requestId, processTaskId, requestTaskType);
    }
}
