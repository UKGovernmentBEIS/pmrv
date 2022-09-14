package uk.gov.pmrv.api.workflow.request.flow.permitrevocation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.service.AccountStatusService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;

@Service
@RequiredArgsConstructor
public class PermitRevokedService {

    private final RequestService requestService;
    private final AccountStatusService accountStatusService;

    public void executePermitRevokedPostActions(final String requestId) {

        final Request request = requestService.findRequestById(requestId);
        final Long accountId = request.getAccountId();

        // update account status
        accountStatusService.handlePermitRevoked(accountId);
    }
}
