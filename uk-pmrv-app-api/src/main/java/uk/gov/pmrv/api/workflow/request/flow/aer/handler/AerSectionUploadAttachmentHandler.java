package uk.gov.pmrv.api.workflow.request.flow.aer.handler;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.flow.aer.service.AerSectionUploadAttachmentService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskUploadAttachmentActionHandler;

@Component
@RequiredArgsConstructor
public class AerSectionUploadAttachmentHandler extends RequestTaskUploadAttachmentActionHandler {

    private final AerSectionUploadAttachmentService aerSectionUploadAttachmentService;

    @Override
    public void uploadAttachment(Long requestTaskId, String attachmentUuid, String filename) {
        aerSectionUploadAttachmentService.uploadAttachment(requestTaskId, attachmentUuid, filename);
    }

    @Override
    public RequestTaskActionType getType() {
        return RequestTaskActionType.AER_UPLOAD_SECTION_ATTACHMENT;
    }
}
