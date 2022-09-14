package uk.gov.pmrv.api.workflow.request.flow.common.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.common.domain.PermitIssuanceRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.rde.domain.RdeData;
import uk.gov.pmrv.api.workflow.request.flow.rde.domain.RdePayload;

@ExtendWith(MockitoExtension.class)
class ExtendReviewExpirationTimerServiceTest {

    @InjectMocks
    private ExtendReviewExpirationTimerService service;

    @Mock
    private RequestService requestService;

    @Mock
    private RequestTaskTimeManagementService requestTaskTimeManagementService;

    @Test
    void extendTimer() {
        final String requestId = "1";
        final LocalDate extensionDate = LocalDate.of(2025, 1, 1);
        final Request request = Request.builder()
            .id(requestId)
            .payload(PermitIssuanceRequestPayload.builder()
            		.rdeData(RdeData.builder()
            				.rdePayload(RdePayload.builder()
            	                    .extensionDate(extensionDate)
            				.build())
                    .build())
                .build())
            .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);

        service.extendTimer(requestId, BpmnProcessConstants.APPLICATION_REVIEW_EXPIRATION_DATE);

        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestTaskTimeManagementService, times(1))
            .setDueDateToTasks(requestId, BpmnProcessConstants.APPLICATION_REVIEW_EXPIRATION_DATE, extensionDate);
    }
}
