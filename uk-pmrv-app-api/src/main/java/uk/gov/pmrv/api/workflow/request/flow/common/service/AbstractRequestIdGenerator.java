package uk.gov.pmrv.api.workflow.request.flow.common.service;

import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestSequence;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestSequenceRepository;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;

/**
 * Contains the 'default' implementation of the generate method, most generators will use it.
 */
@RequiredArgsConstructor
public abstract class AbstractRequestIdGenerator implements RequestIdGenerator {

    private final RequestSequenceRepository repository;

    @Override
    @Transactional
    public String generate(RequestParams params) {
        Long accountId = params.getAccountId();
        RequestType type = params.getType();

        RequestSequence requestSequence =
            repository.findByAccountIdAndType(accountId, type)
                .orElse(new RequestSequence(accountId, type));

        Long sequenceNo = requestSequence.incrementSequenceAndGet();
        String requestId = String.format("%s%d-%d", getPrefix(), accountId, sequenceNo);

        repository.save(requestSequence);
        return requestId;
    }
}
