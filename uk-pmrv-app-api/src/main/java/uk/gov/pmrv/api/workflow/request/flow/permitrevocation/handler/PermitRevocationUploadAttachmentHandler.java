package uk.gov.pmrv.api.workflow.request.flow.permitrevocation.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskUploadAttachmentActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.permitrevocation.service.PermitRevocationUploadAttachmentService;

@Component
@RequiredArgsConstructor
public class PermitRevocationUploadAttachmentHandler extends RequestTaskUploadAttachmentActionHandler {

    private final PermitRevocationUploadAttachmentService service;

    @Override
    public void uploadAttachment(final Long requestTaskId, final String attachmentUuid, final String filename) {
        service.uploadAttachment(requestTaskId, attachmentUuid, filename);
    }

    @Override
    public RequestTaskActionType getType() {
        return RequestTaskActionType.PERMIT_REVOCATION_UPLOAD_ATTACHMENT;
    }
}
