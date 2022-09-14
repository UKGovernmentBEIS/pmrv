package uk.gov.pmrv.api.workflow.bpmn.handler.applicationreview;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.SubRequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.service.ExtendReviewExpirationTimerService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestExpirationVarsBuilder;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ExtendReviewExpirationTimerHandler implements JavaDelegate {

    private final ExtendReviewExpirationTimerService extendReviewExpirationTimerService;
    private final RequestExpirationVarsBuilder requestExpirationVarsBuilder;

    @Override
    public void execute(DelegateExecution execution) {

        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        final LocalDate dueDateLocal = extendReviewExpirationTimerService.extendTimer(requestId, BpmnProcessConstants.APPLICATION_REVIEW_EXPIRATION_DATE);
        final Date dueDate = Date.from(dueDateLocal
                .atTime(LocalTime.MIN)
                .atZone(ZoneId.systemDefault())
                .toInstant());

        Map<String, Object> expirationVars = requestExpirationVarsBuilder
                .buildExpirationVars(SubRequestType.APPLICATION_REVIEW, dueDate);
        execution.setVariables(expirationVars);
    }
}
