package uk.gov.pmrv.api.workflow.request.flow.rfi.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestTaskAttachmentsUncoupleService;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.common.domain.PermitIssuanceRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.rfi.domain.RfiData;
import uk.gov.pmrv.api.workflow.request.flow.rfi.domain.RfiQuestionPayload;
import uk.gov.pmrv.api.workflow.request.flow.rfi.domain.RfiSubmitPayload;
import uk.gov.pmrv.api.workflow.request.flow.rfi.domain.RfiResponseSubmitRequestTaskPayload;

@ExtendWith(MockitoExtension.class)
class RfiResponseSubmitInitializerTest {

    @InjectMocks
    private RfiResponseSubmitInitializer initializer;

    @Mock
    private RequestTaskAttachmentsUncoupleService uncoupleService;

    @Test
    void initializePayload() {


        final UUID file1 = UUID.randomUUID();
        final UUID file2 = UUID.randomUUID();
        final LocalDate deadline = LocalDate.of(2023, 1, 1);
        final RfiQuestionPayload rfiQuestionPayload = RfiQuestionPayload.builder()
            .questions(List.of("what", "when", "how"))
            .files(Set.of(file1, file2))
            .build();
        final RfiSubmitPayload rfiSubmitPayload = RfiSubmitPayload.builder()
            .rfiQuestionPayload(rfiQuestionPayload)
            .deadline(deadline)
            .operators(Set.of("operator"))
            .signatory("signatory")
            .build();

        final Map<UUID, String> rfiAttachments = new HashMap<>();
        rfiAttachments.put(file1, "file1");
        rfiAttachments.put(file2, "file2");

        final PermitIssuanceRequestPayload permitRequestPayload = PermitIssuanceRequestPayload.builder()
            .payloadType(RequestPayloadType.PERMIT_ISSUANCE_REQUEST_PAYLOAD)
            .rfiData(RfiData.builder()
            		.rfiQuestionPayload(rfiQuestionPayload)
            		.rfiAttachments(rfiAttachments)
            		.build())
            .build();

        final Request request = Request.builder()
            .type(RequestType.PERMIT_ISSUANCE)
            .payload(permitRequestPayload)
            .build();

        final RequestTaskPayload requestTaskPayload = initializer.initializePayload(request);

        verify(uncoupleService, times(1)).uncoupleAttachments(requestTaskPayload);

        assertThat(requestTaskPayload.getPayloadType()).isEqualTo(
            RequestTaskPayloadType.RFI_RESPONSE_SUBMIT_PAYLOAD);
        assertThat(requestTaskPayload).isInstanceOf(RfiResponseSubmitRequestTaskPayload.class);
        assertThat(((RfiResponseSubmitRequestTaskPayload) requestTaskPayload).getRfiQuestionPayload().getQuestions()).isEqualTo(
            rfiSubmitPayload.getRfiQuestionPayload().getQuestions());
        assertThat(((RfiResponseSubmitRequestTaskPayload) requestTaskPayload).getRfiQuestionPayload().getFiles()).isEqualTo(
            rfiSubmitPayload.getRfiQuestionPayload().getFiles());
        assertThat(((RfiResponseSubmitRequestTaskPayload) requestTaskPayload).getRfiAttachments())
            .containsExactlyInAnyOrderEntriesOf(Map.of(file1, "file1", file2, "file2"));
    }

    @Test
    void getRequestTaskTypes() {
        assertEquals(initializer.getRequestTaskTypes(), Set.of(
                RequestTaskType.PERMIT_ISSUANCE_RFI_RESPONSE_SUBMIT,
                RequestTaskType.PERMIT_SURRENDER_RFI_RESPONSE_SUBMIT,
                RequestTaskType.PERMIT_NOTIFICATION_RFI_RESPONSE_SUBMIT,
                RequestTaskType.PERMIT_VARIATION_RFI_RESPONSE_SUBMIT
        ));
    }
}
