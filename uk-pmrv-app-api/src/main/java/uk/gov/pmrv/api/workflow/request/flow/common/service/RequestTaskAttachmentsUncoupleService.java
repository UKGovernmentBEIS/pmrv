package uk.gov.pmrv.api.workflow.request.flow.common.service;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.files.common.domain.FileStatus;
import uk.gov.pmrv.api.files.attachments.service.FileAttachmentService;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;

@Service
@RequiredArgsConstructor
public class RequestTaskAttachmentsUncoupleService {

    private final FileAttachmentService fileAttachmentService;
    private final RequestTaskService requestTaskService;

    @Transactional
    public void uncoupleAttachments(Long requestTaskId) {

        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        final RequestTaskPayload requestTaskPayload = requestTask.getPayload();
        this.uncoupleAttachments(requestTaskPayload);
    }   
    
    @Transactional
    public void uncoupleAttachments(RequestTaskPayload requestTaskPayload) {

        if (requestTaskPayload == null) {
            return;
        }
        markReferencedAttachmentsAsSubmitted(requestTaskPayload);
        deleteUnreferencedAttachments(requestTaskPayload);
    }
    
    private void markReferencedAttachmentsAsSubmitted(RequestTaskPayload requestTaskPayload) {
        requestTaskPayload.getReferencedAttachmentIds().forEach(attUuid ->
                fileAttachmentService.updateFileAttachmentStatus(attUuid.toString(), FileStatus.SUBMITTED));
    }
    
    private void deleteUnreferencedAttachments(RequestTaskPayload requestTaskPayload) {
        
        Set<UUID> allAttachments = requestTaskPayload.getAttachments().keySet();
        Set<UUID> referencedAttachments = requestTaskPayload.getReferencedAttachmentIds();
        
        Set<UUID> unreferencedAttachments = new HashSet<>(allAttachments);
        unreferencedAttachments.removeAll(referencedAttachments);

        final Set<UUID> deletedAttachments = new HashSet<>();
        unreferencedAttachments.forEach(attUuid -> {
            fileAttachmentService.deletePendingFileAttachment(attUuid.toString());
            deletedAttachments.add(attUuid);
        });
        requestTaskPayload.removeAttachments(deletedAttachments);
    }
    
    @Transactional
    public void deletePendingAttachments(final Long requestTaskId) {

        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        final RequestTaskPayload requestTaskPayload = requestTask.getPayload();
        if (requestTaskPayload == null) {
            return;
        }
        final Set<UUID> deletedAttachments = new HashSet<>();
        requestTask.getPayload().getAttachments().keySet().forEach(attUuid -> {
            final boolean deleted = fileAttachmentService.deletePendingFileAttachment(attUuid.toString());
            if (deleted) {
                deletedAttachments.add(attUuid);
            }
        });
        requestTaskPayload.removeAttachments(deletedAttachments);
    }
}
