package uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain;

import lombok.Getter;

@Getter
public enum PermitNotificationViolation {

    ATTACHMENT_NOT_FOUND("Attachment not found"),
    ATTACHMENT_NOT_REFERENCED("Attachment is not referenced in permit notification");

    private final String message;

    PermitNotificationViolation(String message) {
        this.message = message;
    }
    
}
