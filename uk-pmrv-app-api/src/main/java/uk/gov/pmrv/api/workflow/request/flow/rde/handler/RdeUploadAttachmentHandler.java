package uk.gov.pmrv.api.workflow.request.flow.rde.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskUploadAttachmentActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.rde.service.RdeUploadAttachmentService;

@Component
@RequiredArgsConstructor
public class RdeUploadAttachmentHandler extends RequestTaskUploadAttachmentActionHandler {

    private final RdeUploadAttachmentService rdeUploadAttachmentService;

    @Override
    public void uploadAttachment(Long requestTaskId, String attachmentUuid, String filename) {
        rdeUploadAttachmentService.uploadAttachment(requestTaskId, attachmentUuid, filename);
    }

    @Override
    public RequestTaskActionType getType() {
        return RequestTaskActionType.RDE_UPLOAD_ATTACHMENT;
    }
}
