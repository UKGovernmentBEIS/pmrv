package uk.gov.pmrv.api.web.logging;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.AdditionalAnswers.answerVoid;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestLoggingServiceTest {
    private static final String URI_EXCLUDED_PATTERN = "/api/*";
    private static final String REQUEST_URI = "/api/test";

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private RestLoggingEntry requestLog, responseLog;
    private Logger logger;
    private List<LogEvent> capturedLogEvents;

    @InjectMocks
    private RestLoggingService restLoggingService;

    @Spy
    private RestLoggingProperties restLoggingProperties;

    @Mock
    private CorrelationIdHeaderWriter correlationIdHeaderWriter;

    @Mock
    private Appender mockedAppender;

    @BeforeEach
    public void setUp() {
        request = new MockHttpServletRequest(HttpMethod.POST.name(), REQUEST_URI);
        response = new MockHttpServletResponse();
        requestLog = RestLoggingEntry.builder()
                .type(RestLoggingEntry.RestLoggingEntryType.REQUEST)
                .headers(Map.of())
                .payload("payload")
                .uri(REQUEST_URI)
                .userId("user")
                .httpMethod(HttpMethod.POST)
                .build();

        responseLog = RestLoggingEntry.builder()
                .type(RestLoggingEntry.RestLoggingEntryType.RESPONSE)
                .headers(Map.of())
                .payload("payload")
                .httpStatus(HttpStatus.ACCEPTED)
                .build();
        initLogger();
        capturedLogEvents = new ArrayList<>();
    }

    @AfterEach
    public void tearDown() {
        logger.removeAppender(mockedAppender);
    }

    @Test
    void doNotLogWhenUriExcludedDoNotLog() {
        when(restLoggingProperties.getExcludedUriPatterns()).thenReturn(List.of(URI_EXCLUDED_PATTERN));

        restLoggingService.logRestRequestResponse(request, response, requestLog, responseLog);

        Mockito.verify(mockedAppender, Mockito.never()).append(any());
    }

    @Test
    void doNotLogWhenLevelNotEnabled() {
        logger.setLevel(Level.DEBUG);

        restLoggingService.logRestRequestResponse(request, response, requestLog, responseLog);

        Mockito.verify(mockedAppender, Mockito.never()).append(any());
    }

    @Test
    void alwaysLogErrors() {
        responseLog.setHttpStatus(HttpStatus.BAD_REQUEST);

        when(mockedAppender.isStarted()).thenReturn(true);
        doAnswer(answerVoid((LogEvent event) -> capturedLogEvents.add(event.toImmutable())))
                .when(this.mockedAppender).append(any());

        restLoggingService.logRestRequestResponse(request, response, requestLog, responseLog);

        assertEquals(2, capturedLogEvents.size());
        LogEvent requestLogEvent = capturedLogEvents.get(0);
        assertTrue(requestLogEvent.getMessage().getFormattedMessage().contains("payload"));

        LogEvent responseLogEvent = capturedLogEvents.get(1);
        assertTrue(responseLogEvent.getMessage().getFormattedMessage().contains(HttpStatus.BAD_REQUEST.toString()));

        assertEquals(Level.ERROR, requestLogEvent.getLevel());
        Mockito.verify(correlationIdHeaderWriter, Mockito.times(1)).writeHeaders(request, response);
    }

    @Test
    void logSuccessWhenLevelIsEnabled() {
        when(mockedAppender.isStarted()).thenReturn(true);
        doAnswer(answerVoid((LogEvent event) -> capturedLogEvents.add(event.toImmutable())))
                .when(this.mockedAppender).append(any());

        restLoggingService.logRestRequestResponse(request, response, requestLog, responseLog);

        assertEquals(2, capturedLogEvents.size());
        LogEvent requestLogEvent = capturedLogEvents.get(0);
        assertTrue(requestLogEvent.getMessage().getFormattedMessage().contains("payload"));

        LogEvent responseLogEvent = capturedLogEvents.get(1);
        assertTrue(responseLogEvent.getMessage().getFormattedMessage().contains(HttpStatus.ACCEPTED.toString()));

        assertEquals(Level.INFO, requestLogEvent.getLevel());
        Mockito.verify(correlationIdHeaderWriter, Mockito.times(1)).writeHeaders(request, response);
    }

    private void initLogger() {
        when(mockedAppender.getName()).thenReturn("MockAppender");
        logger = (Logger) LogManager.getLogger(RestLoggingService.class);
        logger.addAppender(this.mockedAppender);
        logger.setLevel(Level.INFO);
    }


}