package uk.gov.pmrv.api.workflow.request.flow.rde.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.taskhandler.DynamicUserTaskDefinitionKey;
import uk.gov.pmrv.api.workflow.request.flow.rde.service.RdeCancelledService;

@ExtendWith(MockitoExtension.class)
class RdeWaitForResponseDeletedHandlerTest {

    @InjectMocks
    private RdeWaitForResponseDeletedHandler handler;

    @Mock
    private RdeCancelledService rdeCancelledService;


    @Test
    void delete_whenRdeOutcomeNotExists_thenWriteTimelineAction() {

        final String requestId = "1";
        final Map<String, Object> variables = Map.of(
            BpmnProcessConstants.RDE_EXPIRATION_DATE, "03-01-2022"
        );

        handler.process(requestId, variables);

        verify(rdeCancelledService, times(1)).cancel(requestId);
    }

    @Test
    void delete_whenRdeOutcomeExists_thenDoNotWriteTimelineAction() {

        final String requestId = "1";
        final Map<String, Object> variables = Map.of(
            BpmnProcessConstants.RDE_OUTCOME, "cancelled",
            BpmnProcessConstants.RDE_EXPIRATION_DATE, "03-01-2022"
        );

        handler.process(requestId, variables);

        verify(rdeCancelledService, never()).cancel(requestId);
    }

    @Test
    void getTaskDefinition() {
        assertThat(handler.getTaskDefinition()).isEqualTo(DynamicUserTaskDefinitionKey.WAIT_FOR_RDE_RESPONSE);
    }
}
