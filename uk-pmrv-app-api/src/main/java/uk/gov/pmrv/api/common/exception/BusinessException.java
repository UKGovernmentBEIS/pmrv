package uk.gov.pmrv.api.common.exception;

import lombok.Getter;

import java.util.List;

/**
 * Business logic Exception.
 */
@Getter
public class BusinessException extends RuntimeException {

    /** Serialisation version. */
    private static final long serialVersionUID = -3353116845579187958L;

    /** The error status. */
    private final ErrorCode errorCode;

    /** The violation list */
    private final Object[] data;

    /**
     * Construction of BusinessException with error status.
     *
     * @param errorCode {@link ErrorCode}.
     */
    public BusinessException (ErrorCode errorCode) {
        this(errorCode, List.of());
    }

    /**
     * Construction of BusinessException with error status and violation data.
     *
     * @param errorCode {@link ErrorCode}.
     * @param data the violation list data
     */
    public BusinessException(ErrorCode errorCode, Object... data) {
        this(errorCode, null, data);
    }
    
    /**
     * Construction of BusinessException with error status, cause, and violation data
     * @param errorCode {@link ErrorCode}.
     * @param cause 
     * @param data the violation list data
     */
    public BusinessException(ErrorCode errorCode, Throwable cause, Object... data) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
        this.data = data;
    }
}