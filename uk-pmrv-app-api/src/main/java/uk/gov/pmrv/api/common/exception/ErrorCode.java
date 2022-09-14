package uk.gov.pmrv.api.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Error Status enumerator with error codes.
 */
@Getter
public enum ErrorCode {

    /** Codes for User errors. */
    USER_REGISTRATION_FAILED_500("USER1000", HttpStatus.INTERNAL_SERVER_ERROR, "User registration failed"),
    USER_ALREADY_REGISTERED("USER1001", HttpStatus.BAD_REQUEST, "User is already registered"),
    USER_STATUS_DELETED("USER1002", HttpStatus.BAD_REQUEST, "User is deleted"),
    USER_INVALID_STATUS("USER1004", HttpStatus.BAD_REQUEST, "User status is not valid", true),
    USER_NOT_EXIST("USER1005", HttpStatus.BAD_REQUEST, "User not exist"),
    USER_SIGNATURE_NOT_EXIST("USER1006", HttpStatus.BAD_REQUEST, "User signature not exist"),

    /** Codes for Email errors. */
    VERIFICATION_LINK_EXPIRED("EMAIL1001", HttpStatus.BAD_REQUEST, "The verification link has expired", true),

    /** Codes for Account errors. */
    LEGAL_ENTITY_NOT_ASSOCIATED_WITH_USER("ACCOUNT1000", HttpStatus.BAD_REQUEST, "Legal entity is not associated with the user", true),
    ACCOUNT_ALREADY_EXISTS("ACCOUNT1001", HttpStatus.BAD_REQUEST, "Account name already exists", true),
    LEGAL_ENTITY_ALREADY_EXISTS("ACCOUNT1002", HttpStatus.BAD_REQUEST, "Legal entity name already exists", true),
    ACCOUNT_FIELD_NOT_AMENDABLE("ACCOUNT1003", HttpStatus.BAD_REQUEST, "Non amendable account fields"),
    ACCOUNT_NOT_RELATED_TO_CA("ACCOUNT1004", HttpStatus.BAD_REQUEST, "Account is not related to competent authority", true),
    ACCOUNT_NOT_RELATED_TO_VB("ACCOUNT1005", HttpStatus.BAD_REQUEST, "Account is not related to verification body", true),

    VERIFICATION_BODY_ALREADY_APPOINTED_TO_ACCOUNT("ACCOUNT1006", HttpStatus.BAD_REQUEST, "A verification body has already been appointed to the Installation account"),
    VERIFICATION_BODY_NOT_APPOINTED_TO_ACCOUNT("ACCOUNT1007", HttpStatus.BAD_REQUEST, "A verification body has not been appointed to the Installation account"),
    VERIFICATION_BODY_NOT_ACCREDITED_TO_ACCOUNTS_EMISSION_TRADING_SCHEME("ACCOUNT1008", HttpStatus.BAD_REQUEST, "The verification body is not accredited to the account's emission trading scheme", true),
    ACCOUNT_INVALID_STATUS("ACCOUNT1009", HttpStatus.BAD_REQUEST, "Account status is not valid"),
    VERIFICATION_BODY_CONTAINS_NON_UNIQUE_REF_NUM("VERBODY1001", HttpStatus.BAD_REQUEST, "Accreditation reference number already exists"),

    /** Account Contact Types errors */
    ACCOUNT_CONTACT_TYPE_PRIMARY_CONTACT_IS_REQUIRED("ACCOUNT_CONTACT1001", HttpStatus.BAD_REQUEST, "You must have a primary contact on your account", true),
    ACCOUNT_CONTACT_TYPE_FINANCIAL_CONTACT_IS_REQUIRED("ACCOUNT_CONTACT1002", HttpStatus.BAD_REQUEST, "You must have a financial contact on your account", true),
    ACCOUNT_CONTACT_TYPE_SERVICE_CONTACT_IS_REQUIRED("ACCOUNT_CONTACT1003", HttpStatus.BAD_REQUEST, "You must have a service contact on your account", true),
    ACCOUNT_CONTACT_TYPE_PRIMARY_AND_SECONDARY_CONTACT_ARE_IDENTICAL("ACCOUNT_CONTACT1004", HttpStatus.BAD_REQUEST,
               "You cannot assign the same user as a primary and secondary contact on your account", true),
    ACCOUNT_CONTACT_TYPE_PRIMARY_CONTACT_NOT_EMITTER_CONTACT("ACCOUNT_CONTACT1005", HttpStatus.BAD_REQUEST, "You cannot assign an Emitter Contact as primary contact on your account", true),
    ACCOUNT_CONTACT_TYPE_SECONDARY_CONTACT_NOT_EMITTER_CONTACT("ACCOUNT_CONTACT1006", HttpStatus.BAD_REQUEST, "You cannot assign an Emitter Contact as secondary contact on your account", true),

    /** Codes for Requests. */
    REQUEST_CREATE_ACTION_NOT_ALLOWED("REQUEST_CREATE_ACTION1000", HttpStatus.BAD_REQUEST, "Request create action not allowed", true),
    REQUEST_TASK_ACTION_CANNOT_PROCEED("REQUEST_TASK_ACTION1000", HttpStatus.BAD_REQUEST, "Request task action cannot proceed", true),
    REQUEST_TASK_ACTION_USER_NOT_THE_ASSIGNEE("REQUEST_TASK_ACTION1001", HttpStatus.BAD_REQUEST, "User is not the assignee of the request task", true),

    /** Codes for Authority errors. */
    AUTHORITY_CREATION_NOT_ALLOWED("AUTHORITY1000", HttpStatus.BAD_REQUEST, "Can not assign authority. Another role is already assigned to user.", true),
    AUTHORITY_MIN_ONE_OPERATOR_ADMIN_SHOULD_EXIST("AUTHORITY1001", HttpStatus.BAD_REQUEST, "At least one operator admin should exist in account", true),
    AUTHORITY_USER_NOT_RELATED_TO_CA("AUTHORITY1003", HttpStatus.BAD_REQUEST, "User is not related to competent authority", true),
    AUTHORITY_USER_NOT_RELATED_TO_ACCOUNT("AUTHORITY1004", HttpStatus.BAD_REQUEST, "User is not related to account", true),
    AUTHORITY_USER_UPDATE_NON_PENDING_AUTHORITY_NOT_ALLOWED("AUTHORITY1005", HttpStatus.BAD_REQUEST, "User status cannot be updated", true),
    AUTHORITY_USER_NOT_RELATED_TO_VERIFICATION_BODY("AUTHORITY1006", HttpStatus.BAD_REQUEST, "User is not related to verification body", true),
    AUTHORITY_VERIFIER_ADMIN_SHOULD_EXIST("AUTHORITY1007", HttpStatus.BAD_REQUEST, "Active verifier admin should exist", true),
    AUTHORITY_INVALID_STATUS("AUTHORITY1008", HttpStatus.BAD_REQUEST, "Authority status in not valid", true),
    AUTHORITY_USER_IS_NOT_OPERATOR("AUTHORITY1009", HttpStatus.BAD_REQUEST, "User is not operator", true),
    AUTHORITY_USER_REGULATOR_NOT_ALLOWED_TO_ADD_OPERATOR_ROLE_TO_ACCOUNT("AUTHORITY1011", HttpStatus.BAD_REQUEST, "Regulator user can only add operator administrator users to an account", true),
    AUTHORITY_USER_ROLE_MODIFICATION_NOT_ALLOWED("AUTHORITY1012", HttpStatus.BAD_REQUEST, "User role can not be modified", true),
    AUTHORITY_USER_IS_NOT_VERIFIER("AUTHORITY1013", HttpStatus.BAD_REQUEST, "User is not verifier", true),

    /** Codes for Verification Body errors. */
    VERIFICATION_BODY_DOES_NOT_EXIST("VERBODY1002", HttpStatus.BAD_REQUEST, "Verification body does not exist"),

    /** Codes for Role errors. */
    ROLE_INVALID_OPERATOR_ROLE_CODE("ROLE1000", HttpStatus.BAD_REQUEST, "Invalid operator role code", true),

    /** Codes for Request task assignment errors. */
    ASSIGNMENT_NOT_ALLOWED("ITEM1000", HttpStatus.BAD_REQUEST, "Can not assign request to the provided user", true),
    REQUEST_TASK_NOT_ASSIGNABLE("ITEM1001", HttpStatus.BAD_REQUEST, "Request task is not assignable", true),

    /** Codes for notification errors. */
    EMAIL_TEMPLATE_NOT_FOUND("NOTIF1003", HttpStatus.INTERNAL_SERVER_ERROR, "Email template does not exist"),
    EMAIL_TEMPLATE_PROCESSING_FAILED("NOTIF1000", HttpStatus.INTERNAL_SERVER_ERROR, "Email template processing failed", true),
    DOCUMENT_TEMPLATE_FILE_NOT_FOUND("NOTIF1001", HttpStatus.BAD_REQUEST, "File does not exist for document template"),
    DOCUMENT_TEMPLATE_FILE_GENERATION_ERROR("NOTIF1002", HttpStatus.BAD_REQUEST, "Document template file generation failed", true),

    /** Codes for external contact errors. */
    EXTERNAL_CONTACT_NOT_RELATED_TO_CA("EXTCONTACT1000", HttpStatus.BAD_REQUEST, "External contact not related to competent authority", true),
    EXTERNAL_CONTACT_CA_NAME_ALREADY_EXISTS("EXTCONTACT1001", HttpStatus.BAD_REQUEST, "External contact with ca and name already exists"),
    EXTERNAL_CONTACT_CA_EMAIL_ALREADY_EXISTS("EXTCONTACT1002", HttpStatus.BAD_REQUEST, "External contact with ca and email already exists"),
    EXTERNAL_CONTACT_CA_NAME_EMAIL_ALREADY_EXISTS("EXTCONTACT1003", HttpStatus.BAD_REQUEST, "External contact with ca-name and ca-email already exists"),
    EXTERNAL_CONTACT_CA_MISSING("EXTCONTACT1004", HttpStatus.BAD_REQUEST, "External contact ids are missing"),

    /** Codes for Permit errors. */
    INVALID_PERMIT_REVIEW("PERMIT1002", HttpStatus.BAD_REQUEST, "Invalid Permit review", true),
    INVALID_PERMIT("PERMIT1003", HttpStatus.BAD_REQUEST, "Invalid Permit", true),

    INVALID_PERMIT_SURRENDER("PERMITSURRENDER1001", HttpStatus.BAD_REQUEST, "Invalid Permit surrender", true),

    INVALID_PERMIT_NOTIFICATION("PERMITNOTIFICATION1001", HttpStatus.BAD_REQUEST, "Invalid Permit notification", true),

    /** Codes for AER. */
    AER_CREATION_NOT_ALLOWED("AER1000", HttpStatus.BAD_REQUEST, "AER creation is not allowed", true),

    /** Unknown code error. */
    INTERNAL_SERVER("INT1001", HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()),

    /** Invalid Request Format. */
    INVALID_REQUEST_FORMAT("INVALID_REQUEST_FORMAT", HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.getReasonPhrase()),

    /** Form validations. */
    FORM_VALIDATION("FORM1001", HttpStatus.BAD_REQUEST, "Form validation failed", true),
    PARAMETERS_VALIDATION("FORM1002", HttpStatus.BAD_REQUEST, "Parameters validation failed", true),
    PARAMETERS_TYPE_MISMATCH("FORM1003", HttpStatus.BAD_REQUEST, "Parameters type mismatch", true),

    /** Token error code. */
    INVALID_TOKEN("TOKEN1001", HttpStatus.BAD_REQUEST, "Invalid Token", true),
    INVALID_OTP("OTP1001", HttpStatus.BAD_REQUEST, "Invalid OTP", true),

    /** Resource not found error code. */
    RESOURCE_NOT_FOUND("NOTFOUND1001", HttpStatus.NOT_FOUND, "Resource not found", true),

    /** Unauthorized error code. */
    UNAUTHORIZED("UNAUTHORIZED1001", HttpStatus.UNAUTHORIZED, HttpStatus.UNAUTHORIZED.getReasonPhrase(), true),

    FORBIDDEN("FORBIDDEN", HttpStatus.FORBIDDEN, HttpStatus.FORBIDDEN.getReasonPhrase(), true),
    METHOD_NOT_ALLOWED("METHOD_NOT_ALLOWED", HttpStatus.METHOD_NOT_ALLOWED, HttpStatus.METHOD_NOT_ALLOWED.getReasonPhrase(), true),
    UNSUPPORTED_MEDIA_TYPE("UNSUPPORTED_MEDIA_TYPE", HttpStatus.UNSUPPORTED_MEDIA_TYPE, HttpStatus.UNSUPPORTED_MEDIA_TYPE.getReasonPhrase(), true),
    NOT_ACCEPTABLE("NOT_ACCEPTABLE", HttpStatus.NOT_ACCEPTABLE, HttpStatus.NOT_ACCEPTABLE.getReasonPhrase(), true),

    /** File error codes */
    INFECTED_STREAM("FILE1001", HttpStatus.BAD_REQUEST, "Virus found in input stream", true),
    MIN_FILE_SIZE_ERROR("FILE1002", HttpStatus.BAD_REQUEST, "File size is less than minimum", true),
    MAX_FILE_SIZE_ERROR("FILE1003", HttpStatus.BAD_REQUEST, "File size is greater than maximum", true),
    UPLOAD_FILE_FAILED_ERROR("FILE1004", HttpStatus.BAD_REQUEST, "File upload failed"),
    INVALID_FILE_TYPE("FILE1005", HttpStatus.BAD_REQUEST, "This type of file cannot be uploaded"),
    INVALID_IMAGE_DIMENSIONS("IMAGE1001", HttpStatus.BAD_REQUEST, "Image dimensions are not valid", true),

    /** Payment error codes */
    FEE_CONFIGURATION_NOT_EXIST("PAYMENT1001", HttpStatus.BAD_REQUEST, "Fee has not been configured for the provided parameter combination"),
    INVALID_PAYMENT_METHOD("PAYMENT1002", HttpStatus.BAD_REQUEST, "Payment method is not valid"),
    EXTERNAL_PAYMENT_ID_NOT_EXIST("PAYMENT1003", HttpStatus.BAD_REQUEST, "Payment id does not exist"),
    PAYMENT_PROCESSING_FAILED("PAYMENT1004", HttpStatus.INTERNAL_SERVER_ERROR, "Payment processing failed"),
    MI_REPORT_TYPE_NOT_SUPPORTED("MIREPORT1000", HttpStatus.CONFLICT, "The provided MI Report Type is not supported");

    /** The error code. */
    private final String code;

    /** The http status. */
    private final HttpStatus httpStatus;

    /** The message. */
    private final String message;

    /** Whether the error is security related */
    private boolean security;

    ErrorCode(String code, HttpStatus httpStatus, String message) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.message = message;
    }

    ErrorCode(String code, HttpStatus httpStatus, String message, boolean isSecurity) {
        this(code, httpStatus, message);
        this.security = isSecurity;
    }
}
