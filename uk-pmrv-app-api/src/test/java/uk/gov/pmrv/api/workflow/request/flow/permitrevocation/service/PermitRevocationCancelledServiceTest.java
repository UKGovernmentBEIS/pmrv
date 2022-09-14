package uk.gov.pmrv.api.workflow.request.flow.permitrevocation.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.permitrevocation.domain.PermitRevocationRequestPayload;

@ExtendWith(MockitoExtension.class)
class PermitRevocationCancelledServiceTest {

    @InjectMocks
    private PermitRevocationCancelledService service;

    @Mock
    private RequestService requestService;

    @Test
    void cancel() {

        final String requestId = "1";
        final PermitRevocationRequestPayload requestPayload = PermitRevocationRequestPayload.builder()
            .payloadType(RequestPayloadType.PERMIT_REVOCATION_REQUEST_PAYLOAD)
            .regulatorAssignee("regulatorAssignee")
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
            RequestActionType.PERMIT_REVOCATION_APPLICATION_CANCELLED,
            "regulatorAssignee");
    }
}
