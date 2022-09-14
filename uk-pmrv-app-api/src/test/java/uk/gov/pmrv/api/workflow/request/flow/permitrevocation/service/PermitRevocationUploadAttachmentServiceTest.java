package uk.gov.pmrv.api.workflow.request.flow.permitrevocation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.permitrevocation.domain.PermitRevocationWaitForAppealRequestTaskPayload;

@ExtendWith(MockitoExtension.class)
class PermitRevocationUploadAttachmentServiceTest {

    @InjectMocks
    private PermitRevocationUploadAttachmentService service;
    
    @Mock
    private RequestTaskService requestTaskService;
    
    @Test
    void uploadAttachment() {
        
        final Long requestTaskId = 1L;
        final UUID attachmentUuid = UUID.randomUUID();
        final String filename = "fn";

        PermitRevocationWaitForAppealRequestTaskPayload taskPayload =
            PermitRevocationWaitForAppealRequestTaskPayload.builder()
                        .payloadType(RequestTaskPayloadType.PERMIT_REVOCATION_WAIT_FOR_APPEAL_PAYLOAD)
                        .reason("the reason")
                        .build();
        
        final RequestTask requestTask = RequestTask.builder().id(requestTaskId).payload(taskPayload).build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);
        
        service.uploadAttachment(requestTaskId, attachmentUuid.toString(), filename);
        
        assertThat(taskPayload.getRevocationAttachments()).containsExactly(entry(attachmentUuid, filename));
        
        verify(requestTaskService, times(1)).saveRequestTask(requestTask);
    }
}
