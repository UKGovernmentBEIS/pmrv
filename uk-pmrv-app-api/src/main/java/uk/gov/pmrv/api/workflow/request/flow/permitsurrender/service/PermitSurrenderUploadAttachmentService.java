package uk.gov.pmrv.api.workflow.request.flow.permitsurrender.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderApplicationSubmitRequestTaskPayload;

@Service
@RequiredArgsConstructor
public class PermitSurrenderUploadAttachmentService {

    private final RequestTaskService requestTaskService;

    @Transactional
    public void uploadAttachment(Long requestTaskId, String attachmentUuid, String filename) {
        RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        PermitSurrenderApplicationSubmitRequestTaskPayload requestTaskPayload =
            (PermitSurrenderApplicationSubmitRequestTaskPayload) requestTask.getPayload();

        requestTaskPayload.getPermitSurrenderAttachments().put(UUID.fromString(attachmentUuid), filename);
        requestTaskService.saveRequestTask(requestTask);
    }
}
