package uk.gov.pmrv.api.web.logging;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.web.servlet.filter.OrderedFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CorrelationIdHeaderWriterFilter extends OncePerRequestFilter implements OrderedFilter {
    private final SecurityProperties securityProperties;
    private final CorrelationIdHeaderWriter correlationIdHeaderWriter;

    @Override
    public int getOrder() {
        return securityProperties.getFilter().getOrder() + 1;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        correlationIdHeaderWriter.writeHeaders(request, response);
        filterChain.doFilter(request, response);
    }
}
