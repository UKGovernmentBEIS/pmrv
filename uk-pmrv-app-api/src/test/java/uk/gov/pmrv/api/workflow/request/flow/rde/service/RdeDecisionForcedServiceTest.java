package uk.gov.pmrv.api.workflow.request.flow.rde.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.common.domain.PermitIssuanceRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.rde.domain.RdeData;
import uk.gov.pmrv.api.workflow.request.flow.rde.domain.RdeDecisionForcedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.rde.domain.RdeDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.rde.domain.RdeForceDecisionPayload;

@ExtendWith(MockitoExtension.class)
class RdeDecisionForcedServiceTest {

    @InjectMocks
    private RdeDecisionForcedService service;

    @Mock
    private RequestService requestService;

    @Test
    void reject() {

        final String requestId = "1";
        final PermitIssuanceRequestPayload requestPayload = PermitIssuanceRequestPayload.builder()
				.rdeData(RdeData
						.builder().rdeForceDecisionPayload(RdeForceDecisionPayload.builder()
								.decision(RdeDecisionType.REJECTED).evidence("evidence").build())        				
        				.build())
            .regulatorAssignee("regulator")
            .build();
        final Request request = Request.builder()
            .id(requestId)
            .payload(requestPayload)
            .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);

        service.force(requestId);
        
        ArgumentCaptor<RdeDecisionForcedRequestActionPayload> actionPayloadArgumentCaptor =
            ArgumentCaptor.forClass(RdeDecisionForcedRequestActionPayload.class);

        verify(requestService, times(1)).addActionToRequest(
            eq(request),
            actionPayloadArgumentCaptor.capture(),
            eq(RequestActionType.RDE_FORCE_REJECTED),
            eq("regulator"));

        final RdeDecisionForcedRequestActionPayload captorValue = actionPayloadArgumentCaptor.getValue();
        assertThat(captorValue.getRdeForceDecisionPayload().getDecision()).isEqualTo(RdeDecisionType.REJECTED);
        assertThat(captorValue.getRdeForceDecisionPayload().getEvidence()).isEqualTo("evidence");
    }

    @Test
    void accept() {

        final String requestId = "1";
        final PermitIssuanceRequestPayload requestPayload = PermitIssuanceRequestPayload.builder()
				.rdeData(RdeData.builder()
						.rdeForceDecisionPayload(RdeForceDecisionPayload.builder().decision(RdeDecisionType.ACCEPTED)
								.evidence("evidence").build())
						.build())
            .regulatorAssignee("regulator")
            .build();
        final Request request = Request.builder()
            .id(requestId)
            .payload(requestPayload)
            .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);

        service.force(requestId);

        ArgumentCaptor<RdeDecisionForcedRequestActionPayload> actionPayloadArgumentCaptor =
            ArgumentCaptor.forClass(RdeDecisionForcedRequestActionPayload.class);
        
        verify(requestService, times(1)).addActionToRequest(
            eq(request),
            actionPayloadArgumentCaptor.capture(),
            eq(RequestActionType.RDE_FORCE_ACCEPTED),
            eq("regulator"));
        
        final RdeDecisionForcedRequestActionPayload captorValue = actionPayloadArgumentCaptor.getValue();
        assertThat(captorValue.getRdeForceDecisionPayload().getDecision()).isEqualTo(RdeDecisionType.ACCEPTED);
        assertThat(captorValue.getRdeForceDecisionPayload().getEvidence()).isEqualTo("evidence");
    }
}
