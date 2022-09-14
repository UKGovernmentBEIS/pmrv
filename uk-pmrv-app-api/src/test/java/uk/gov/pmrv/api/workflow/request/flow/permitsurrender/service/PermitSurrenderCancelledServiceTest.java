package uk.gov.pmrv.api.workflow.request.flow.permitsurrender.service;


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
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.common.domain.PermitIssuanceRequestPayload;

@ExtendWith(MockitoExtension.class)
class PermitSurrenderCancelledServiceTest {

    @InjectMocks
    private PermitSurrenderCancelledService service;

    @Mock
    private RequestService requestService;

    @Test
    void cancel() {

        final Request request = Request.builder()
            .id("1")
            .payload(PermitIssuanceRequestPayload.builder().operatorAssignee("operator").build())
            .build();

        when(requestService.findRequestById("1")).thenReturn(request);

        service.cancel("1");

        verify(requestService, times(1))
            .addActionToRequest(request,
                null,
                RequestActionType.PERMIT_SURRENDER_APPLICATION_CANCELLED,
                "operator");
    }
}
