package uk.gov.pmrv.api.workflow.request.flow.aer.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskCreateService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AerApplicationSubmitCreatedHandlerTest {

    @InjectMocks
    private AerApplicationSubmitCreatedHandler handler;

    @Mock
    private RequestTaskCreateService requestTaskCreateService;

    @Test
    void create() {
        final String requestId = "requestId";
        final String processTaskId = "processTaskId";
        LocalDate localDate = LocalDate.of(2022, 1, 1);
        Date dueDate = Timestamp.valueOf(localDate.atStartOfDay());
        Map<String, Object> variables = new HashMap<>(){{
           put(BpmnProcessConstants.AER_EXPIRATION_DATE, dueDate);
        }};

        // Invoke
        handler.create(requestId, processTaskId, variables);

        // Verify
        verify(requestTaskCreateService, times(1))
                .create(requestId, processTaskId, RequestTaskType.AER_APPLICATION_SUBMIT, null, localDate);
    }

    @Test
    void create_no_deadline() {
        final String requestId = "requestId";
        final String processTaskId = "processTaskId";

        // Invoke
        handler.create(requestId, processTaskId, new HashMap<>());

        // Verify
        verify(requestTaskCreateService, times(1))
                .create(requestId, processTaskId, RequestTaskType.AER_APPLICATION_SUBMIT, null, null);
    }
}
