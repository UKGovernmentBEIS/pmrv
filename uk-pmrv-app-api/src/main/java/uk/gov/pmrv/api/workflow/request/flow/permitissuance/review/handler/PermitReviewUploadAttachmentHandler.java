package uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskUploadAttachmentActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.service.PermitReviewUploadAttachmentService;

@Component
@RequiredArgsConstructor
public class PermitReviewUploadAttachmentHandler extends RequestTaskUploadAttachmentActionHandler {

    private final PermitReviewUploadAttachmentService permitReviewUploadAttachmentService;

    @Override
    public void uploadAttachment(Long requestTaskId, String attachmentUuid, String filename) {
        permitReviewUploadAttachmentService.uploadAttachment(requestTaskId, attachmentUuid, filename);
    }

    @Override
    public RequestTaskActionType getType() {
        return RequestTaskActionType.PERMIT_ISSUANCE_UPLOAD_REVIEW_GROUP_DECISION_ATTACHMENT;
    }
}
