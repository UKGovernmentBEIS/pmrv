package uk.gov.pmrv.api.workflow.bpmn.handler.dynamicusertask;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.workflow.request.flow.common.taskhandler.DynamicUserTaskCreatedHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.taskhandler.DynamicUserTaskDefinitionKey;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DynamicUserTaskCreatedHandlerMapper {

    private final List<DynamicUserTaskCreatedHandler> handlers;

    public Optional<DynamicUserTaskCreatedHandler> get(final String taskDefinitionId) {

        final Optional<DynamicUserTaskDefinitionKey> taskDefinition = Arrays.stream(DynamicUserTaskDefinitionKey.values())
            .filter(v -> v.getId().equals(taskDefinitionId))
            .findFirst();

        return taskDefinition.flatMap(td ->
            handlers.stream()
                .filter(handler -> td.equals(handler.getTaskDefinition()))
                .findFirst());
    }
}

