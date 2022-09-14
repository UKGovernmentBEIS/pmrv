package uk.gov.pmrv.api.web.logging;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;

/**
 * Filter used to log Rest API requests/responses.
 */
@Component
@RequiredArgsConstructor
public class RestLoggingFilter extends OncePerRequestFilter {
    private final RestLoggingService restLoggingService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        MultiReadHttpServletRequestWrapper wrappedRequest = new MultiReadHttpServletRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);
        if (isAsyncDispatch(request)) {
            filterChain.doFilter(request, response);
        }

        RestLoggingEntry requestLog = getRequestLog(wrappedRequest);
        filterChain.doFilter(wrappedRequest, wrappedResponse);
        RestLoggingEntry responseLog = getResponseLog(wrappedResponse);

        restLoggingService.logRestRequestResponse(request, wrappedResponse, requestLog, responseLog);
        wrappedResponse.copyBodyToResponse();
    }

    private RestLoggingEntry getRequestLog(MultiReadHttpServletRequestWrapper request) {
        String requestUri = ObjectUtils.isEmpty(request.getQueryString()) ?
                request.getRequestURI() :
                request.getRequestURI().concat(request.getQueryString());
        return RestLoggingEntry.getRequestRestLoggingEntry(requestUri, request);
    }

    private RestLoggingEntry getResponseLog(ContentCachingResponseWrapper response) {
        HttpStatus httpStatus = HttpStatus.valueOf(response.getStatus());
        String payload = getResponsePayloadAsString(response);
        return RestLoggingEntry.getResponseRestLoggingEntry(httpStatus, payload, response);
    }

    private String getResponsePayloadAsString(ContentCachingResponseWrapper response) {
        if(response.getHeader(CONTENT_DISPOSITION) != null) {
            return "[fileContent]";
        }
        return RestLoggingEntry.getPayloadAsString(response.getContentAsByteArray(), response.getCharacterEncoding());
    }
}
