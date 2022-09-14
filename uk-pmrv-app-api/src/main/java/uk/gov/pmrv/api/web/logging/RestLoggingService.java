package uk.gov.pmrv.api.web.logging;

import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static uk.gov.pmrv.api.web.logging.CorrelationIdHeaderWriter.CORRELATION_ID_HEADER;

@Log4j2
@Service
public class RestLoggingService {
    private final Level logLevel;
    private final RestLoggingProperties restLoggingProperties;
    private final CorrelationIdHeaderWriter correlationIdHeaderWriter;

    public RestLoggingService(RestLoggingProperties restLoggingProperties, CorrelationIdHeaderWriter correlationIdHeaderWriter) {
        this.correlationIdHeaderWriter = correlationIdHeaderWriter;
        this.restLoggingProperties = restLoggingProperties;
        logLevel = Level.valueOf(restLoggingProperties.getLevel().name());
    }

    public void logRestRequestResponse(HttpServletRequest request, HttpServletResponse response,
                                       RestLoggingEntry requestLog, RestLoggingEntry responseLog) {
        correlationIdHeaderWriter.writeHeaders(request, response);
        String correlationId = response.getHeader(CORRELATION_ID_HEADER);
        requestLog.setCorrelationId(correlationId);
        responseLog.setCorrelationId(correlationId);

        if (responseLog.getHttpStatus().isError()) {
            log.log(Level.ERROR, requestLog::toString);
            log.log(Level.ERROR, responseLog::toString);
        } else if (log.isEnabled(logLevel) && !isUriExcluded(requestLog.getUri())) {
            log.log(logLevel, requestLog::toString);
            log.log(logLevel, responseLog::toString);
        }
    }

    private boolean isUriExcluded(String uri) {
        List<Pattern> excludedUriPatterns = this.restLoggingProperties.getExcludedUriPatterns().stream()
                .map(Pattern::compile)
                .collect(Collectors.toList());

        for (Pattern pattern : excludedUriPatterns) {
            if (pattern.matcher(uri).find()) {
                return true;
            }
        }
        return false;
    }
}
