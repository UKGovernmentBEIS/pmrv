package uk.gov.pmrv.api.workflow.request.flow.permitnotification.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskUploadAttachmentActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.service.PermitNotificationFollowUpUploadAttachmentService;

@Component
@RequiredArgsConstructor
public class PermitNotificationFollowUpUploadAttachmentHandler extends RequestTaskUploadAttachmentActionHandler {

    private final PermitNotificationFollowUpUploadAttachmentService service;
    
    @Override
    public void uploadAttachment(Long requestTaskId, String attachmentUuid, String filename) {
        service.uploadAttachment(requestTaskId, attachmentUuid, filename);
    }

    @Override
    public RequestTaskActionType getType() {
        return RequestTaskActionType.PERMIT_NOTIFICATION_FOLLOW_UP_UPLOAD_ATTACHMENT;
    }
}
