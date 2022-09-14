package uk.gov.pmrv.api.workflow.request.flow.rde.service;

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
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.common.domain.PermitIssuanceRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.rde.domain.RdeData;
import uk.gov.pmrv.api.workflow.request.flow.rde.domain.RdeDecisionPayload;
import uk.gov.pmrv.api.workflow.request.flow.rde.domain.RdeDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.rde.domain.RdePayload;

@ExtendWith(MockitoExtension.class)
class RdeCancelledServiceTest {

    @InjectMocks
    private RdeCancelledService service;

    @Mock
    private RequestService requestService;


    @Test
    void cancel() {

        final String requestId = "1";
        final PermitIssuanceRequestPayload requestPayload = PermitIssuanceRequestPayload.builder()
        		.rdeData(RdeData.builder()
        				.rdePayload(RdePayload.builder()
        		                .extensionDate(LocalDate.of(2023, 1, 1))
        		                .deadline(LocalDate.of(2022, 1, 1))
        		                .build())
        		            .rdeDecisionPayload(RdeDecisionPayload.builder().decision(RdeDecisionType.ACCEPTED).build())
        				.build())
            .regulatorAssignee("regulator")
            .build();
        final Request request = Request.builder()
            .id(requestId)
            .payload(requestPayload)
            .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);

        service.cancel(requestId);

        verify(requestService, times(1)).addActionToRequest(
            request,
            null,
            RequestActionType.RDE_CANCELLED,
            "regulator");
    }
}
