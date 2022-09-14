package uk.gov.pmrv.api.workflow.request.flow.permitissuance.common.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskUploadAttachmentActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.common.service.PermitSectionUploadAttachmentService;

@Component
@RequiredArgsConstructor
public class PermitSectionUploadAttachmentHandler extends RequestTaskUploadAttachmentActionHandler {

    private final PermitSectionUploadAttachmentService permitSectionUploadAttachmentService;
    
    @Override
    public void uploadAttachment(Long requestTaskId, String attachmentUuid, String filename) {
        permitSectionUploadAttachmentService.uploadAttachment(requestTaskId, attachmentUuid, filename);
    }

    @Override
    public RequestTaskActionType getType() {
        return RequestTaskActionType.PERMIT_ISSUANCE_UPLOAD_SECTION_ATTACHMENT;
    }

}
