package uk.gov.pmrv.api.workflow.request.flow.common.constants;

import lombok.experimental.UtilityClass;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.SubRequestType;

/**
 * Encapsulates constants related to BPMN Process
 */
@UtilityClass
public class BpmnProcessConstants {
    
    public static final String _EXPIRATION_DATE = "ExpirationDate";
    public static final String _FIRST_REMINDER_DATE = "FirstReminderDate";
    public static final String _SECOND_REMINDER_DATE = "SecondReminderDate";

    public static final String REQUEST_ID = "requestId";
    public static final String REQUEST_STATUS = "requestStatus";
    public static final String APPLICATION_ACCEPTED = "applicationAccepted";
    public static final String REQUEST_TASK_TYPE = "requestTaskType";
    public static final String REQUEST_TASK_ASSIGNEE = "requestTaskAssignee";
    public static final String REQUEST_TYPE = "requestType";
    public static final String REQUEST_INITIATOR_ROLE_TYPE = "requestInitiatorRoleType";
    public static final String BUSINESS_KEY = "businessKey";
    
    public static final String APPLICATION_REVIEW_EXPIRATION_DATE = SubRequestType.APPLICATION_REVIEW.getCode() + _EXPIRATION_DATE;
    public static final String REVIEW_DETERMINATION = "reviewDetermination";
    public static final String REVIEW_OUTCOME = "reviewOutcome";

    public static final String ACCOUNT_IDS = "accountIds";
    
    // rfi
    public static final String RFI_REQUESTED = "rfiRequested";
    public static final String RFI_START_TIME = "rfiStartTime";
    public static final String RFI_EXPIRATION_DATE = SubRequestType.RFI.getCode() + _EXPIRATION_DATE;
    public static final String RFI_FIRST_REMINDER_DATE = SubRequestType.RFI.getCode() + _FIRST_REMINDER_DATE;
    public static final String RFI_SECOND_REMINDER_DATE = SubRequestType.RFI.getCode() + _SECOND_REMINDER_DATE;
    public static final String RFI_OUTCOME = "rfiOutcome";

    // Request for Determination Extension (RDE)
    public static final String RDE_REQUESTED = "rdeRequested";
    public static final String RDE_EXPIRATION_DATE = SubRequestType.RDE.getCode() + _EXPIRATION_DATE;
    public static final String RDE_FIRST_REMINDER_DATE = SubRequestType.RDE.getCode() + _FIRST_REMINDER_DATE;
    public static final String RDE_SECOND_REMINDER_DATE = SubRequestType.RDE.getCode() + _SECOND_REMINDER_DATE;
    public static final String RDE_OUTCOME = "rdeOutcome";
    
    // surrender
    public static final String SURRENDER_OUTCOME = "surrenderOutcome";
    public static final String SURRENDER_REMINDER_NOTICE_DATE = "surrenderReminderNoticeDate";

    // Permit Notification
    public static final String NOTIFICATION_OUTCOME = "notificationOutcome";
    public static final String FOLLOW_UP_RESPONSE_NEEDED = "followUpResponseNeeded";
    public static final String FOLLOW_UP_RESPONSE_EXPIRATION_DATE = SubRequestType.FOLLOW_UP_RESPONSE.getCode() + _EXPIRATION_DATE;
    public static final String FOLLOW_UP_RESPONSE_FIRST_REMINDER_DATE = SubRequestType.FOLLOW_UP_RESPONSE.getCode() + _FIRST_REMINDER_DATE;
    public static final String FOLLOW_UP_RESPONSE_SECOND_REMINDER_DATE = SubRequestType.FOLLOW_UP_RESPONSE.getCode() + _SECOND_REMINDER_DATE;
    public static final String FOLLOW_UP_TIMER_EXTENDED = "followUpTimerExtended";
    
    // revocation
    public static final String REVOCATION_OUTCOME = "revocationOutcome";
    public static final String REVOCATION_EFFECTIVE_DATE = "revocationEffectiveDate";
    public static final String REVOCATION_REMINDER_EFFECTIVE_DATE = "revocationReminderEffectiveDate";
    
    // permit variation
    public static final String PERMIT_VARIATION_SUBMIT_OUTCOME = "permitVariationSubmitOutcome";

    // AER
    public static final String AER_OUTCOME = "aerOutcome";
    public static final String AER_REVIEW_OUTCOME = "aerReviewOutcome";
    public static final String AER_EXPIRATION_DATE = SubRequestType.AER.getCode() + _EXPIRATION_DATE;
    public static final String AER_FIRST_REMINDER_DATE = SubRequestType.AER.getCode() + _FIRST_REMINDER_DATE;
    public static final String AER_SECOND_REMINDER_DATE = SubRequestType.AER.getCode() + _SECOND_REMINDER_DATE;

    //payment
    public static final String PAYMENT_AMOUNT = "paymentAmount";
    public static final String PAYMENT_OUTCOME = "paymentOutcome";
    public static final String PAYMENT_REVIEW_OUTCOME = "paymentReviewOutcome";
    public static final String PAYMENT_EXPIRATION_DATE = SubRequestType.PAYMENT.getCode() + _EXPIRATION_DATE;
    public static final String PAYMENT_FIRST_REMINDER_DATE = SubRequestType.PAYMENT.getCode() + _FIRST_REMINDER_DATE;
    public static final String PAYMENT_SECOND_REMINDER_DATE = SubRequestType.PAYMENT.getCode() + _SECOND_REMINDER_DATE;

}
