package uk.gov.pmrv.api.workflow.request.flow.rfi.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static uk.gov.pmrv.api.workflow.request.flow.common.taskhandler.DynamicUserTaskDefinitionKey.WAIT_FOR_RFI_RESPONSE;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.taskhandler.DynamicUserTaskDefinitionKey;

@ExtendWith(MockitoExtension.class)
class RfiResponseWaitCreatedHandlerTest {

    @InjectMocks
    private RfiResponseWaitCreatedHandler handler;

    @Test
    void getTaskDefinition() {
        DynamicUserTaskDefinitionKey actual = handler.getTaskDefinition();

        assertEquals(WAIT_FOR_RFI_RESPONSE, actual);
    }

    @Test
    void getExpirationDateKey() {
        String actual = handler.getExpirationDateKey();

        assertEquals(BpmnProcessConstants.RFI_EXPIRATION_DATE, actual);
    }
}
