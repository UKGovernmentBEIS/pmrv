package uk.gov.pmrv.api.workflow.request.flow.permitnotification.service;

import org.springframework.stereotype.Service;

import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestSequenceRepository;
import uk.gov.pmrv.api.workflow.request.flow.common.service.AbstractRequestIdGenerator;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestIdGenerator;

@Service
public class PermitNotificationRequestIdGenerator extends AbstractRequestIdGenerator implements RequestIdGenerator {

    public PermitNotificationRequestIdGenerator(RequestSequenceRepository repository) {
        super(repository);
    }

    @Override
    public RequestType getType() {
        return RequestType.PERMIT_NOTIFICATION;
    }

    @Override
    public String getPrefix() {
        return "AEMN";
    }
}
