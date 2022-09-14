package uk.gov.pmrv.api.workflow.request.flow.rfi.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.taskhandler.DynamicUserTaskDefinitionKey;
import uk.gov.pmrv.api.workflow.request.flow.rfi.service.RfiCancelledService;

@ExtendWith(MockitoExtension.class)
class RfiWaitForResponseDeletedHandlerTest {

    @InjectMocks
    private RfiWaitForResponseDeletedHandler handler;

    @Mock
    private RfiCancelledService rfiCancelledService;


    @Test
    void delete_whenRfiOutcomeNotExists_thenWriteTimelineAction() {

        final String requestId = "1";
        final Map<String, Object> variables = new HashMap<>();

        handler.process(requestId, variables);

        verify(rfiCancelledService, times(1)).cancel(requestId);
    }

    @Test
    void delete_whenRfiOutcomeExists_thenDoNotWriteTimelineAction() {

        final String requestId = "1";
        final Map<String, Object> variables = Map.of(
            BpmnProcessConstants.RFI_OUTCOME, "cancelled"
        );

        handler.process(requestId, variables);

        verify(rfiCancelledService, never()).cancel(requestId);
    }

    @Test
    void getTaskDefinition() {
        assertThat(handler.getTaskDefinition()).isEqualTo(DynamicUserTaskDefinitionKey.WAIT_FOR_RFI_RESPONSE);
    }
}
