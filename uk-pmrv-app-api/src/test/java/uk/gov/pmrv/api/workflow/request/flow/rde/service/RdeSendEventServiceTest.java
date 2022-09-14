package uk.gov.pmrv.api.workflow.request.flow.rde.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.SubRequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestExpirationVarsBuilder;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RdeSendEventServiceTest {

    @InjectMocks
    private RdeSendEventService service;

    @Mock
    private WorkflowService workflowService;
    
    @Mock
    private RequestExpirationVarsBuilder requestExpirationVarsBuilder;

    @Test
    void send() {
        final String requestId = "1";
        final LocalDate deadline = LocalDate.now().plusDays(2);
        final Date deadlineDate = Date.from(deadline
                .atTime(LocalTime.MIN)
                .atZone(ZoneId.systemDefault())
                .toInstant());
        final Map<String, Object> expirationReminderVars = Map.of(
                "var1", "val1"
                );
        
        when(requestExpirationVarsBuilder.buildExpirationVars(SubRequestType.RDE, deadlineDate))
            .thenReturn(expirationReminderVars);

        // Invoke
        service.send(requestId, deadline);

        // Verify
        verify(requestExpirationVarsBuilder, times(1)).buildExpirationVars(SubRequestType.RDE, deadlineDate);
        ArgumentCaptor<Map<String, Object>> rdeVariablesCaptor = ArgumentCaptor.forClass(Map.class);
        verify(workflowService, times(1)).sendEvent(Mockito.eq(requestId), Mockito.eq(BpmnProcessConstants.RDE_REQUESTED), rdeVariablesCaptor.capture());
        Map<String, Object> resultRdeVars = rdeVariablesCaptor.getValue();
        
        assertThat(resultRdeVars).containsExactly(entry("var1", "val1"));
    }
}
