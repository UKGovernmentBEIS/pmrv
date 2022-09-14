package uk.gov.pmrv.api.workflow.request.flow.accountinstallationopening.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestIdGenerator;

@Service
@RequiredArgsConstructor
public class InstallationAccountOpeningRequestIdGenerator implements RequestIdGenerator {

    @Override
    public String generate(final RequestParams params) {

        final Long accountId = params.getAccountId();
        return String.format("%s%05d", this.getPrefix(), accountId);
    }

    @Override
    public RequestType getType() {
        return RequestType.INSTALLATION_ACCOUNT_OPENING;
    }

    @Override
    public String getPrefix() {
        return "NEW";
    }
}
