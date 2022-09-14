package uk.gov.pmrv.api.workflow.request.flow.aer.service;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aer.domain.AerApplicationRequestTaskPayload;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AerSectionUploadAttachmentService {

    private final RequestTaskService requestTaskService;

    @Transactional
    public void uploadAttachment(Long requestTaskId, String attachmentUuid, String filename) {
        RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        AerApplicationRequestTaskPayload requestTaskPayload = (AerApplicationRequestTaskPayload) requestTask.getPayload();

        requestTaskPayload.getAerAttachments().put(UUID.fromString(attachmentUuid), filename);
        requestTaskService.saveRequestTask(requestTask);
    }
}
