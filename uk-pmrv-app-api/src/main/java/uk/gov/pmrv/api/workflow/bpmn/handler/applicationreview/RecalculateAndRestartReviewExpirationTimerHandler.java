package uk.gov.pmrv.api.workflow.bpmn.handler.applicationreview;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.SubRequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RecalculateAndRestartTimerService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestExpirationVarsBuilder;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RecalculateAndRestartReviewExpirationTimerHandler implements JavaDelegate {

    private final RecalculateAndRestartTimerService recalculateAndRestartTimerService;
    private final RequestExpirationVarsBuilder requestExpirationVarsBuilder;

    @Override
    public void execute(DelegateExecution execution) {
        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        final Date rfiStart = (Date) execution.getVariable(BpmnProcessConstants.RFI_START_TIME);
        final Date expiration = (Date) execution.getVariable(BpmnProcessConstants.APPLICATION_REVIEW_EXPIRATION_DATE);
        
        final LocalDateTime dueDateLocal = recalculateAndRestartTimerService.updateDueDate(rfiStart,
            expiration,
            requestId,
            BpmnProcessConstants.APPLICATION_REVIEW_EXPIRATION_DATE);
        final Date dueDate = Date.from(dueDateLocal.atZone(ZoneId.systemDefault()).toInstant());

        Map<String, Object> expirationVars = requestExpirationVarsBuilder
                .buildExpirationVars(SubRequestType.APPLICATION_REVIEW, dueDate);
        execution.setVariables(expirationVars);
    }
}
