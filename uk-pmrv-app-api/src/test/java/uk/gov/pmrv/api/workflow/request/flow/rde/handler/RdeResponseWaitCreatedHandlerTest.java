package uk.gov.pmrv.api.workflow.request.flow.rde.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.taskhandler.DynamicUserTaskDefinitionKey;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static uk.gov.pmrv.api.workflow.request.flow.common.taskhandler.DynamicUserTaskDefinitionKey.WAIT_FOR_RDE_RESPONSE;

@ExtendWith(MockitoExtension.class)
class RdeResponseWaitCreatedHandlerTest {

    @InjectMocks
    private RdeResponseWaitCreatedHandler handler;

    @Test
    void getTaskDefinition() {
        DynamicUserTaskDefinitionKey actual = handler.getTaskDefinition();

        assertEquals(WAIT_FOR_RDE_RESPONSE, actual);
    }

    @Test
    void getExpirationDateKey() {
        String actual = handler.getExpirationDateKey();

        assertEquals(BpmnProcessConstants.RDE_EXPIRATION_DATE, actual);
    }
}
