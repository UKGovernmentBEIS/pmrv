package uk.gov.pmrv.api.workflow.request.flow.common.service;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestSequenceRepository;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;

/**
 * Default generator that uses the database sequence to generate request id.
 * Used when no generator found for a RequestType.
 */
@Service
public class FallbackRequestIdGenerator extends AbstractRequestIdGenerator implements RequestIdGenerator {

    private final RequestSequenceRepository repository;

    public FallbackRequestIdGenerator(RequestSequenceRepository repository) {
        super(repository);
        this.repository = repository;
    }

    @Override
    public String generate(RequestParams params) {
        return repository.getNextSequenceValue().toString();
    }

    @Override
    public RequestType getType() {
        return null;
    }

    @Override
    public String getPrefix() {
        return null;
    }
}
