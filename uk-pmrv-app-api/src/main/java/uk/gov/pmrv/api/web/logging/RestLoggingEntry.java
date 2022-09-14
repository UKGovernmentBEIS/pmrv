package uk.gov.pmrv.api.web.logging;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.util.ObjectUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

/**
 * Facilitator class for logging rest api request/response.
 */
@Data
@Builder
public class RestLoggingEntry {
    private RestLoggingEntryType type;
    @Builder.Default
    private final Map<String, String> headers = new ConcurrentHashMap<>();
    private String payload;
    private String uri;
    private String userId;
    private HttpMethod httpMethod;
    private HttpStatus httpStatus;
    private String correlationId;
    @Builder.Default
    @EqualsAndHashCode.Exclude
    private LocalDateTime timestamp = LocalDateTime.now();

    public enum RestLoggingEntryType {
        REQUEST,
        RESPONSE
    }

    @Override
    public String toString() {
        StringBuilder msg = new StringBuilder()
                .append(this.getType().toString())
                .append("[")
                .append("correlationId=").append(this.getCorrelationId())
                .append(", headers=").append(this.getHeaders())
                .append(", payload=").append(this.getPayload())
                .append(", timestamp=").append(this.getTimestamp());

        if (!ObjectUtils.isEmpty(this.getUri())) {
            msg.append(", uri=").append(this.getUri());
        }
        if (!ObjectUtils.isEmpty(this.getUserId())) {
            msg.append(", user=").append(this.getUserId());
        }
        if (!ObjectUtils.isEmpty(this.getHttpMethod())) {
            msg.append(", httpMethod=").append(this.getHttpMethod());
        }
        if (!ObjectUtils.isEmpty(this.getHttpStatus())) {
            msg.append(", httpStatus=").append(this.getHttpStatus());
        }
        msg.append("]");

        return msg.toString();
    }

    public static RestLoggingEntry getRequestRestLoggingEntry(String requestUri, HttpServletRequest request) {
        String user = request.getRemoteUser();
        HttpMethod httpMethod = HttpMethod.valueOf(request.getMethod());
        String payload = getRequestPayloadAsString(request);

        Map<String, String> headers = new HashMap<>();
        Collections.list(request.getHeaderNames()).stream()
                .filter(header -> !header.equalsIgnoreCase(HttpHeaders.AUTHORIZATION))
                .forEach(header -> headers.put(header, request.getHeader(header)));

        return RestLoggingEntry.builder()
                .type(RestLoggingEntry.RestLoggingEntryType.REQUEST)
                .headers(headers)
                .payload(payload)
                .uri(requestUri)
                .userId(user)
                .httpMethod(httpMethod)
                .build();
    }

    public static RestLoggingEntry getResponseRestLoggingEntry(HttpStatus httpStatus, String payload, HttpServletResponse response) {
        Map<String, String> headers = new HashMap<>();
        response.getHeaderNames()
                .forEach(header -> headers.put(header, response.getHeader(header)));

        return RestLoggingEntry.builder()
                .type(RestLoggingEntry.RestLoggingEntryType.RESPONSE)
                .headers(headers)
                .payload(payload)
                .httpStatus(httpStatus)
                .build();
    }

    private static String getRequestPayloadAsString(HttpServletRequest request) {
        try {
            if(request.getContentType() != null && request.getContentType().contains(MULTIPART_FORM_DATA_VALUE)) {
                Optional<Part> requestJsonPart = request.getParts().stream()
                        .filter(part -> part.getContentType().equals(APPLICATION_JSON_VALUE))
                        .findFirst();

                if(requestJsonPart.isPresent()) {
                    return getPayloadAsString(requestJsonPart.get().getInputStream().readAllBytes(), request.getCharacterEncoding());
                }

            } else {
                return getPayloadAsString(request.getInputStream().readAllBytes(), request.getCharacterEncoding());
            }
        } catch (IOException | ServletException ex) {
            return "[unknownContent]";
        }

        return null;
    }

    public static String getPayloadAsString(byte[] buffer, String characterEncoding) {
        if (buffer != null && buffer.length > 0) {
            try {
                return new String(buffer, characterEncoding);
            } catch (UnsupportedEncodingException ex) {
                return "[unknownContent]";
            }
        }
        return null;
    }
}
