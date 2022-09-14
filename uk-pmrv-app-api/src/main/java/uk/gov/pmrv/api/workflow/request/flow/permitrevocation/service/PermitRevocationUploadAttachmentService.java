package uk.gov.pmrv.api.workflow.request.flow.permitrevocation.service;


import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.permitrevocation.domain.PermitRevocationWaitForAppealRequestTaskPayload;

@Service
@RequiredArgsConstructor
public class PermitRevocationUploadAttachmentService {

    private final RequestTaskService requestTaskService;

    @Transactional
    public void uploadAttachment(final Long requestTaskId, final String attachmentUuid, final String filename) {

        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        final PermitRevocationWaitForAppealRequestTaskPayload requestTaskPayload =
            (PermitRevocationWaitForAppealRequestTaskPayload) requestTask.getPayload();

        requestTaskPayload.getRevocationAttachments().put(UUID.fromString(attachmentUuid), filename);
        requestTaskService.saveRequestTask(requestTask);
    }
}
