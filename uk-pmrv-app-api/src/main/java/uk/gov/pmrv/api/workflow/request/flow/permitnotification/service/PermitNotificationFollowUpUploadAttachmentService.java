package uk.gov.pmrv.api.workflow.request.flow.permitnotification.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationFollowUpRequestTaskPayload;

@Service
@RequiredArgsConstructor
public class PermitNotificationFollowUpUploadAttachmentService {

    private final RequestTaskService requestTaskService;

    @Transactional
    public void uploadAttachment(final Long requestTaskId,
                                 final String attachmentUuid,
                                 final String filename) {

        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        final PermitNotificationFollowUpRequestTaskPayload requestTaskPayload =
            (PermitNotificationFollowUpRequestTaskPayload) requestTask.getPayload();
        requestTaskPayload.getFollowUpAttachments().put(UUID.fromString(attachmentUuid), filename);
    }
}
