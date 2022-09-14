package uk.gov.pmrv.api.workflow.request.flow.payment.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskCreateService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.taskhandler.DynamicUserTaskDefinitionKey;
import uk.gov.pmrv.api.workflow.request.flow.payment.handler.MakePaymentCreatedHandler;

@ExtendWith(MockitoExtension.class)
class MakePaymentCreatedHandlerTest {

    @InjectMocks
    private MakePaymentCreatedHandler makePaymentCreatedHandler;

    @Mock
    private RequestTaskCreateService requestTaskCreateService;

    @Test
    void create() {
        final String requestId = "1";
        final String processTaskId = "processTaskId";
        Map<String, Object> variables = new HashMap<>();
        variables.put(BpmnProcessConstants.REQUEST_TYPE, RequestType.PERMIT_ISSUANCE.name());

        makePaymentCreatedHandler.create(requestId, processTaskId, variables);

        verify(requestTaskCreateService, times(1))
            .create(requestId, processTaskId, RequestTaskType.PERMIT_ISSUANCE_MAKE_PAYMENT, null, null);
    }

    @Test
    void getTaskDefinition() {
        Assertions.assertEquals(DynamicUserTaskDefinitionKey.MAKE_PAYMENT, makePaymentCreatedHandler.getTaskDefinition());
    }
}