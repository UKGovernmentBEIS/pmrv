package uk.gov.pmrv.api.workflow.bpmn.handler.dynamicusertask;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.workflow.request.flow.common.taskhandler.DynamicUserTaskDefinitionKey;
import uk.gov.pmrv.api.workflow.request.flow.common.taskhandler.DynamicUserTaskDeletedHandler;

@Component
@RequiredArgsConstructor
public class DynamicUserTaskDeletedHandlerMapper {

    private final List<DynamicUserTaskDeletedHandler> handlers;

    public Optional<DynamicUserTaskDeletedHandler> get(final String taskDefinitionId) {

        final Optional<DynamicUserTaskDefinitionKey> taskDefinition = Arrays.stream(DynamicUserTaskDefinitionKey.values())
            .filter(v -> v.getId().equals(taskDefinitionId))
            .findFirst();

        return taskDefinition.flatMap(td ->
            handlers.stream()
                .filter(handler -> td.equals(handler.getTaskDefinition()))
                .findFirst());
    }
}

