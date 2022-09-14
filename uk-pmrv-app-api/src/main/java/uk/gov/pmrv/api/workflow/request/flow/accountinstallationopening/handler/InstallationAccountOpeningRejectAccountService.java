package uk.gov.pmrv.api.workflow.request.flow.accountinstallationopening.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.service.AccountRejectionService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;

@Service
@RequiredArgsConstructor
public class InstallationAccountOpeningRejectAccountService {
    private final AccountRejectionService accountRejectionService;
    private final RequestService requestService;

    public void execute(String requestId) {
    	Request request = requestService.findRequestById(requestId);
		accountRejectionService.rejectAccount(request.getAccountId());
    }
}
