package uk.gov.pmrv.api.web.logging;

import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.ServletException;
import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
class RestLoggingFilterTest {
    private static final String REQUEST_URI = "/api/test";

    @InjectMocks
    private RestLoggingFilter restLoggingFilter;

    @Mock
    private RestLoggingService restLoggingService;

    private MockFilterChain filterChain;

    @BeforeEach
    public void setUp() {
        filterChain = Mockito.spy(new MockFilterChain());
    }

    @Test
    void doFilterInternalLog() throws IOException, ServletException {
        MockHttpServletRequest request = new MockHttpServletRequest(HttpMethod.POST.name(), REQUEST_URI);
        request.addHeader("header", "value");
        request.addHeader(HttpHeaders.AUTHORIZATION, "value");
        request.setRemoteUser("user");
        RestLoggingEntry requestLog = RestLoggingEntry.getRequestRestLoggingEntry(REQUEST_URI, request);

        MockHttpServletResponse response = new MockHttpServletResponse();
        response.addHeader("header", "value");
        response.setStatus(HttpStatus.ACCEPTED.value());
        RestLoggingEntry responseLog = RestLoggingEntry.getResponseRestLoggingEntry(HttpStatus.ACCEPTED, null, response);

        restLoggingFilter.doFilterInternal(request, response, filterChain);
        Mockito.verify(restLoggingService, Mockito.times(1)).logRestRequestResponse(any(), any(), eq(requestLog), eq(responseLog));
        Mockito.verify(filterChain, Mockito.times(1)).doFilter(any(), any());
    }
}