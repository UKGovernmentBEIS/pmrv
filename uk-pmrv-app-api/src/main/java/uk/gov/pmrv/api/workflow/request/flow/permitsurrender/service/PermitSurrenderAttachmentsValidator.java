package uk.gov.pmrv.api.workflow.request.flow.permitsurrender.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.files.attachments.service.FileAttachmentService;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
class PermitSurrenderAttachmentsValidator {
    
    private final FileAttachmentService fileAttachmentService;
    
    public boolean attachmentsExist(final Set<UUID> sectionAttachments) {
        if (sectionAttachments.isEmpty()) {
            return true;
        } 
        
        return fileAttachmentService.fileAttachmentsExist(sectionAttachments.stream().map(UUID::toString).collect(Collectors.toSet()));
    }
    
    public boolean sectionAttachmentsReferencedInPermitSurrender(final Set<UUID> sectionAttachments, final Set<UUID> permitSurrenderAttachments) {
        return permitSurrenderAttachments.containsAll(sectionAttachments);
    }

}
