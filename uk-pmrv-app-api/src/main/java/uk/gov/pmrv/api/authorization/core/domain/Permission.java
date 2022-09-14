package uk.gov.pmrv.api.authorization.core.domain;

public enum Permission {

    // Installation Account Opening Application task
    PERM_INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW_VIEW_TASK,
    PERM_INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW_EXECUTE_TASK,
    
    // Installation Account Opening Archive task
    PERM_INSTALLATION_ACCOUNT_OPENING_ARCHIVE_VIEW_TASK,
    PERM_INSTALLATION_ACCOUNT_OPENING_ARCHIVE_EXECUTE_TASK,

    // Permit Issuance Application
    PERM_PERMIT_ISSUANCE_APPLICATION_SUBMIT_VIEW_TASK,
    PERM_PERMIT_ISSUANCE_APPLICATION_SUBMIT_EXECUTE_TASK,

    PERM_PERMIT_ISSUANCE_APPLICATION_REVIEW_VIEW_TASK,
    PERM_PERMIT_ISSUANCE_APPLICATION_REVIEW_EXECUTE_TASK,

    PERM_PERMIT_ISSUANCE_APPLICATION_PEER_REVIEW_VIEW_TASK,
    PERM_PERMIT_ISSUANCE_APPLICATION_PEER_REVIEW_EXECUTE_TASK,

    // Permit Surrender Application
    PERM_PERMIT_SURRENDER_APPLICATION_SUBMIT_VIEW_TASK,
    PERM_PERMIT_SURRENDER_APPLICATION_SUBMIT_EXECUTE_TASK,

    PERM_PERMIT_SURRENDER_REVIEW_VIEW_TASK,
    PERM_PERMIT_SURRENDER_REVIEW_EXECUTE_TASK,

    PERM_PERMIT_SURRENDER_PEER_REVIEW_VIEW_TASK,
    PERM_PERMIT_SURRENDER_PEER_REVIEW_EXECUTE_TASK,

    // Permit Revocation
    PERM_PERMIT_REVOCATION_SUBMIT_VIEW_TASK,
    PERM_PERMIT_REVOCATION_SUBMIT_EXECUTE_TASK,

    PERM_PERMIT_REVOCATION_PEER_REVIEW_VIEW_TASK,
    PERM_PERMIT_REVOCATION_PEER_REVIEW_EXECUTE_TASK,

    PERM_PERMIT_REVOCATION_MAKE_PAYMENT_VIEW_TASK,
    PERM_PERMIT_REVOCATION_MAKE_PAYMENT_EXECUTE_TASK,

    // Permit Notification Application
    PERM_PERMIT_NOTIFICATION_APPLICATION_SUBMIT_VIEW_TASK,
    PERM_PERMIT_NOTIFICATION_APPLICATION_SUBMIT_EXECUTE_TASK,

    PERM_PERMIT_NOTIFICATION_REVIEW_VIEW_TASK,
    PERM_PERMIT_NOTIFICATION_REVIEW_EXECUTE_TASK,

    PERM_PERMIT_NOTIFICATION_PEER_REVIEW_VIEW_TASK,
    PERM_PERMIT_NOTIFICATION_PEER_REVIEW_EXECUTE_TASK,
    
    //Permit Variation
    PERM_PERMIT_VARIATION_APPLICATION_SUBMIT_VIEW_TASK,
    PERM_PERMIT_VARIATION_APPLICATION_SUBMIT_EXECUTE_TASK,
    PERM_PERMIT_VARIATION_SUBMIT_REVIEW_VIEW_TASK,
    PERM_PERMIT_VARIATION_SUBMIT_REVIEW_EXECUTE_TASK,
    PERM_PERMIT_VARIATION_PEER_REVIEW_VIEW_TASK,
    PERM_PERMIT_VARIATION_PEER_REVIEW_EXECUTE_TASK,

    // AER Application
    PERM_AER_APPLICATION_SUBMIT_VIEW_TASK,
    PERM_AER_APPLICATION_SUBMIT_EXECUTE_TASK,

    PERM_AER_APPLICATION_REVIEW_VIEW_TASK,
    PERM_AER_APPLICATION_REVIEW_EXECUTE_TASK,

    // Permissions for Task Assignment
    PERM_TASK_ASSIGNMENT,
    
    // User management
    PERM_ACCOUNT_USERS_EDIT,
    PERM_CA_USERS_EDIT,
    PERM_VB_USERS_EDIT,
    PERM_VB_MANAGE,

}
