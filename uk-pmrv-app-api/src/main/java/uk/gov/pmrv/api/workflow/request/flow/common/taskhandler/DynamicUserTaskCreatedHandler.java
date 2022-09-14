package uk.gov.pmrv.api.workflow.request.flow.common.taskhandler;

import java.util.Map;

public interface DynamicUserTaskCreatedHandler {

    void create(String requestId, String processTaskId, Map<String, Object> variables);

    DynamicUserTaskDefinitionKey getTaskDefinition();
}
