package uk.gov.pmrv.api.workflow.request.flow.common.service.notification;

public enum DocumentTemplateGenerationContextActionType {
    
    RFI_SUBMIT,
    RDE_SUBMIT,
    
    PERMIT_SURRENDER_GRANTED,
    PERMIT_SURRENDER_REJECTED,
    PERMIT_SURRENDER_DEEMED_WITHDRAWN,
    PERMIT_SURRENDER_CESSATION,
    
    PERMIT_REVOCATION,
    PERMIT_REVOCATION_WITHDRAWN,
    PERMIT_REVOCATION_CESSATION,

    PERMIT_NOTIFICATION_GRANTED,
    PERMIT_NOTIFICATION_REJECTED,
}
