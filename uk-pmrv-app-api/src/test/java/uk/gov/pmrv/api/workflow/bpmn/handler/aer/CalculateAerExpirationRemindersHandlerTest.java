package uk.gov.pmrv.api.workflow.bpmn.handler.aer;

import org.apache.commons.lang3.time.DateUtils;

import org.camunda.bpm.engine.delegate.DelegateExecution;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.SubRequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestExpirationVarsBuilder;

import java.util.Date;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CalculateAerExpirationRemindersHandlerTest {

    @InjectMocks
    private CalculateAerExpirationRemindersHandler handler;

    @Mock
    private RequestExpirationVarsBuilder requestExpirationVarsBuilder;

    @Test
    void execute() {
        final DelegateExecution execution = mock(DelegateExecution.class);
        final Date start = new Date();
        final Date expirationDate = DateUtils.addDays(start, 10);

        final Map<String, Object> vars = Map.of(
                "var1", "val1"
        );

        when(execution.getVariable(BpmnProcessConstants.AER_EXPIRATION_DATE)).thenReturn(expirationDate);
        when(requestExpirationVarsBuilder.buildExpirationReminderVars(SubRequestType.AER, expirationDate))
                .thenReturn(vars);

        // Invoke
        handler.execute(execution);

        // Verify
        verify(execution, times(1)).getVariable(BpmnProcessConstants.AER_EXPIRATION_DATE);
        verify(requestExpirationVarsBuilder, times(1))
                .buildExpirationReminderVars(SubRequestType.AER, expirationDate);
        verify(execution, times(1)).setVariables(vars);
    }
}
