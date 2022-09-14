package uk.gov.pmrv.api.workflow.request.flow.rfi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.common.domain.PermitIssuanceRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.rfi.domain.RfiData;
import uk.gov.pmrv.api.workflow.request.flow.rfi.domain.RfiQuestionPayload;
import uk.gov.pmrv.api.workflow.request.flow.rfi.domain.RfiResponsePayload;

@ExtendWith(MockitoExtension.class)
class RfiTerminatedServiceTest {

    @InjectMocks
    private RfiTerminatedService service;

    @Mock
    private RequestService requestService;


    @Test
    void terminate() {

        final String requestId = "1";
        final UUID uuid = UUID.randomUUID();
        final Map<UUID, String> attachments = new HashMap<>();
        attachments.put(uuid, "file");
        final PermitIssuanceRequestPayload requestPayload = PermitIssuanceRequestPayload.builder()
        		.rfiData(RfiData.builder()
        				 .rfiQuestionPayload(RfiQuestionPayload.builder().questions(List.of("who", "what")).build())
        		            .rfiResponsePayload(RfiResponsePayload.builder().answers(List.of("him", "that")).build())
        		            .rfiAttachments(attachments)
                		.build())
            .regulatorAssignee("regulator")
            .build();
        final Request request = Request.builder()
            .id(requestId)
            .payload(requestPayload)
            .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);

        service.terminate(requestId);

        assertThat(requestPayload.getRfiData().getRfiQuestionPayload()).isNull();
        assertThat(requestPayload.getRfiData().getRfiResponsePayload()).isNull();
        assertThat(requestPayload.getRfiData().getRfiAttachments()).isEmpty();
    }
}
