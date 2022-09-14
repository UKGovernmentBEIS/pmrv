package uk.gov.pmrv.api.workflow.request.flow.aer.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.flow.aer.service.AerSectionUploadAttachmentService;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AerSectionUploadAttachmentHandlerTest {

    @InjectMocks
    private AerSectionUploadAttachmentHandler handler;

    @Mock
    private AerSectionUploadAttachmentService aerSectionUploadAttachmentService;

    @Test
    void uploadAttachment() {
        Long requestTaskId = 1L;
        String filename = "filename";
        String attachmentUuid = UUID.randomUUID().toString();

        // Invoke
        handler.uploadAttachment(requestTaskId, attachmentUuid, filename);

        // Verify
        verify(aerSectionUploadAttachmentService, times(1))
                .uploadAttachment(requestTaskId, attachmentUuid, filename);
    }

    @Test
    void getType() {
        RequestTaskActionType actual = handler.getType();

        assertThat(actual).isEqualTo(RequestTaskActionType.AER_UPLOAD_SECTION_ATTACHMENT);
    }
}
