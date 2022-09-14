package uk.gov.pmrv.api.workflow.bpmn.handler.applicationreview;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestTaskTimeManagementService;

@ExtendWith(MockitoExtension.class)
class SetRequestTasksDueDateHandlerTest {

    @InjectMocks
    private SetRequestTasksDueDateHandler handler;

    @Mock
    private RequestTaskTimeManagementService requestTaskTimeManagementService;

    @Test
    void execute() {
        final DelegateExecution execution = mock(DelegateExecution.class);
        final String requestId = "1";
        final Date rfiStart = new Date();
        final Date reviewExpirationDate = DateUtils.addDays(rfiStart, 10);
        final LocalDate dueDate = reviewExpirationDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        
        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        when(execution.getVariable(BpmnProcessConstants.APPLICATION_REVIEW_EXPIRATION_DATE)).thenReturn(reviewExpirationDate);
        
        //invoke
        handler.execute(execution);
        
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(execution, times(1)).getVariable(BpmnProcessConstants.APPLICATION_REVIEW_EXPIRATION_DATE);
        verify(requestTaskTimeManagementService, times(1))
            .setDueDateToTasks(requestId, BpmnProcessConstants.APPLICATION_REVIEW_EXPIRATION_DATE, dueDate);
    }
}
