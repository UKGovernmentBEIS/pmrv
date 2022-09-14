package uk.gov.pmrv.api.notification.template.domain.enumeration;

public enum DocumentTemplateType {

    /** INExtension_RequestRFI_#L025 */
    IN_RFI,
    
    /** INExtension_RequestRDE_#L026 */
    IN_RDE,
    
    /** L028_Surrendered */
    PERMIT_SURRENDERED_NOTICE,
    
    /** L031_SurrenderRefused */
    PERMIT_SURRENDER_REJECTED_NOTICE,
    
    /** L132_SurrenderDeemedWithdrawn */
    PERMIT_SURRENDER_DEEMED_WITHDRAWN_NOTICE,
    
    /** L022_CessationFollowingSurrender */
    PERMIT_SURRENDER_CESSATION,
    
    /** L020_P3_Revocation */
    PERMIT_REVOCATION,
    
    /** L021_P3_Revocation_Withdrawn */
    PERMIT_REVOCATION_WITHDRAWN,
    
    /** L022_P3_Cessation */
    PERMIT_REVOCATION_CESSATION,

    /** L010_IN_Notification_Accepted */
    PERMIT_NOTIFICATION_ACCEPTED,

    /** L011_IN_Notification_Refused */
    PERMIT_NOTIFICATION_REFUSED,
}
