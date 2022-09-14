package uk.gov.pmrv.api.workflow.request.flow.rfi.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.common.domain.PermitIssuanceRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.rfi.domain.RfiOutcome;
import uk.gov.pmrv.api.workflow.request.flow.rfi.domain.RfiData;
import uk.gov.pmrv.api.workflow.request.flow.rfi.domain.RfiQuestionPayload;
import uk.gov.pmrv.api.workflow.request.flow.rfi.domain.RfiResponsePayload;
import uk.gov.pmrv.api.workflow.request.flow.rfi.domain.RfiResponseSubmitRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.rfi.domain.RfiResponseSubmitRequestTaskPayload;

@ExtendWith(MockitoExtension.class)
class RfiResponseSubmitActionHandlerTest {

    @InjectMocks
    private RfiResponseSubmitActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private WorkflowService workflowService;


    @Test
    void process() {

        final Long requestTaskId = 1L;
        final PmrvUser pmrvUser = PmrvUser.builder().userId("userId").build();

        final UUID referencedFile1 = UUID.randomUUID();
        final UUID referencedFile2 = UUID.randomUUID();

        final RfiQuestionPayload rfiQuestionPayload = RfiQuestionPayload.builder()
            .questions(List.of("that", "then"))
            .files(Set.of(referencedFile2))
            .build();

        final RfiResponsePayload rfiResponsePayload = RfiResponsePayload.builder()
            .answers(List.of("that", "then"))
            .files(Set.of(referencedFile1))
            .build();

        final RfiResponseSubmitRequestTaskActionPayload taskActionPayload =
            RfiResponseSubmitRequestTaskActionPayload.builder()
                .rfiResponsePayload(rfiResponsePayload)
                .build();

        final Map<UUID, String> rfiAttachments = new HashMap<>();
        rfiAttachments.put(referencedFile1, "rfiFileReferenced1");
        rfiAttachments.put(referencedFile2, "rfiFileReferenced2");
        final RfiResponseSubmitRequestTaskPayload requestTaskPayload =
            RfiResponseSubmitRequestTaskPayload.builder()
                .rfiQuestionPayload(rfiQuestionPayload)
                .rfiResponsePayload(rfiResponsePayload)
                .rfiAttachments(rfiAttachments)
                .build();

        final RequestTask requestTask = RequestTask.builder()
            .id(requestTaskId)
            .request(Request.builder().payload(PermitIssuanceRequestPayload.builder().rfiData(RfiData.builder().build()).build()).id("2").build())
            .type(RequestTaskType.PERMIT_ISSUANCE_APPLICATION_REVIEW)
            .payload(requestTaskPayload)
            .processTaskId("processTaskId")
            .build();


        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        handler.process(
            requestTaskId,
            RequestTaskActionType.RFI_RESPONSE_SUBMIT,
            pmrvUser,
            taskActionPayload);

        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(workflowService, times(1))
            .completeTask("processTaskId",
                Map.of(BpmnProcessConstants.REQUEST_ID, "2",
                    BpmnProcessConstants.RFI_OUTCOME, RfiOutcome.RESPONDED)
            );
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes()).containsExactly(RequestTaskActionType.RFI_RESPONSE_SUBMIT);
    }
}
