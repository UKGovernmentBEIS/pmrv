package uk.gov.pmrv.api.workflow.request.flow.permitvariation.service;


import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.permitvariation.domain.PermitVariationRequestPayload;

@ExtendWith(MockitoExtension.class)
class PermitVariationCancelledServiceTest {

    @InjectMocks
    private PermitVariationCancelledService service;

    @Mock
    private RequestService requestService;

    @Test
    void cancel() {

        final Request request = Request.builder()
            .id("1")
            .payload(PermitVariationRequestPayload.builder().operatorAssignee("operator").build())
            .build();

        when(requestService.findRequestById("1")).thenReturn(request);

        service.cancel("1", RoleType.OPERATOR);

        verify(requestService, times(1)).addActionToRequest(request,
            null,
            RequestActionType.PERMIT_VARIATION_APPLICATION_CANCELLED,
            "operator");
    }
}
