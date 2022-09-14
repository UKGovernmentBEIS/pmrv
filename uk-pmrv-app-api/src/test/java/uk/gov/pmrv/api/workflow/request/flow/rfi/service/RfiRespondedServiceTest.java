package uk.gov.pmrv.api.workflow.request.flow.rfi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestMetadataType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.common.domain.PermitIssuanceRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.common.domain.PermitIssuanceRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.rfi.domain.RfiData;
import uk.gov.pmrv.api.workflow.request.flow.rfi.domain.RfiQuestionPayload;
import uk.gov.pmrv.api.workflow.request.flow.rfi.domain.RfiResponsePayload;
import uk.gov.pmrv.api.workflow.request.flow.rfi.domain.RfiResponseSubmittedRequestActionPayload;

@ExtendWith(MockitoExtension.class)
class RfiRespondedServiceTest {

    @InjectMocks
    private RfiRespondedService service;

    @Mock
    private RequestService requestService;


    @Test
    void respond() {

        final String requestId = "1";
        final RequestTask task = RequestTask.builder()
            .type(RequestTaskType.PERMIT_ISSUANCE_APPLICATION_REVIEW)
            .build();
        final UUID uuid = UUID.randomUUID();
        final Map<UUID, String> attachments = new HashMap<>();
        attachments.put(uuid, "file");
        final PermitIssuanceRequestPayload requestPayload = PermitIssuanceRequestPayload.builder()
        		.rfiData(RfiData.builder()
        				.rfiQuestionPayload(RfiQuestionPayload.builder().questions(List.of("who", "what")).build())
        	            .rfiResponsePayload(RfiResponsePayload.builder().answers(List.of("him", "that")).build())
        	            .rfiAttachments(attachments)
                		.build())
            .operatorAssignee("operator")
            .build();
        final Request request = Request.builder()
            .id(requestId)
            .requestTasks(List.of(task))
            .payload(requestPayload)
            .metadata(PermitIssuanceRequestMetadata.builder()
                    .type(RequestMetadataType.PERMIT_ISSUANCE)
                    .build())
            .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);

        service.respond(requestId);
        
        ArgumentCaptor<RfiResponseSubmittedRequestActionPayload> actionPayloadArgumentCaptor =
            ArgumentCaptor.forClass(RfiResponseSubmittedRequestActionPayload.class);

        verify(requestService, times(1)).addActionToRequest(
            eq(request),
            actionPayloadArgumentCaptor.capture(),
            eq(RequestActionType.RFI_RESPONSE_SUBMITTED),
            eq("operator"));
        
        final RfiResponseSubmittedRequestActionPayload captorValue = actionPayloadArgumentCaptor.getValue();
        assertThat(captorValue.getRfiQuestionPayload().getQuestions()).isEqualTo(List.of("who", "what"));
        assertThat(captorValue.getRfiResponsePayload().getAnswers()).isEqualTo(List.of("him", "that"));
        assertThat(request.getMetadata()).isExactlyInstanceOf(PermitIssuanceRequestMetadata.class);
        assertThat(((PermitIssuanceRequestMetadata) request.getMetadata()).getRfiResponseDates()).isNotEmpty();
    }
}
