package uk.gov.pmrv.api.workflow.request.flow.rfi.service;


import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.rfi.domain.RequestTaskPayloadRfiAttachable;

@Service
@RequiredArgsConstructor
public class RfiUploadAttachmentService {

    private final RequestTaskService requestTaskService;

    @Transactional
    public void uploadAttachment(final Long requestTaskId, final String attachmentUuid, final String filename) {
        
        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        final RequestTaskPayloadRfiAttachable requestTaskPayload = (RequestTaskPayloadRfiAttachable) requestTask.getPayload();

        requestTaskPayload.getRfiAttachments().put(UUID.fromString(attachmentUuid), filename);
        requestTaskService.saveRequestTask(requestTask);
    }
}
