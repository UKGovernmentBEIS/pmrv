package uk.gov.pmrv.api.workflow.request.flow.common.taskhandler;

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
class ApplicationReviewCreatedHandlerTest {

    @InjectMocks
    private ApplicationReviewCreatedHandler handler;
    
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

        handler.create(requestId, processTaskId, variables);

        verify(requestTaskCreateService, times(1))
            .create(requestId, processTaskId, RequestTaskType.PERMIT_ISSUANCE_APPLICATION_REVIEW, null, localDate);
    }
}
