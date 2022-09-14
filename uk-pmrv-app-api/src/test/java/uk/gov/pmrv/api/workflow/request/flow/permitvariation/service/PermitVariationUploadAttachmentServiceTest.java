package uk.gov.pmrv.api.workflow.request.flow.permitvariation.service;

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

import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.abbreviations.Abbreviations;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.permitvariation.domain.PermitVariationApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitvariation.domain.PermitVariationApplicationSubmitRequestTaskPayload;

@ExtendWith(MockitoExtension.class)
class PermitVariationUploadAttachmentServiceTest {
	
	@InjectMocks
    private PermitVariationUploadAttachmentService service;
	
	@Mock
    private RequestTaskService requestTaskService;
	
	@Test
    void uploadAttachment() {
        Long requestTaskId = 1L;
        UUID attachmentUuid = UUID.randomUUID();
        String filename = "fn";
        
        PermitVariationApplicationSubmitRequestTaskPayload taskPayload = 
        		PermitVariationApplicationSubmitRequestTaskPayload.builder()
                        .payloadType(RequestTaskPayloadType.PERMIT_VARIATION_APPLICATION_SUBMIT_PAYLOAD)
                        .permit(Permit.builder().abbreviations(Abbreviations.builder().exist(true).build()).build())
                        .build();
        
        RequestTask requestTask = RequestTask.builder().id(requestTaskId).payload(taskPayload).build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);
        
        assertThat(taskPayload.getPermitAttachments()).isEmpty();
        
        service.uploadAttachment(requestTaskId, attachmentUuid.toString(), filename);
        
        assertThat(taskPayload.getPermitAttachments()).containsExactly(entry(attachmentUuid, filename));
        verify(requestTaskService, times(1)).saveRequestTask(requestTask);
    }

    @Test
    void uploadReviewGroupDecisionAttachment() {
        Long requestTaskId = 1L;
        UUID attachmentUuid = UUID.randomUUID();
        String filename = "fn";

        PermitVariationApplicationReviewRequestTaskPayload taskPayload =
            PermitVariationApplicationReviewRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.PERMIT_VARIATION_APPLICATION_REVIEW_PAYLOAD)
                .permit(Permit.builder().abbreviations(Abbreviations.builder().exist(true).build()).build())
                .build();

        RequestTask requestTask = RequestTask.builder().id(requestTaskId).payload(taskPayload).build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        assertThat(taskPayload.getReviewAttachments()).isEmpty();

        service.uploadReviewGroupDecisionAttachment(requestTaskId, attachmentUuid.toString(), filename);

        assertThat(taskPayload.getReviewAttachments()).containsExactly(entry(attachmentUuid, filename));
    }
}
