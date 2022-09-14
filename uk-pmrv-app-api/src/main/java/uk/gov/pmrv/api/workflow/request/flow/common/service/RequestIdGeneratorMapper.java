package uk.gov.pmrv.api.workflow.request.flow.common.service;

import java.util.List;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;

@Component
@RequiredArgsConstructor
public class RequestIdGeneratorMapper {
    private final List<RequestIdGenerator> requestIdGenerators;

    public RequestIdGenerator get(RequestType type) {
        return requestIdGenerators.stream()
            .filter(generator -> type.equals(generator.getType()))
            .findFirst()
            .orElseGet(retrieveFallbackGenerator());
    }

    private Supplier<RequestIdGenerator> retrieveFallbackGenerator() {
        return () -> requestIdGenerators.stream()
            .filter(g -> g.getType() == null)
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Could not retrieve fallback generator"));
    }
}
