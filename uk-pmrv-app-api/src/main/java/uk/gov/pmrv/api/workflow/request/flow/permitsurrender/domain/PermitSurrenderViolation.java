package uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain;

import lombok.Getter;

@Getter
public enum PermitSurrenderViolation {
    
    ATTACHMENT_NOT_FOUND("Attachment not found"),
    ATTACHMENT_NOT_REFERENCED("Attachment is not referenced in permit surrender");

    private final String message;

    PermitSurrenderViolation(String message) {
        this.message = message;
    }
    
}
