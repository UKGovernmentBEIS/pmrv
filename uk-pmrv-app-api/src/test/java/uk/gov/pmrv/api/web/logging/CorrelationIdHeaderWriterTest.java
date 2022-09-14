package uk.gov.pmrv.api.web.logging;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.gov.pmrv.api.web.logging.CorrelationIdHeaderWriter.CORRELATION_ID_HEADER;

class CorrelationIdHeaderWriterTest {
    private final CorrelationIdHeaderWriter correlationIdHeaderWriter = new CorrelationIdHeaderWriter();

    @Test
    void writeHeaders() {
        MockHttpServletRequest request = new MockHttpServletRequest(HttpMethod.POST.name(), "uri");
        MockHttpServletResponse response = new MockHttpServletResponse();

        correlationIdHeaderWriter.writeHeaders(request, response);
        assertTrue(response.containsHeader(CORRELATION_ID_HEADER));
    }
}