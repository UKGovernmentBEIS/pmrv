package uk.gov.pmrv.api.workflow.request.flow.permitnotification.service;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import uk.gov.pmrv.api.files.attachments.service.FileAttachmentService;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
class PermitNotificationAttachmentsValidator {
    
    private final FileAttachmentService fileAttachmentService;
    
    public boolean attachmentsExist(final Set<UUID> sectionAttachments) {
        return sectionAttachments.isEmpty() || fileAttachmentService
                .fileAttachmentsExist(sectionAttachments.stream().map(UUID::toString).collect(Collectors.toSet()));
    }
    
    public boolean sectionAttachmentsReferencedInPermitNotification(final Set<UUID> sectionAttachments, final Set<UUID> permitNotificationAttachments) {
        return permitNotificationAttachments.containsAll(sectionAttachments);
    }

}
