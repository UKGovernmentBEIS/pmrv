package uk.gov.pmrv.api.web.controller.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import uk.gov.pmrv.api.common.domain.dto.validation.Violation;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.user.core.domain.dto.EmailDTO;

class ExceptionControllerAdviceTest {

    private ExceptionControllerAdvice exceptionControllerAdvice;

    @BeforeEach
    void setUp() {
        exceptionControllerAdvice = new ExceptionControllerAdvice();
    }

    @Test
    void handleBusinessException() {
        final ErrorCode errorCode = ErrorCode.USER_INVALID_STATUS;

        BusinessException businessException = new BusinessException(errorCode);

        //invoke
        ResponseEntity<ErrorResponse> errorResponseEntity =
            exceptionControllerAdvice.handleBusinessException(businessException);

        //assertions
        assertNotNull(errorResponseEntity);
        assertEquals(errorCode.getHttpStatus(), errorResponseEntity.getStatusCode());

        ErrorResponse errorResponse = errorResponseEntity.getBody();
        assertNotNull(errorResponse);
        assertEquals(errorCode.getCode(), errorResponse.getCode());
        assertEquals(errorCode.getMessage(), errorResponse.getMessage());
    }

    @Test
    void handleMethodArgumentNotValidException_field_error() throws NoSuchMethodException {
        final ErrorCode expectedErrorCode  = ErrorCode.FORM_VALIDATION;
        final String field = "email";
        final String fieldSetter = "setEmail";
        final String errorCode = "errorCode";
        final String defaultMessage = "defaultMessage";

        BeanPropertyBindingResult error = new BeanPropertyBindingResult(new EmailDTO(), field);
        error.rejectValue(field, errorCode, defaultMessage);

        MethodParameter parameter = new MethodParameter(EmailDTO.class.getMethod(fieldSetter, String.class), 0);

        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(parameter, error);

        //invoke
        ResponseEntity<ErrorResponse> errorResponseEntity =
            exceptionControllerAdvice.handleValidationException(exception);

        //assertions
        assertNotNull(errorResponseEntity);
        assertEquals(expectedErrorCode.getHttpStatus(), errorResponseEntity.getStatusCode());

        ErrorResponse errorResponse = errorResponseEntity.getBody();
        assertNotNull(errorResponse);
        assertEquals(expectedErrorCode.getCode(), errorResponse.getCode());
        assertEquals(expectedErrorCode.getMessage(), errorResponse.getMessage());
        assertEquals(1, errorResponse.getData().length);
        assertThat(errorResponse.getData()[0]).isInstanceOf(Violation.class);

        Violation violation = (Violation) errorResponse.getData()[0];
        assertEquals(field, violation.getFieldName());
        assertEquals(defaultMessage, violation.getMessage());
    }

    @Test
    void handleMethodArgumentNotValidException_object_error() throws NoSuchMethodException {
        final ErrorCode expectedErrorCode  = ErrorCode.FORM_VALIDATION;
        final String field = "email";
        final String fieldSetter = "setEmail";
        final String errorCode = "errorCode";
        final String defaultMessage = "defaultMessage";

        BeanPropertyBindingResult error = new BeanPropertyBindingResult(new EmailDTO(), field);
        error.rejectValue(null, errorCode, defaultMessage);

        MethodParameter parameter = new MethodParameter(EmailDTO.class.getMethod(fieldSetter, String.class), 0);

        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(parameter, error);

        //invoke
        ResponseEntity<ErrorResponse> errorResponseEntity =
            exceptionControllerAdvice.handleValidationException(exception);

        //assertions
        assertNotNull(errorResponseEntity);
        assertEquals(expectedErrorCode.getHttpStatus(), errorResponseEntity.getStatusCode());

        ErrorResponse errorResponse = errorResponseEntity.getBody();
        assertNotNull(errorResponse);
        assertEquals(expectedErrorCode.getCode(), errorResponse.getCode());
        assertEquals(expectedErrorCode.getMessage(), errorResponse.getMessage());
        assertEquals(1, errorResponse.getData().length);
        assertThat(errorResponse.getData()[0]).isInstanceOf(Violation.class);

        Violation violation = (Violation) errorResponse.getData()[0];
        assertEquals(field, violation.getFieldName());
        assertEquals(defaultMessage, violation.getMessage());
    }

    @Test
    void handleMissingServletRequestParameterException() {
        final ErrorCode expectedErrorCode = ErrorCode.PARAMETERS_VALIDATION;
        final String parameterName = "parameterName";
        final String parameterType = "parameterType";

        MissingServletRequestParameterException exception =
            new MissingServletRequestParameterException(parameterName, parameterType);

        //invoke
        ResponseEntity<ErrorResponse> errorResponseEntity =
            exceptionControllerAdvice.handleValidationException(exception);

        //assertions
        assertNotNull(errorResponseEntity);
        assertEquals(expectedErrorCode.getHttpStatus(), errorResponseEntity.getStatusCode());

        ErrorResponse errorResponse = errorResponseEntity.getBody();
        assertNotNull(errorResponse);
        assertEquals(expectedErrorCode.getCode(), errorResponse.getCode());
        assertEquals(expectedErrorCode.getMessage(), errorResponse.getMessage());
        assertEquals(1, errorResponse.getData().length);
        assertThat(errorResponse.getData()[0]).isInstanceOf(Violation.class);

        Violation violation = (Violation) errorResponse.getData()[0];
        assertEquals(parameterName, violation.getFieldName());
    }

    @Test
    void handleHttpRequestMethodNotSupported() {
        final ErrorCode expectedErrorCode = ErrorCode.METHOD_NOT_ALLOWED;
        final String method = "GET";
        HttpRequestMethodNotSupportedException exception = new HttpRequestMethodNotSupportedException(method);

        //invoke
        ResponseEntity<ErrorResponse> errorResponseEntity =
            exceptionControllerAdvice.handleHttpRequestMethodNotSupported(exception);

        //assertions
        assertNotNull(errorResponseEntity);
        assertEquals(expectedErrorCode.getHttpStatus(), errorResponseEntity.getStatusCode());

        ErrorResponse errorResponse = errorResponseEntity.getBody();
        assertNotNull(errorResponse);
        assertEquals(expectedErrorCode.getCode(), errorResponse.getCode());
        assertEquals(expectedErrorCode.getMessage(), errorResponse.getMessage());
        assertEquals(1, errorResponse.getData().length);
        assertThat(errorResponse.getData()[0]).isInstanceOf(String.class);

        String errorResponseData = (String) errorResponse.getData()[0];
        assertEquals(exception.getMessage(), errorResponseData);
    }

    @Test
    void handleHttpMediaTypeNotSupported() {
        final ErrorCode expectedErrorCode = ErrorCode.UNSUPPORTED_MEDIA_TYPE;
        final MediaType unsupportedMediaType = MediaType.APPLICATION_ATOM_XML;
        final List<MediaType> supportedMediaTypes = List.of(MediaType.APPLICATION_JSON);

        HttpMediaTypeNotSupportedException exception =
            new HttpMediaTypeNotSupportedException(unsupportedMediaType, supportedMediaTypes);

        //invoke
        ResponseEntity<ErrorResponse> errorResponseEntity =
            exceptionControllerAdvice.handleHttpMediaTypeNotSupported(exception);

        //assertions
        assertNotNull(errorResponseEntity);
        assertEquals(expectedErrorCode.getHttpStatus(), errorResponseEntity.getStatusCode());

        ErrorResponse errorResponse = errorResponseEntity.getBody();
        assertNotNull(errorResponse);
        assertEquals(expectedErrorCode.getCode(), errorResponse.getCode());
        assertEquals(expectedErrorCode.getMessage(), errorResponse.getMessage());
        assertEquals(1, errorResponse.getData().length);
        assertThat(errorResponse.getData()[0]).isInstanceOf(String.class);

        String errorResponseData = (String) errorResponse.getData()[0];
        assertEquals(exception.getMessage(), errorResponseData);
    }

    @Test
    void handleHttpMediaTypeNotAcceptable() {
        final ErrorCode expectedErrorCode = ErrorCode.NOT_ACCEPTABLE;
        final List<MediaType> supportedMediaTypes = List.of(MediaType.APPLICATION_JSON);

        HttpMediaTypeNotAcceptableException exception = new HttpMediaTypeNotAcceptableException(supportedMediaTypes);

        //invoke
        ResponseEntity<ErrorResponse> errorResponseEntity =
            exceptionControllerAdvice.handleHttpMediaTypeNotAcceptable(exception);

        //assertions
        assertNotNull(errorResponseEntity);
        assertEquals(expectedErrorCode.getHttpStatus(), errorResponseEntity.getStatusCode());

        ErrorResponse errorResponse = errorResponseEntity.getBody();
        assertNotNull(errorResponse);
        assertEquals(expectedErrorCode.getCode(), errorResponse.getCode());
        assertEquals(expectedErrorCode.getMessage(), errorResponse.getMessage());
        assertEquals(1, errorResponse.getData().length);
        assertThat(errorResponse.getData()[0]).isInstanceOf(String.class);

        String errorResponseData = (String) errorResponse.getData()[0];
        assertEquals(exception.getMessage(), errorResponseData);
    }

    @Test
    void handleMethodArgumentTypeMismatch() throws NoSuchMethodException {
        final ErrorCode expectedErrorCode = ErrorCode.PARAMETERS_TYPE_MISMATCH;
        final String fieldName = "amount";
        MethodParameter parameter = new MethodParameter(Object.class.getMethod("toString"), -1);

        MethodArgumentTypeMismatchException exception = new MethodArgumentTypeMismatchException(
            new HashMap<String, String>(), BigDecimal.class, fieldName, parameter, null);

        //invoke
        ResponseEntity<ErrorResponse> errorResponseEntity =
            exceptionControllerAdvice.handleMethodArgumentTypeMismatch(exception);

        //assertions
        assertNotNull(errorResponseEntity);
        assertEquals(expectedErrorCode.getHttpStatus(), errorResponseEntity.getStatusCode());

        ErrorResponse errorResponse = errorResponseEntity.getBody();
        assertNotNull(errorResponse);
        assertEquals(expectedErrorCode.getCode(), errorResponse.getCode());
        assertEquals(expectedErrorCode.getMessage(), errorResponse.getMessage());
        assertEquals(1, errorResponse.getData().length);
        assertThat(errorResponse.getData()[0]).isInstanceOf(Violation.class);

        Violation violation = (Violation) errorResponse.getData()[0];
        assertEquals(fieldName, violation.getFieldName());
    }

    @Test
    void handleGenericException() {
        final ErrorCode expectedErrorCode = ErrorCode.INTERNAL_SERVER;

        //invoke
        ResponseEntity<ErrorResponse> errorResponseEntity =
            exceptionControllerAdvice.handleGenericException(new Exception());

        //assertions
        assertNotNull(errorResponseEntity);
        assertEquals(expectedErrorCode.getHttpStatus(), errorResponseEntity.getStatusCode());

        ErrorResponse errorResponse = errorResponseEntity.getBody();
        assertNotNull(errorResponse);
        assertEquals(expectedErrorCode.getCode(), errorResponse.getCode());
        assertEquals(expectedErrorCode.getMessage(), errorResponse.getMessage());
        assertThat(errorResponse.getData()).isEmpty();
    }

    @Test
    void handleConstraintViolationException_form_violation() {
        final ErrorCode expectedErrorCode = ErrorCode.FORM_VALIDATION;
        final EmailDTO emailDTO = new EmailDTO("invalidEmail");

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<EmailDTO>> constraintViolations = validator.validate(emailDTO) ;
        ConstraintViolationException exception = new ConstraintViolationException(constraintViolations);
        //invoke
        ResponseEntity<ErrorResponse> errorResponseEntity =
            exceptionControllerAdvice.handleValidationException(exception);

        //assertions
        assertNotNull(errorResponseEntity);
        assertEquals(expectedErrorCode.getHttpStatus(), errorResponseEntity.getStatusCode());

        ErrorResponse errorResponse = errorResponseEntity.getBody();
        assertNotNull(errorResponse);
        assertEquals(expectedErrorCode.getCode(), errorResponse.getCode());
        assertEquals(expectedErrorCode.getMessage(), errorResponse.getMessage());
        assertEquals(1, errorResponse.getData().length);
        assertThat(errorResponse.getData()[0]).isInstanceOf(Violation.class);

        Violation violation = (Violation) errorResponse.getData()[0];
        assertEquals("email", violation.getFieldName());
    }

}