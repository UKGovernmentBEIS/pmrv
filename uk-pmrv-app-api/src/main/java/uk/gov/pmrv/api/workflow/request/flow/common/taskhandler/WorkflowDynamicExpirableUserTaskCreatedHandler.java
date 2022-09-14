package uk.gov.pmrv.api.workflow.request.flow.common.taskhandler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskCreateService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;

@Component
@RequiredArgsConstructor
public abstract class WorkflowDynamicExpirableUserTaskCreatedHandler
    implements DynamicUserTaskCreatedHandler, UserTaskExpirable {

    private final RequestTaskCreateService requestTaskCreateService;

    @Override
    public void create(final String requestId, final String processTaskId, final Map<String, Object> variables) {

        final String expirationDateKey = this.getExpirationDateKey();
        final Date dueDate = (Date) variables.get(expirationDateKey);
        final String requestType = (String)variables.get(BpmnProcessConstants.REQUEST_TYPE);
        final String taskDefinitionKey = this.getTaskDefinition().getId();
        final String taskDefinition = requestType != null ? requestType + "_" + taskDefinitionKey : taskDefinitionKey;
        final RequestTaskType requestTaskType = RequestTaskType.valueOf(taskDefinition);
        final LocalDate dueDateLd = dueDate != null ? dueDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(): null;

        requestTaskCreateService.create(requestId, processTaskId, requestTaskType, null, dueDateLd);
    }
}
