package uk.gov.pmrv.api.workflow.request.flow.common.taskhandler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import java.util.Date;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskCreateService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@ExtendWith(MockitoExtension.class)
class WaitForPeerReviewCreatedHandlerTest {

    @InjectMocks
    private WaitForPeerReviewCreatedHandler waitForPeerReviewCreatedHandler;

    @Mock
    private RequestTaskCreateService requestTaskCreateService;

    @Test
    void create() {
        final Date date = new Date();
        final LocalDate localDate = LocalDate.now();
        final String requestId = "1";
        final String processTaskId = "processTaskId";
        final Map<String, Object> variables =
            Map.of(BpmnProcessConstants.APPLICATION_REVIEW_EXPIRATION_DATE, date,
                BpmnProcessConstants.REQUEST_TYPE, RequestType.PERMIT_ISSUANCE.name());

        waitForPeerReviewCreatedHandler.create(requestId, processTaskId, variables);

        verify(requestTaskCreateService, times(1))
            .create(requestId, processTaskId, RequestTaskType.PERMIT_ISSUANCE_WAIT_FOR_PEER_REVIEW, null, localDate);
    }

    @Test
    void getTaskDefinition() {
        assertEquals(DynamicUserTaskDefinitionKey.WAIT_FOR_PEER_REVIEW, waitForPeerReviewCreatedHandler.getTaskDefinition());
    }

    @Test
    void getExpirationDateKey() {
        assertEquals(BpmnProcessConstants.APPLICATION_REVIEW_EXPIRATION_DATE, waitForPeerReviewCreatedHandler.getExpirationDateKey());
    }
}
