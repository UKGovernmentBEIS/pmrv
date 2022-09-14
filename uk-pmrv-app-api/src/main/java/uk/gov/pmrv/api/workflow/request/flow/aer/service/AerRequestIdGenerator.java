package uk.gov.pmrv.api.workflow.request.flow.aer.service;

import org.springframework.stereotype.Service;

import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.aer.domain.AerRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestIdGenerator;

@Service
public class AerRequestIdGenerator implements RequestIdGenerator {

    @Override
    public String generate(RequestParams params) {
        Long accountId = params.getAccountId();
        AerRequestMetadata metaData = (AerRequestMetadata) params.getRequestMetadata();
        int year = metaData.getYear().getValue();

        return String.format("%s%d-%d", getPrefix(), accountId, year);
    }

    @Override
    public RequestType getType() {
        return RequestType.AER;
    }

    @Override
    public String getPrefix() {
        return "AEM";
    }
}
