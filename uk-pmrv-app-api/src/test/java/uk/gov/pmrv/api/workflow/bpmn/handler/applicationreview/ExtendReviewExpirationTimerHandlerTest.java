package uk.gov.pmrv.api.workflow.bpmn.handler.applicationreview;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.SubRequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.service.ExtendReviewExpirationTimerService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestExpirationVarsBuilder;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExtendReviewExpirationTimerHandlerTest {

    @InjectMocks
    private ExtendReviewExpirationTimerHandler handler;

    @Mock
    private ExtendReviewExpirationTimerService extendReviewExpirationTimerService;

    @Mock
    private RequestExpirationVarsBuilder requestExpirationVarsBuilder;
    
    @Test
    void execute() {
        final DelegateExecution delegateExecution = mock(DelegateExecution.class);
        String requestId = "1";
        final LocalDate dueDateLocal = LocalDate.of(2023, 1, 2);
        final Date dueDate = Date.from(dueDateLocal
              .atTime(LocalTime.MIN)
              .atZone(ZoneId.systemDefault())
              .toInstant());
        
        final Map<String, Object> vars = Map.of(
                "var1", "val1"
                );
        
        when(delegateExecution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        when(extendReviewExpirationTimerService.extendTimer(requestId, BpmnProcessConstants.APPLICATION_REVIEW_EXPIRATION_DATE))
            .thenReturn(dueDateLocal);
        when(requestExpirationVarsBuilder.buildExpirationVars(SubRequestType.APPLICATION_REVIEW, dueDate))
            .thenReturn(vars);
        
        handler.execute(delegateExecution);
        
        verify(requestExpirationVarsBuilder, times(1)).buildExpirationVars(SubRequestType.APPLICATION_REVIEW, dueDate);
        verify(delegateExecution, times(1)).setVariables(vars);
    }

}
