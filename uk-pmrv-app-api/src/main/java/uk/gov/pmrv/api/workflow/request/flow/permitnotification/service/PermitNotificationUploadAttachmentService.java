package uk.gov.pmrv.api.workflow.request.flow.permitnotification.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationApplicationSubmitRequestTaskPayload;

@Service
@RequiredArgsConstructor
public class PermitNotificationUploadAttachmentService {

    private final RequestTaskService requestTaskService;

    @Transactional
    public void uploadAttachment(Long requestTaskId, String attachmentUuid, String filename) {
        RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        PermitNotificationApplicationSubmitRequestTaskPayload requestTaskPayload =
            (PermitNotificationApplicationSubmitRequestTaskPayload) requestTask.getPayload();
        requestTaskPayload.getPermitNotificationAttachments().put(UUID.fromString(attachmentUuid), filename);
        requestTaskService.saveRequestTask(requestTask);
    }
}
