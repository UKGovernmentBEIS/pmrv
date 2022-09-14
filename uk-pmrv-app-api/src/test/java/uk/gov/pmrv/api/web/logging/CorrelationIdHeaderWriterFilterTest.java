package uk.gov.pmrv.api.web.logging;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.ServletException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CorrelationIdHeaderWriterFilterTest {
    @InjectMocks
    private CorrelationIdHeaderWriterFilter correlationIdHeaderWriterFilter;

    @Mock(answer = RETURNS_DEEP_STUBS)
    private SecurityProperties securityProperties;

    @Mock
    private CorrelationIdHeaderWriter correlationIdHeaderWriter;

    @Test
    void doFilterInternal() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest(HttpMethod.POST.name(), "uri");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = Mockito.spy(new MockFilterChain());

        correlationIdHeaderWriterFilter.doFilterInternal(request, response, filterChain);
        Mockito.verify(correlationIdHeaderWriter, Mockito.times(1)).writeHeaders(request, response);
    }

    @Test
    void getOrder() {
        when(securityProperties.getFilter().getOrder()).thenReturn(1);

        assertEquals(2, correlationIdHeaderWriterFilter.getOrder());
    }

}