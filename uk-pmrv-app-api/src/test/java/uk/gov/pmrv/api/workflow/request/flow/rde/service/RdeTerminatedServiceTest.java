package uk.gov.pmrv.api.workflow.request.flow.rde.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.common.domain.PermitIssuanceRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.rde.domain.RdeData;
import uk.gov.pmrv.api.workflow.request.flow.rde.domain.RdeDecisionPayload;
import uk.gov.pmrv.api.workflow.request.flow.rde.domain.RdeDecisionType;

@ExtendWith(MockitoExtension.class)
class RdeTerminatedServiceTest {

    @InjectMocks
    private RdeTerminatedService service;

    @Mock
    private RequestService requestService;

    @Test
    void terminate() {

        final String requestId = "1";
        final PermitIssuanceRequestPayload requestPayload = PermitIssuanceRequestPayload.builder()
				.rdeData(RdeData.builder().rdeDecisionPayload(
						RdeDecisionPayload.builder().decision(RdeDecisionType.REJECTED).reason("reason").build())
						.build())
            .operatorAssignee("operator")
            .build();
        final Request request = Request.builder()
            .id(requestId)
            .payload(requestPayload)
            .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);

        service.terminate(requestId);

        assertThat(requestPayload.getRdeData().getRdePayload()).isNull();
        assertThat(requestPayload.getRdeData().getRdeDecisionPayload()).isNull();
        assertThat(requestPayload.getRdeData().getRdeForceDecisionPayload()).isNull();
        assertTrue(requestPayload.getRdeData().getRdeAttachments().isEmpty());
    }
}
