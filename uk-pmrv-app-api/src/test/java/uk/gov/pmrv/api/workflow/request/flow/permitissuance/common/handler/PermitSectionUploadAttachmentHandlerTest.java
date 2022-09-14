package uk.gov.pmrv.api.workflow.request.flow.permitissuance.common.handler;

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
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.common.service.PermitSectionUploadAttachmentService;


@ExtendWith(MockitoExtension.class)
class PermitSectionUploadAttachmentHandlerTest {

    @InjectMocks
    private PermitSectionUploadAttachmentHandler handler;

    @Mock
    private PermitSectionUploadAttachmentService permitSectionUploadAttachmentService;

    @Test
    void uploadAttachment() {
        Long requestTaskId = 1L;
        String filename = "filename";
        String attachmentUuid = UUID.randomUUID().toString();

        //invoke
        handler.uploadAttachment(requestTaskId, attachmentUuid, filename);

        verify(permitSectionUploadAttachmentService, times(1))
            .uploadAttachment(requestTaskId, attachmentUuid, filename);
    }

    @Test
    void getTypes() {
        assertThat(handler.getType()).isEqualTo(RequestTaskActionType.PERMIT_ISSUANCE_UPLOAD_SECTION_ATTACHMENT);
    }

}
