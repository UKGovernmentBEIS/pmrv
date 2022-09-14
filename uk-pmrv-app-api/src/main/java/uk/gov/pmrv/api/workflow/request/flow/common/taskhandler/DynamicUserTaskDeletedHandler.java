package uk.gov.pmrv.api.workflow.request.flow.common.taskhandler;

import java.util.Map;

public interface DynamicUserTaskDeletedHandler {

    void process(String requestId, Map<String, Object> variables);

    DynamicUserTaskDefinitionKey getTaskDefinition();
}
