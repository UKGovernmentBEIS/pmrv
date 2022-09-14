package uk.gov.pmrv.api.workflow.request.flow.permitvariation.handler;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskUploadAttachmentActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.permitvariation.service.PermitVariationUploadAttachmentService;

@Component
@RequiredArgsConstructor
public class PermitVariationSectionUploadAttachmentHandler extends RequestTaskUploadAttachmentActionHandler {

    private final PermitVariationUploadAttachmentService permitVariationUploadAttachmentService;
    
    @Override
    public void uploadAttachment(Long requestTaskId, String attachmentUuid, String filename) {
    	permitVariationUploadAttachmentService.uploadAttachment(requestTaskId, attachmentUuid, filename);
    }

    @Override
    public RequestTaskActionType getType() {
        return RequestTaskActionType.PERMIT_VARIATION_UPLOAD_SECTION_ATTACHMENT;
    }
}