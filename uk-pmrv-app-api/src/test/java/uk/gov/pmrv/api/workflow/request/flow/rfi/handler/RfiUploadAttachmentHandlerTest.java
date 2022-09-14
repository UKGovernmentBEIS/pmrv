package uk.gov.pmrv.api.workflow.request.flow.rfi.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.flow.rfi.service.RfiUploadAttachmentService;


@ExtendWith(MockitoExtension.class)
class RfiUploadAttachmentHandlerTest {

    @InjectMocks
    private RfiUploadAttachmentHandler handler;

    @Mock
    private RfiUploadAttachmentService uploadAttachmentService;

    @Test
    void uploadAttachment() {

        final Long requestTaskId = 1L;
        final String filename = "filename";
        final String attachmentUuid = UUID.randomUUID().toString();

        handler.uploadAttachment(requestTaskId, attachmentUuid, filename);

        verify(uploadAttachmentService, times(1)).uploadAttachment(requestTaskId, attachmentUuid, filename);
    }

    @Test
    void getTypes() {
        assertThat(handler.getType()).isEqualTo(RequestTaskActionType.RFI_UPLOAD_ATTACHMENT);
    }
}
