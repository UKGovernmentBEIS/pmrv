package uk.gov.pmrv.api.workflow.request.flow.permitvariation.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.permitvariation.domain.PermitVariationApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitvariation.domain.PermitVariationRequestTaskPayload;

@Service
@RequiredArgsConstructor
public class PermitVariationUploadAttachmentService {

    private final RequestTaskService requestTaskService;

    @Transactional
    public void uploadAttachment(Long requestTaskId, String attachmentUuid, String filename) {
        RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        PermitVariationRequestTaskPayload requestTaskPayload =
            (PermitVariationRequestTaskPayload) requestTask.getPayload();

        requestTaskPayload.getPermitAttachments().put(UUID.fromString(attachmentUuid), filename);
        requestTaskService.saveRequestTask(requestTask);
    }

    @Transactional
    public void uploadReviewGroupDecisionAttachment(Long requestTaskId, String attachmentUuid, String filename) {
        RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        PermitVariationApplicationReviewRequestTaskPayload requestTaskPayload =
            (PermitVariationApplicationReviewRequestTaskPayload) requestTask.getPayload();

        requestTaskPayload.getReviewAttachments().put(UUID.fromString(attachmentUuid), filename);
    }
}
