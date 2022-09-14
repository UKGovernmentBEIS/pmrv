package uk.gov.pmrv.api.web.controller.exception;

import lombok.extern.log4j.Log4j2;
import org.hibernate.validator.internal.metadata.descriptor.ConstraintDescriptorImpl;
import org.hibernate.validator.internal.metadata.location.ConstraintLocation;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import uk.gov.pmrv.api.common.domain.dto.validation.Violation;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.common.utils.ExceptionUtils;
import uk.gov.pmrv.api.web.util.ErrorUtil;

import javax.validation.ConstraintViolationException;

/**
 * Spring Controller for all Exceptions.
 */
@ControllerAdvice
@Log4j2
public class ExceptionControllerAdvice {

    /**
     * Exception Business Handler for all Business exceptions.
     *
     * @param e {@link BusinessException}
     * @return {@link ResponseEntity}
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
        log.error("Business Logic Exception:", ExceptionUtils.getRootCause(e));

        return ErrorUtil.getErrorResponse(e.getData(), e.getErrorCode());
    }

    /**
     * Exception Validation Handler that returns the error fields.
     *
     * @param e {@link MethodArgumentNotValidException}
     * @return {@link ResponseEntity}
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException e) {
        log.error("Method Argument Not Valid Exception:", ExceptionUtils.getRootCause(e));

        Object[] errors;

        if (e.getBindingResult().getFieldErrors().isEmpty()) {
            errors = e.getBindingResult().getAllErrors().stream()
                .map(field -> new Violation(field.getObjectName(), field.getDefaultMessage()))
                .toArray();

        } else {
            errors = e.getBindingResult().getFieldErrors().stream()
                .map(field -> new Violation(field.getField(), field.getDefaultMessage()))
                .toArray();
        }

        return ErrorUtil.getErrorResponse(errors, ErrorCode.FORM_VALIDATION);
    }

    /**
     * Exception Validation Handler that returns the error fields.
     *
     * @param e {@link ConstraintViolationException}
     * @return {@link ResponseEntity}
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleValidationException(ConstraintViolationException e) {
        log.error("Constraint Violation Exception:", ExceptionUtils.getRootCause(e));

        Object[] errors = e.getConstraintViolations().stream()
            .map(v -> new Violation(v.getPropertyPath().toString(), v.getMessage()))
            .toArray();

        boolean isParameter = e.getConstraintViolations().stream()
                .map(cv -> (ConstraintDescriptorImpl<?>) cv.getConstraintDescriptor())
                .anyMatch(constraintDescriptor -> constraintDescriptor.getConstraintLocationKind()
                        .equals(ConstraintLocation.ConstraintLocationKind.PARAMETER));

       return ErrorUtil.getErrorResponse(errors, isParameter ? ErrorCode.PARAMETERS_VALIDATION : ErrorCode.FORM_VALIDATION);
    }

    /**
     * Exception MissingServletRequestParameterException Handler that returns the error message.
     *
     * @param e {@link MissingServletRequestParameterException}
     * @return {@link ResponseEntity}
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleValidationException(MissingServletRequestParameterException e) {
        log.error("Missing Servlet Request Parameter Exception:", ExceptionUtils.getRootCause(e));

        Object[] errors = {new Violation(e.getParameterName(), e.getMessage())};

        return ErrorUtil.getErrorResponse(errors, ErrorCode.PARAMETERS_VALIDATION);
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleValidationException(MissingServletRequestPartException e) {
        log.error("Missing Servlet Request Part Exception:", ExceptionUtils.getRootCause(e));

        Object[] errors = { new Violation(e.getRequestPartName(), e.getMessage()) };

        return ErrorUtil.getErrorResponse(errors, ErrorCode.PARAMETERS_VALIDATION);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException e) {
        log.error("Http Request Method Not Supported Exception:", ExceptionUtils.getRootCause(e));

        return ErrorUtil.getErrorResponse(new Object[] {e.getMessage()}, ErrorCode.METHOD_NOT_ALLOWED );
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException e) {
        log.error("Http Media Type Not Supported Exception:", ExceptionUtils.getRootCause(e));

        return ErrorUtil.getErrorResponse(new Object[] {e.getMessage()}, ErrorCode.UNSUPPORTED_MEDIA_TYPE );
    }

    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleHttpMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException e) {
        log.error("Http Media Type Not Acceptable Exception:", ExceptionUtils.getRootCause(e));

        return ErrorUtil.getErrorResponse(new Object[] {e.getMessage()}, ErrorCode.NOT_ACCEPTABLE );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException e) {
        log.error("Method Argument Type Mismatch:", ExceptionUtils.getRootCause(e));

        Object[] errors = {new Violation(e.getName(), e.getValue() + " is not valid type")};

        return ErrorUtil.getErrorResponse(errors, ErrorCode.PARAMETERS_TYPE_MISMATCH );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.error("Invalid Request Format Exception:", ExceptionUtils.getRootCause(e));

        return ErrorUtil.getErrorResponse(new Object[] {}, ErrorCode.INVALID_REQUEST_FORMAT );
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleGenericException(Exception e) {
        log.error("Generic Server Exception:", ExceptionUtils.getRootCause(e));

        return ErrorUtil.getErrorResponse(new Object[]{}, ErrorCode.INTERNAL_SERVER);
    }
}
