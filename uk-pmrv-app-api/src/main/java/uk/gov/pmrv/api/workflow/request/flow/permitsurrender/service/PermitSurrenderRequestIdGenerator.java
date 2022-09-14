package uk.gov.pmrv.api.workflow.request.flow.permitsurrender.service;

import org.springframework.stereotype.Service;

import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestSequenceRepository;
import uk.gov.pmrv.api.workflow.request.flow.common.service.AbstractRequestIdGenerator;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestIdGenerator;

@Service
public class PermitSurrenderRequestIdGenerator extends AbstractRequestIdGenerator implements RequestIdGenerator {

    public PermitSurrenderRequestIdGenerator(RequestSequenceRepository repository) {
        super(repository);
    }

    @Override
    public RequestType getType() {
        return RequestType.PERMIT_SURRENDER;
    }

    @Override
    public String getPrefix() {
        return "AEMS";
    }
}
