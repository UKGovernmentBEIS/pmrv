package uk.gov.pmrv.api.workflow.request.flow.permitnotification.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskUploadAttachmentActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.service.PermitNotificationUploadAttachmentService;

@Component
@RequiredArgsConstructor
public class PermitNotificationUploadAttachmentHandler extends RequestTaskUploadAttachmentActionHandler {

    private final PermitNotificationUploadAttachmentService uploadAttachmentService;
    
    @Override
    public void uploadAttachment(Long requestTaskId, String attachmentUuid, String filename) {
        uploadAttachmentService.uploadAttachment(requestTaskId, attachmentUuid, filename);
    }

    @Override
    public RequestTaskActionType getType() {
        return RequestTaskActionType.PERMIT_NOTIFICATION_UPLOAD_ATTACHMENT;
    }
}
