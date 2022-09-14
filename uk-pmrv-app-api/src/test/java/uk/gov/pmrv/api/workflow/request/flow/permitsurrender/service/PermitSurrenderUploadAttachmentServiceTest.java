package uk.gov.pmrv.api.workflow.request.flow.permitsurrender.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrender;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderApplicationSubmitRequestTaskPayload;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PermitSurrenderUploadAttachmentServiceTest {

    @InjectMocks
    private PermitSurrenderUploadAttachmentService service;
    
    @Mock
    private RequestTaskService requestTaskService;
    
    @Test
    void uploadAttachment() {
        Long requestTaskId = 1L;
        UUID attachmentUuid = UUID.randomUUID();
        String filename = "fn";
        
        PermitSurrenderApplicationSubmitRequestTaskPayload taskPayload = 
                PermitSurrenderApplicationSubmitRequestTaskPayload.builder()
                        .payloadType(RequestTaskPayloadType.PERMIT_SURRENDER_APPLICATION_SUBMIT_PAYLOAD)
                        .permitSurrender(PermitSurrender.builder().stopDate(LocalDate.now().minusDays(1)).build())
                        .build();
        
        RequestTask requestTask = RequestTask.builder().id(requestTaskId).payload(taskPayload).build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);
        
        assertThat(taskPayload.getPermitSurrenderAttachments()).isEmpty();
        
        service.uploadAttachment(requestTaskId, attachmentUuid.toString(), filename);
        
        assertThat(taskPayload.getPermitSurrenderAttachments()).containsExactly(entry(attachmentUuid, filename));
        verify(requestTaskService, times(1)).saveRequestTask(requestTask);
    }
}
