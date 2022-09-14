package uk.gov.pmrv.api.workflow.request.flow.rfi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain.PermitIssuanceApplicationReviewRequestTaskPayload;

@ExtendWith(MockitoExtension.class)
class RfiUploadAttachmentServiceTest {

    @InjectMocks
    private RfiUploadAttachmentService service;

    @Mock
    private RequestTaskService requestTaskService;

    @Test
    void uploadAttachment() {
        
        Long requestTaskId = 1L;
        String fileName = "name";
        RequestTask requestTask = RequestTask.builder()
            .id(requestTaskId)
            .payload(PermitIssuanceApplicationReviewRequestTaskPayload.builder().build())
            .build();
        String attachmentUuid = UUID.randomUUID().toString();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        service.uploadAttachment(requestTaskId, attachmentUuid, fileName);

        verify(requestTaskService, times(1)).findTaskById(requestTaskId);

        ArgumentCaptor<RequestTask> requestTaskCaptor = ArgumentCaptor.forClass(RequestTask.class);
        verify(requestTaskService, times(1)).saveRequestTask(requestTaskCaptor.capture());

        RequestTask updateRequestTask = requestTaskCaptor.getValue();
        assertThat(updateRequestTask.getPayload().getAttachments()).containsEntry(UUID.fromString(attachmentUuid),
            fileName);
    }
}
