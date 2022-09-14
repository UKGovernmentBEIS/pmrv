package uk.gov.pmrv.api.workflow.request.flow.permitsurrender.handler;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskUploadAttachmentActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.service.PermitSurrenderUploadAttachmentService;

@Component
@RequiredArgsConstructor
public class PermitSurrenderUploadAttachmentHandler extends RequestTaskUploadAttachmentActionHandler {

    private final PermitSurrenderUploadAttachmentService uploadAttachmentService;
    
    @Override
    public void uploadAttachment(Long requestTaskId, String attachmentUuid, String filename) {
        uploadAttachmentService.uploadAttachment(requestTaskId, attachmentUuid, filename);
    }

    @Override
    public RequestTaskActionType getType() {
        return RequestTaskActionType.PERMIT_SURRENDER_UPLOAD_ATTACHMENT;
    }
}
