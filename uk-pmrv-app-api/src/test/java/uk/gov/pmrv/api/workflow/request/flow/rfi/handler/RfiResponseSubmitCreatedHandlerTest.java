package uk.gov.pmrv.api.workflow.request.flow.rfi.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static uk.gov.pmrv.api.workflow.request.flow.common.taskhandler.DynamicUserTaskDefinitionKey.RFI_RESPONSE_SUBMIT;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.taskhandler.DynamicUserTaskDefinitionKey;

@ExtendWith(MockitoExtension.class)
class RfiResponseSubmitCreatedHandlerTest {

    @InjectMocks
    private RfiResponseSubmitCreatedHandler handler;

    @Test
    void getTaskDefinition() {
        DynamicUserTaskDefinitionKey actual = handler.getTaskDefinition();

        assertEquals(RFI_RESPONSE_SUBMIT, actual);
    }

    @Test
    void getExpirationDateKey() {
        String actual = handler.getExpirationDateKey();

        assertEquals(BpmnProcessConstants.RFI_EXPIRATION_DATE, actual);
    }
}
