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
import uk.gov.pmrv.api.workflow.request.flow.rde.domain.RdeDecisionPayload;
import uk.gov.pmrv.api.workflow.request.flow.rde.domain.RdeDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.rde.domain.RdeRejectedRequestActionPayload;

@ExtendWith(MockitoExtension.class)
class RdeRespondedServiceTest {

    @InjectMocks
    private RdeRespondedService service;

    @Mock
    private RequestService requestService;

    @Test
    void reject() {

        final String requestId = "1";
        final PermitIssuanceRequestPayload requestPayload = PermitIssuanceRequestPayload.builder()
        		.rdeData(RdeData.builder()
        				.rdeDecisionPayload(RdeDecisionPayload.builder().decision(RdeDecisionType.REJECTED).reason("reason").build())			
        				.build())
            .operatorAssignee("operator")
            .build();
        final Request request = Request.builder()
            .id(requestId)
            .payload(requestPayload)
            .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);

        service.respond(requestId);
        
        ArgumentCaptor<RdeRejectedRequestActionPayload> actionPayloadArgumentCaptor =
            ArgumentCaptor.forClass(RdeRejectedRequestActionPayload.class);

        verify(requestService, times(1)).addActionToRequest(
            eq(request),
            actionPayloadArgumentCaptor.capture(),
            eq(RequestActionType.RDE_REJECTED),
            eq("operator"));

        final RdeRejectedRequestActionPayload captorValue = actionPayloadArgumentCaptor.getValue();
        assertThat(captorValue.getReason()).isEqualTo("reason");
    }

    @Test
    void accept() {

        final String requestId = "1";
        final PermitIssuanceRequestPayload requestPayload = PermitIssuanceRequestPayload.builder()
        		.rdeData(RdeData.builder()
        				 .rdeDecisionPayload(RdeDecisionPayload.builder().decision(RdeDecisionType.ACCEPTED).build())			
        				.build())
            .operatorAssignee("operator")
            .build();
        final Request request = Request.builder()
            .id(requestId)
            .payload(requestPayload)
            .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);

        service.respond(requestId);

        verify(requestService, times(1)).addActionToRequest(
            request,
            null,
            RequestActionType.RDE_ACCEPTED,
            "operator");
    }
}
