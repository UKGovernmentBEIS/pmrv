package uk.gov.pmrv.api.workflow.bpmn.handler.applicationreview;

import org.apache.commons.lang3.time.DateUtils;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.SubRequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RecalculateAndRestartTimerService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestExpirationVarsBuilder;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecalculateAndRestartReviewExpirationTimerHandlerTest {

    @InjectMocks
    private RecalculateAndRestartReviewExpirationTimerHandler handler;

    @Mock
    private RecalculateAndRestartTimerService recalculateAndRestartTimerService;

    @Mock
    private RequestExpirationVarsBuilder requestExpirationVarsBuilder;
    
    @Test
    void execute() {
        final DelegateExecution execution = mock(DelegateExecution.class);
        final String requestId = "1";
        final Date rfiStart = new Date();
        final Date expiration = DateUtils.addDays(rfiStart, 10);
        final LocalDateTime dueDateLocal = LocalDateTime.of(2023, 1, 2, 0, 0);
        final Date dueDate = Date.from(dueDateLocal.atZone(ZoneId.systemDefault()).toInstant());
        final Map<String, Object> expirationReminderVars = Map.of(
                "var1", "val1"
                );

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        when(execution.getVariable(BpmnProcessConstants.RFI_START_TIME)).thenReturn(rfiStart);
        when(execution.getVariable(BpmnProcessConstants.APPLICATION_REVIEW_EXPIRATION_DATE))
            .thenReturn(expiration);

        when(recalculateAndRestartTimerService.updateDueDate(rfiStart, expiration, requestId, BpmnProcessConstants.APPLICATION_REVIEW_EXPIRATION_DATE))
            .thenReturn(dueDateLocal);

        when(requestExpirationVarsBuilder.buildExpirationVars(SubRequestType.APPLICATION_REVIEW, dueDate))
            .thenReturn(expirationReminderVars);

        //invoke
        handler.execute(execution);

        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(execution, times(1)).getVariable(BpmnProcessConstants.RFI_START_TIME);
        verify(execution, times(1)).getVariable(BpmnProcessConstants.APPLICATION_REVIEW_EXPIRATION_DATE);
        verify(recalculateAndRestartTimerService, times(1)).updateDueDate(rfiStart,
                expiration,
                requestId,
                BpmnProcessConstants.APPLICATION_REVIEW_EXPIRATION_DATE);
        verify(requestExpirationVarsBuilder, times(1)).buildExpirationVars(SubRequestType.APPLICATION_REVIEW, dueDate);
        verify(execution, times(1)).setVariables(expirationReminderVars);
    }

}
