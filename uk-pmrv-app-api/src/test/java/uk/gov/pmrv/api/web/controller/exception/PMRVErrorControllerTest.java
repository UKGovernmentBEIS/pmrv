package uk.gov.pmrv.api.web.controller.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.web.logging.RestLoggingEntry;
import uk.gov.pmrv.api.web.logging.RestLoggingService;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PMRVErrorControllerTest {

    @InjectMocks
    private PMRVErrorController errorController;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private HttpServletRequest httpServletRequest;

    @Mock
    private HttpServletResponse httpServletResponse;

    @Mock
    private RestLoggingService restLoggingService;


    @Test
    void handleUnidentifiedError_error_status_code_401() throws IOException {
        final ErrorCode expectedErrorCode = ErrorCode.UNAUTHORIZED;
        when(httpServletRequest.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)).thenReturn(401);
        when(httpServletRequest.getMethod()).thenReturn(HttpMethod.POST.name());
        when(httpServletRequest.getInputStream().readAllBytes()).thenReturn("payload".getBytes());
        when(httpServletRequest.getCharacterEncoding()).thenReturn("UTF-8");
        when(httpServletRequest.getAttribute(RequestDispatcher.ERROR_REQUEST_URI)).thenReturn("/error");

        ResponseEntity<ErrorResponse> errorResponseEntity = errorController.handleUnidentifiedErrorGet(httpServletRequest, httpServletResponse);

        //assertions
        assertNotNull(errorResponseEntity);
        assertEquals(expectedErrorCode.getHttpStatus(), errorResponseEntity.getStatusCode());

        ErrorResponse errorResponse = errorResponseEntity.getBody();
        assertNotNull(errorResponse);
        assertEquals(expectedErrorCode.getCode(), errorResponse.getCode());
        assertEquals(expectedErrorCode.getMessage(), errorResponse.getMessage());
        assertThat(errorResponse.getData()).isEmpty();

        RestLoggingEntry requestLog = RestLoggingEntry.getRequestRestLoggingEntry("/error", httpServletRequest);
        RestLoggingEntry responseLog = RestLoggingEntry.getResponseRestLoggingEntry(HttpStatus.UNAUTHORIZED, errorResponse.toString(), httpServletResponse);
        Mockito.verify(restLoggingService, Mockito.times(1)).logRestRequestResponse(httpServletRequest, httpServletResponse, requestLog, responseLog);
    }

    @Test
    void handleUnidentifiedError_error_status_code_404() throws IOException {
        final ErrorCode expectedErrorCode = ErrorCode.RESOURCE_NOT_FOUND;
        when(httpServletRequest.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)).thenReturn(404);
        when(httpServletRequest.getMethod()).thenReturn(HttpMethod.POST.name());
        when(httpServletRequest.getInputStream().readAllBytes()).thenReturn("payload".getBytes());
        when(httpServletRequest.getCharacterEncoding()).thenReturn("UTF-8");
        when(httpServletRequest.getMethod()).thenReturn(HttpMethod.GET.name());
        when(httpServletRequest.getAttribute(RequestDispatcher.ERROR_REQUEST_URI)).thenReturn("/error");

        ResponseEntity<ErrorResponse> errorResponseEntity = errorController.handleUnidentifiedErrorPost(httpServletRequest, httpServletResponse);

        //assertions
        assertNotNull(errorResponseEntity);
        assertEquals(expectedErrorCode.getHttpStatus(), errorResponseEntity.getStatusCode());

        ErrorResponse errorResponse = errorResponseEntity.getBody();
        assertNotNull(errorResponse);
        assertEquals(expectedErrorCode.getCode(), errorResponse.getCode());
        assertEquals(expectedErrorCode.getMessage(), errorResponse.getMessage());
        assertThat(errorResponse.getData()).isEmpty();

        RestLoggingEntry requestLog = RestLoggingEntry.getRequestRestLoggingEntry("/error", httpServletRequest);
        RestLoggingEntry responseLog = RestLoggingEntry.getResponseRestLoggingEntry(HttpStatus.NOT_FOUND, errorResponse.toString(), httpServletResponse);
        Mockito.verify(restLoggingService, Mockito.times(1)).logRestRequestResponse(httpServletRequest, httpServletResponse, requestLog, responseLog);
    }

    @Test
    void handleUnidentifiedError_error_status_code_405() throws IOException {
        final ErrorCode expectedErrorCode = ErrorCode.METHOD_NOT_ALLOWED;
        when(httpServletRequest.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)).thenReturn(405);
        when(httpServletRequest.getMethod()).thenReturn(HttpMethod.POST.name());
        when(httpServletRequest.getInputStream().readAllBytes()).thenReturn("payload".getBytes());
        when(httpServletRequest.getCharacterEncoding()).thenReturn("UTF-8");
        when(httpServletRequest.getMethod()).thenReturn(HttpMethod.GET.name());
        when(httpServletRequest.getAttribute(RequestDispatcher.ERROR_REQUEST_URI)).thenReturn("/error");

        ResponseEntity<ErrorResponse> errorResponseEntity = errorController.handleUnidentifiedErrorPut(httpServletRequest, httpServletResponse);

        //assertions
        assertNotNull(errorResponseEntity);
        assertEquals(expectedErrorCode.getHttpStatus(), errorResponseEntity.getStatusCode());

        ErrorResponse errorResponse = errorResponseEntity.getBody();
        assertNotNull(errorResponse);
        assertEquals(expectedErrorCode.getCode(), errorResponse.getCode());
        assertEquals(expectedErrorCode.getMessage(), errorResponse.getMessage());
        assertThat(errorResponse.getData()).isEmpty();

        RestLoggingEntry requestLog = RestLoggingEntry.getRequestRestLoggingEntry("/error", httpServletRequest);
        RestLoggingEntry responseLog = RestLoggingEntry.getResponseRestLoggingEntry(HttpStatus.METHOD_NOT_ALLOWED, errorResponse.toString(), httpServletResponse);
        Mockito.verify(restLoggingService, Mockito.times(1)).logRestRequestResponse(httpServletRequest, httpServletResponse, requestLog, responseLog);
    }

    @Test
    void handleUnidentifiedError_error_status_code_406() throws IOException {
        final ErrorCode expectedErrorCode = ErrorCode.NOT_ACCEPTABLE;
        when(httpServletRequest.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)).thenReturn(406);
        when(httpServletRequest.getMethod()).thenReturn(HttpMethod.POST.name());
        when(httpServletRequest.getInputStream().readAllBytes()).thenReturn("payload".getBytes());
        when(httpServletRequest.getCharacterEncoding()).thenReturn("UTF-8");
        when(httpServletRequest.getMethod()).thenReturn(HttpMethod.GET.name());
        when(httpServletRequest.getAttribute(RequestDispatcher.ERROR_REQUEST_URI)).thenReturn("/error");

        ResponseEntity<ErrorResponse> errorResponseEntity = errorController.handleUnidentifiedErrorPatch(httpServletRequest, httpServletResponse);

        //assertions
        assertNotNull(errorResponseEntity);
        assertEquals(expectedErrorCode.getHttpStatus(), errorResponseEntity.getStatusCode());

        ErrorResponse errorResponse = errorResponseEntity.getBody();
        assertNotNull(errorResponse);
        assertEquals(expectedErrorCode.getCode(), errorResponse.getCode());
        assertEquals(expectedErrorCode.getMessage(), errorResponse.getMessage());
        assertThat(errorResponse.getData()).isEmpty();

        RestLoggingEntry requestLog = RestLoggingEntry.getRequestRestLoggingEntry("/error", httpServletRequest);
        RestLoggingEntry responseLog = RestLoggingEntry.getResponseRestLoggingEntry(HttpStatus.NOT_ACCEPTABLE, errorResponse.toString(), httpServletResponse);
        Mockito.verify(restLoggingService, Mockito.times(1)).logRestRequestResponse(httpServletRequest, httpServletResponse, requestLog, responseLog);
    }

    @Test
    void handleUnidentifiedError_error_status_code_415() throws IOException {
        final ErrorCode expectedErrorCode = ErrorCode.UNSUPPORTED_MEDIA_TYPE;
        when(httpServletRequest.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)).thenReturn(415);
        when(httpServletRequest.getMethod()).thenReturn(HttpMethod.POST.name());
        when(httpServletRequest.getInputStream().readAllBytes()).thenReturn("payload".getBytes());
        when(httpServletRequest.getCharacterEncoding()).thenReturn("UTF-8");
        when(httpServletRequest.getMethod()).thenReturn(HttpMethod.GET.name());
        when(httpServletRequest.getAttribute(RequestDispatcher.ERROR_REQUEST_URI)).thenReturn("/error");

        ResponseEntity<ErrorResponse> errorResponseEntity = errorController.handleUnidentifiedErrorDelete(httpServletRequest, httpServletResponse);

        //assertions
        assertNotNull(errorResponseEntity);
        assertEquals(expectedErrorCode.getHttpStatus(), errorResponseEntity.getStatusCode());

        ErrorResponse errorResponse = errorResponseEntity.getBody();
        assertNotNull(errorResponse);
        assertEquals(expectedErrorCode.getCode(), errorResponse.getCode());
        assertEquals(expectedErrorCode.getMessage(), errorResponse.getMessage());
        assertThat(errorResponse.getData()).isEmpty();

        RestLoggingEntry requestLog = RestLoggingEntry.getRequestRestLoggingEntry("/error", httpServletRequest);
        RestLoggingEntry responseLog = RestLoggingEntry.getResponseRestLoggingEntry(HttpStatus.UNSUPPORTED_MEDIA_TYPE, errorResponse.toString(), httpServletResponse);
        Mockito.verify(restLoggingService, Mockito.times(1)).logRestRequestResponse(httpServletRequest, httpServletResponse, requestLog, responseLog);
    }

    @Test
    void handleUnidentifiedError_error_status_code_500() throws IOException {
        final ErrorCode expectedErrorCode = ErrorCode.INTERNAL_SERVER;
        when(httpServletRequest.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)).thenReturn(500);
        when(httpServletRequest.getMethod()).thenReturn(HttpMethod.POST.name());
        when(httpServletRequest.getInputStream().readAllBytes()).thenReturn("payload".getBytes());
        when(httpServletRequest.getCharacterEncoding()).thenReturn("UTF-8");
        when(httpServletRequest.getMethod()).thenReturn(HttpMethod.GET.name());
        when(httpServletRequest.getAttribute(RequestDispatcher.ERROR_REQUEST_URI)).thenReturn("/error");

        ResponseEntity<ErrorResponse> errorResponseEntity = errorController.handleUnidentifiedErrorGet(httpServletRequest, httpServletResponse);

        //assertions
        assertNotNull(errorResponseEntity);
        assertEquals(expectedErrorCode.getHttpStatus(), errorResponseEntity.getStatusCode());

        ErrorResponse errorResponse = errorResponseEntity.getBody();
        assertNotNull(errorResponse);
        assertEquals(expectedErrorCode.getCode(), errorResponse.getCode());
        assertEquals(expectedErrorCode.getMessage(), errorResponse.getMessage());
        assertThat(errorResponse.getData()).isEmpty();

        RestLoggingEntry requestLog = RestLoggingEntry.getRequestRestLoggingEntry("/error", httpServletRequest);
        RestLoggingEntry responseLog = RestLoggingEntry.getResponseRestLoggingEntry(HttpStatus.INTERNAL_SERVER_ERROR, errorResponse.toString(), httpServletResponse);
        Mockito.verify(restLoggingService, Mockito.times(1)).logRestRequestResponse(httpServletRequest, httpServletResponse, requestLog, responseLog);
    }

    @Test
    void handleUnidentifiedError_error_status_code_null() throws IOException {
        final ErrorCode expectedErrorCode = ErrorCode.RESOURCE_NOT_FOUND;
        when(httpServletRequest.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)).thenReturn(null);
        when(httpServletRequest.getMethod()).thenReturn(HttpMethod.POST.name());
        when(httpServletRequest.getInputStream().readAllBytes()).thenReturn("payload".getBytes());
        when(httpServletRequest.getCharacterEncoding()).thenReturn("UTF-8");
        when(httpServletRequest.getMethod()).thenReturn(HttpMethod.GET.name());
        when(httpServletRequest.getAttribute(RequestDispatcher.ERROR_REQUEST_URI)).thenReturn("/error");

        ResponseEntity<ErrorResponse> errorResponseEntity = errorController.handleUnidentifiedErrorPost(httpServletRequest, httpServletResponse);

        //assertions
        assertNotNull(errorResponseEntity);
        assertEquals(expectedErrorCode.getHttpStatus(), errorResponseEntity.getStatusCode());

        ErrorResponse errorResponse = errorResponseEntity.getBody();
        assertNotNull(errorResponse);
        assertEquals(expectedErrorCode.getCode(), errorResponse.getCode());
        assertEquals(expectedErrorCode.getMessage(), errorResponse.getMessage());
        assertThat(errorResponse.getData()).isEmpty();

        RestLoggingEntry requestLog = RestLoggingEntry.getRequestRestLoggingEntry("/error", httpServletRequest);
        RestLoggingEntry responseLog = RestLoggingEntry.getResponseRestLoggingEntry(null, errorResponse.toString(), httpServletResponse);
        Mockito.verify(restLoggingService, Mockito.times(1)).logRestRequestResponse(httpServletRequest, httpServletResponse, requestLog, responseLog);
    }
}