package uk.gov.pmrv.api.workflow.request.flow.common.taskhandler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class WorkflowDynamicExpirableUserTaskCreatedHandlerMapper {

    private final List<WorkflowDynamicExpirableUserTaskCreatedHandler> handlers;
    
    public boolean handlerExists(final String requestTaskType,
                                 final String expirationDateKey) {
        
        return this.get(requestTaskType, expirationDateKey).isPresent(); 
    }

    private Optional<WorkflowDynamicExpirableUserTaskCreatedHandler> get(final String requestTaskType,
                                                                         final String expirationDateKey) {
        // get dynamicUserTaskDefinitionKey from requestTaskType
        final DynamicUserTaskDefinitionKey taskDefinition =
            Arrays.stream(DynamicUserTaskDefinitionKey.values())
                .filter(key -> requestTaskType.endsWith(key.getId()))
                .findFirst()
                .orElse(null);

        // check if dynamicUserTaskDefinitionKey and expirationDateKey are applicable to the task
        return handlers.stream()
            .filter(h -> h.getTaskDefinition().equals(taskDefinition) &&
                         h.getExpirationDateKey().equals(expirationDateKey))
            .findFirst();
    }
}

