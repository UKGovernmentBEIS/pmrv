package uk.gov.pmrv.api.workflow.request.flow.rde.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.taskhandler.DynamicUserTaskDefinitionKey;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static uk.gov.pmrv.api.workflow.request.flow.common.taskhandler.DynamicUserTaskDefinitionKey.RDE_RESPONSE_SUBMIT;

@ExtendWith(MockitoExtension.class)
class RdeResponseSubmitCreatedHandlerTest {

    @InjectMocks
    private RdeResponseSubmitCreatedHandler handler;

    @Test
    void getTaskDefinition() {
        DynamicUserTaskDefinitionKey actual = handler.getTaskDefinition();

        assertEquals(RDE_RESPONSE_SUBMIT, actual);
    }

    @Test
    void getExpirationDateKey() {
        String actual = handler.getExpirationDateKey();

        assertEquals(BpmnProcessConstants.RDE_EXPIRATION_DATE, actual);
    }
}
