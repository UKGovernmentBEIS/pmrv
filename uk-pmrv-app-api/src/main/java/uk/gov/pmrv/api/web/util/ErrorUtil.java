package uk.gov.pmrv.api.web.util;

import lombok.experimental.UtilityClass;

import org.springframework.http.ResponseEntity;

import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.web.controller.exception.ErrorResponse;

/**
 * ErrorUtil for error manipulation.
 */
@UtilityClass
public class ErrorUtil {

    /**
     * Constructs the {@link ErrorResponse}.
     *
     * @param data Error data populated
     * @param errorCode {@link ErrorCode}
     * @return {@link ErrorResponse}
     */
    public ResponseEntity<ErrorResponse> getErrorResponse(Object[] data, ErrorCode errorCode) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .security(errorCode.isSecurity())
                .data(data)
                .build();

        return new ResponseEntity<>(errorResponse, errorCode.getHttpStatus());
    }
}
