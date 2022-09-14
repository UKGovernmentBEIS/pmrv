package uk.gov.pmrv.api.workflow.request.flow.aer.handler;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskCreateService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.taskhandler.DynamicUserTaskCreatedHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.taskhandler.DynamicUserTaskDefinitionKey;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class AerApplicationSubmitCreatedHandler implements DynamicUserTaskCreatedHandler {

    private final RequestTaskCreateService requestTaskCreateService;

    @Override
    public void create(final String requestId, final String processTaskId, final Map<String, Object> variables) {
        final Date dueDate = (Date) variables.get(BpmnProcessConstants.AER_EXPIRATION_DATE);
        LocalDate dueDateLd = dueDate != null ? dueDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate() : null;

        final RequestTaskType requestTaskType = RequestTaskType.valueOf(getTaskDefinition().name());

        requestTaskCreateService.create(requestId, processTaskId, requestTaskType, null, dueDateLd);
    }

    @Override
    public DynamicUserTaskDefinitionKey getTaskDefinition() {
        return DynamicUserTaskDefinitionKey.AER_APPLICATION_SUBMIT;
    }
}
